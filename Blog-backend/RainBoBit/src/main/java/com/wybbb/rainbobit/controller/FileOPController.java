package com.wybbb.rainbobit.controller;

import com.wybbb.rainbobit.common.constants.SystemConstants;
import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.common.utils.R2OssUtil;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件操作", description = "文件上传相关接口")
@RestController
public class FileOPController {

    @Resource
    private R2OssUtil r2OssUtil;

    @Operation(summary = "上传图片", description = "上传图片文件到云存储")
    @PostMapping("/upload")
    public ResponseResult<?> upload(
            @Parameter(description = "图片文件") MultipartFile img) {
        if (img == null || img.isEmpty()) {
            throw new SystemException(SystemConstants.FILE_IS_NULL);
        }
        String url = r2OssUtil.uploadIMG(img);
        return ResponseResult.okResult(url);
    }
}
