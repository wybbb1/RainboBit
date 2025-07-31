package com.wybbb.rainbobit.common.config;

import com.wybbb.rainbobit.common.constants.ArticleConstants;
import com.wybbb.rainbobit.common.utils.RedisCacheHelper;
import com.wybbb.rainbobit.mapper.ArticleMapper;
import com.wybbb.rainbobit.pojo.entity.Article;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ViewCountRunner implements CommandLineRunner {

    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private RedisCacheHelper redisCacheHelper;

    @Override
    public void run(String... args) throws Exception {
        Map<String, String> map = articleMapper.selectList(null).stream()
                .collect(Collectors.toMap(
                        article -> article.getId().toString(),
                        article -> article.getViewCount().toString()
                ));

        log.info("Initializing view count cache with data: {}", map);

        redisCacheHelper.setCacheMap(ArticleConstants.VIEW_COUNT_CACHE_KEY, map);
    }
}
