package com.wybbb.rainbobit;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.wybbb.rainbobit.common.prop.AIProperties;
import com.wybbb.rainbobit.common.utils.R2OssUtil;
import com.wybbb.rainbobit.pojo.entity.Article;
import com.wybbb.rainbobit.pojo.entity.Menu;
import com.wybbb.rainbobit.service.ArticleService;
import com.wybbb.rainbobit.service.Assistant;
import com.wybbb.rainbobit.service.MenuService;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.PartialThinking;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.BeforeToolExecution;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootTest
class RainBoBitApplicationTests {

    @Resource
    private R2OssUtil r2OssUtil;
    @Resource
    private ElasticsearchOperations elasticsearchOperations;
    @Resource
    private ArticleService articleService;

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

    @Test
    void ESTest() throws InterruptedException {
        IndexOperations indexOps = elasticsearchOperations.indexOps(Article.class);

        // 如果索引不存在，则创建
        if (!indexOps.exists()) {
            System.out.println("索引 'article' 不存在，正在创建...");
            indexOps.create();
            // 自动将实体类的@Field注解映射到索引
            indexOps.putMapping(indexOps.createMapping(Article.class));
            System.out.println("索引 'article' 创建成功!");
        } else {
            System.out.println("索引 'article' 已经存在。");
        }

        Article article = articleService.getById(13L);
        elasticsearchOperations.save(article);
    }

}
