package com.wybbb.rainbobit.common.utils;

import com.wybbb.rainbobit.common.constants.SystemConstants;
import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.common.prop.R2OssProperties;
import com.wybbb.rainbobit.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class R2OssUtil {

    private final R2OssProperties r2OssProperties;
    private final S3Client s3Client;

    public R2OssUtil(R2OssProperties r2OssProperties) {
        this.r2OssProperties = r2OssProperties;

        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                r2OssProperties.getAccessKeyId(),
                r2OssProperties.getAccessKeySecret()
        );
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        this.s3Client = S3Client.builder()
                .region(Region.of(r2OssProperties.getRegion())) // e.g., "auto", "wnam", "enam"
                .credentialsProvider(credentialsProvider)
                .endpointOverride(URI.create(r2OssProperties.getEndpoint())) // e.g., https://<ACCOUNT_ID>.r2.cloudflarestorage.com
                .build();
        log.info("R2 OSS Client initialized with endpoint: {} and bucket: {}", r2OssProperties.getEndpoint(), r2OssProperties.getBucketName());
    }

    public String uploadIMG(MultipartFile img) {
        String originalFilename = img.getOriginalFilename();

        if (originalFilename != null && !originalFilename.endsWith(".png") && !originalFilename.endsWith(".jpg")) {
            throw new SystemException(SystemConstants.FILE_TYPE_ERROR);
        }

        // 1. 获取文件基本信息
        long size = img.getSize();
        String contentType = img.getContentType();
        // 2. 生成文件名
        String objectName = img.getOriginalFilename();
        // 3. 上传文件到R2
        try (InputStream inputStream = img.getInputStream()) {
            String fileUrl = upload(inputStream, objectName, size, contentType);

            if (fileUrl != null) {
                log.info("文件上传成功: {}，访问URL: {}", originalFilename, fileUrl);
                return fileUrl;
            } else {
                log.error("文件上传失败: {}，R2OssUtil未能返回URL。", originalFilename);
                // R2OssUtil 内部应该已经记录了更详细的S3Exception日志
                throw new SystemException(SystemConstants.FILE_UPLOAD_ERROR);
            }
        } catch (Exception e) {
            log.error("文件上传时发生未知错误: {} : {}", originalFilename, e.getMessage(), e);
            throw new SystemException(SystemConstants.FILE_UPLOAD_ERROR);
        }
    }

    /**
     * 上传文件到 R2，并返回可公开访问的 URL。
     *
     * @param file       要上传的本地文件
     * 如果为 null 或空，则会生成一个基于 UUID 的随机文件名，并保留原始扩展名。
     * @return 上传成功后文件的公开访问 URL，失败则返回 null。
     */
    public String upload(File file) {
        if (file == null || !file.exists()) {
            log.error("Upload failed: File is null or does not exist.");
            return null;
        }

        String key = generateUniqueObjectName(file.getName());

        String contentType;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            log.warn("Failed to determine content type for file: {}, defaulting to octet-stream", file.getName(), e);
            contentType = "application/octet-stream"; // 默认类型
        }
        if (contentType == null) { // probeContentType 可能返回 null
            contentType = "application/octet-stream";
        }


        try (InputStream inputStream = new FileInputStream(file)) {
            return upload(inputStream, key, file.length(), contentType);
        } catch (IOException e) {
            log.error("Error opening file input stream for {}: {}", file.getName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 上传文件流到 R2，并返回可公开访问的 URL。
     *
     * @param inputStream 要上传的文件输入流
     * @param objectName  在存储桶中存储的对象名称 (例如 "data/report.pdf")。不允许为 null 或空。
     * @param contentLength 输入流的长度。对于 InputStream，提供长度可以优化上传。
     * @param contentType 文件的 MIME 类型 (例如 "image/jpeg", "application/pdf")。
     * @return 上传成功后文件的公开访问 URL，失败则返回 null。
     */
    public String upload(InputStream inputStream, String objectName, long contentLength, String contentType) {
        if (inputStream == null) {
            log.error("Upload failed: InputStream is null.");
            return null;
        }
        if (objectName == null || objectName.trim().isEmpty()) {
            log.error("Upload failed: Object name cannot be null or empty for InputStream upload.");
            return null;
        }
        if (contentType == null || contentType.trim().isEmpty()) {
            log.warn("Content type is null or empty for object: {}, defaulting to octet-stream", objectName);
            contentType = "application/octet-stream";
        }

        objectName = generateUniqueObjectName(objectName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(r2OssProperties.getBucketName())
                .key(objectName)
                .contentType(contentType)
                // .acl(ObjectCannedACL.PUBLIC_READ) // 如果你的 R2 存储桶策略允许，并且你想设置为公共可读
                .build();

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
            log.info("File uploaded successfully to R2: {}/{}", r2OssProperties.getBucketName(), objectName);

            // 构建公开访问 URL
            // 确保 domain 不以 / 结尾，objectName 不以 / 开头，以避免双斜杠
            String domain = r2OssProperties.getDomain();
            if (domain.endsWith("/")) {
                domain = domain.substring(0, domain.length() - 1);
            }
            String keyForUrl = objectName;
            if (keyForUrl.startsWith("/")) {
                keyForUrl = keyForUrl.substring(1);
            }
            // 通常 R2 的公开访问域名会自动处理 https，如果你的 domain 配置不带协议，可能需要添加
            // 例如，如果 domain 是 "myr2.example.com"，则 URL 是 "https://myr2.example.com/objectKey"
            // 如果 domain 已经是 "https://myr2.example.com"，则直接拼接
            if (!domain.toLowerCase().startsWith("http://") && !domain.toLowerCase().startsWith("https://")) {
                domain = "https://" + domain;
            }

            return domain + "/" + keyForUrl;

        } catch (S3Exception e) {
            log.error("Error uploading file to R2: {} (AWS Error Code: {})", e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode(), e);
        } catch (Exception e) {
            log.error("Unexpected error uploading file to R2: {}", e.getMessage(), e);
        } finally {
            // 注意：此方法不负责关闭传入的 inputStream，调用者应负责关闭
        }
        return null;
    }


    /**
     * 从 R2 下载文件。
     *
     * @param objectName 要下载的对象名称/键
     * @return 包含对象数据的 ResponseInputStream，如果下载失败则返回 null。
     * 调用者负责关闭此 InputStream。
     */
    public ResponseInputStream<GetObjectResponse> download(String objectName) {
        if (objectName == null || objectName.trim().isEmpty()) {
            log.error("Download failed: Object name cannot be null or empty.");
            return null;
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(r2OssProperties.getBucketName())
                .key(objectName)
                .build();
        try {
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getObjectRequest);
            log.info("File {} downloaded successfully from R2 bucket {}", objectName, r2OssProperties.getBucketName());
            return response;
        } catch (S3Exception e) {
            log.error("Error downloading file {} from R2: {} (AWS Error Code: {})", objectName, e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode(), e);
        } catch (Exception e) {
            log.error("Unexpected error downloading file {} from R2: {}", objectName, e.getMessage(), e);
        }
        return null;
    }

    /**
     * 删除 R2 中的对象。
     *
     * @param objectName 要删除的对象名称/键
     * @return true 如果删除成功或对象不存在，false 如果发生错误。
     */
    public boolean delete(String objectName) {
        if (objectName == null || objectName.trim().isEmpty()) {
            log.error("Delete failed: Object name cannot be null or empty.");
            return false;
        }
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(r2OssProperties.getBucketName())
                .key(objectName)
                .build();
        try {
            s3Client.deleteObject(deleteObjectRequest);
            log.info("File {} deleted successfully from R2 bucket {}", objectName, r2OssProperties.getBucketName());
            return true;
        } catch (S3Exception e) {
            log.error("Error deleting file {} from R2: {} (AWS Error Code: {})", objectName, e.awsErrorDetails().errorMessage(), e.awsErrorDetails().errorCode(), e);
        } catch (Exception e) {
            log.error("Unexpected error deleting file {} from R2: {}", objectName, e.getMessage(), e);
        }
        return false;
    }


    /**
     * 生成一个唯一的对象名称，保留原始文件的扩展名。
     * @param originalFilename 原始文件名
     * @return 基于 UUID 的唯一对象名
     */
    private String generateUniqueObjectName(String originalFilename) {
        //根据日期生成路径
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/");
        String datePath = sdf.format(new Date());
        //uuid作为文件名
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //后缀和文件后缀一致
        int index = originalFilename.lastIndexOf(".");
        // test.jpg -> .jpg
        String fileType = originalFilename.substring(index);
        return new StringBuilder().append(datePath).append(uuid).append(fileType).toString();
    }

}