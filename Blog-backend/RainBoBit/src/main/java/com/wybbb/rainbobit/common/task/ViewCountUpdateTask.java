package com.wybbb.rainbobit.common.task;

import com.wybbb.rainbobit.common.constants.ArticleConstants;
import com.wybbb.rainbobit.common.utils.RedisCacheHelper;
import com.wybbb.rainbobit.mapper.ArticleMapper;
import com.wybbb.rainbobit.pojo.entity.Article;
import com.wybbb.rainbobit.service.ArticleService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ViewCountUpdateTask {

    @Resource
    private ArticleService articleService;
    @Resource
    private RedisCacheHelper redisCacheHelper;

    @Scheduled(cron = "0/10 * * * * *")
    public void updateViewCount(){
        List<Article> list = redisCacheHelper.getCacheMap(ArticleConstants.VIEW_COUNT_CACHE_KEY, Long.class, Long.class).entrySet().stream()
                .map(entry -> new Article(entry.getKey(), entry.getValue()))
                .toList();

        articleService.updateBatchById(list);
    }
}
