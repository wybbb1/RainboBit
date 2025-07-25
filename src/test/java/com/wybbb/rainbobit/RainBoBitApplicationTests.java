package com.wybbb.rainbobit;

import com.wybbb.rainbobit.common.utils.R2OssUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
class RainBoBitApplicationTests {

    @Resource
    private R2OssUtil r2OssUtil;

    @Test
    void uploadFileTest() throws InterruptedException {
        File file = new File("C:\\Users\\95887\\Desktop\\post\\takeout\\img\\cache.png");
        String fileName = "test/cache.png";
        String url = r2OssUtil.upload(file, fileName);
        System.out.println("Uploaded file URL: " + url);

        // 验证文件是否存在
        r2OssUtil.delete(fileName);
        System.out.println("File deleted successfully.");
    }

}
