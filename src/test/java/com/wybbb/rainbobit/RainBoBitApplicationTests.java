package com.wybbb.rainbobit;

import com.wybbb.rainbobit.common.utils.R2OssUtil;
import com.wybbb.rainbobit.pojo.entity.Menu;
import com.wybbb.rainbobit.service.MenuService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;

@SpringBootTest
class RainBoBitApplicationTests {

    @Resource
    private R2OssUtil r2OssUtil;
    @Resource
    private MenuService menuService;

    @Test
    void uploadFileTest() throws InterruptedException {
        File file = new File("C:\\Users\\95887\\Desktop\\post\\takeout\\img\\cache.png");
        String fileName = "test/cache.png";
        String url = r2OssUtil.upload(file);
        System.out.println("Uploaded file URL: " + url);

        // 验证文件是否存在
        r2OssUtil.delete(fileName);
        System.out.println("File deleted successfully.");
    }




}
