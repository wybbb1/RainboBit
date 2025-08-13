package com.wybbb.rainbobit.common.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wybbb.rainbobit.common.constants.ArticleConstants;
import com.wybbb.rainbobit.common.constants.StatisticsConstants;
import com.wybbb.rainbobit.common.utils.RedisCacheHelper;
import com.wybbb.rainbobit.mapper.ArticleMapper;
import com.wybbb.rainbobit.mapper.CategoryMapper;
import com.wybbb.rainbobit.mapper.CommentMapper;
import com.wybbb.rainbobit.mapper.TagMapper;
import com.wybbb.rainbobit.pojo.entity.Article;
import com.wybbb.rainbobit.pojo.entity.Comment;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CommandRunner implements CommandLineRunner {

    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private RedisCacheHelper redisCacheHelper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private CommentMapper commentMapper;

    @Override
    public void run(String... args) throws Exception {
        // 每篇文章浏览量
        Map<String, String> map = articleMapper.selectList(null).stream()
                .collect(Collectors.toMap(
                        article -> article.getId().toString(),
                        article -> article.getViewCount().toString()
                ));

        // 统计数据
        Map<String, String> statisticsMap = createStatisticsMap(map);

        log.info("Initializing statistics data in Redis...");

        redisCacheHelper.setCacheMap(ArticleConstants.VIEW_COUNT_CACHE_KEY, map);
        redisCacheHelper.setCacheMap(StatisticsConstants.STATISTICS_CACHE_KEY, statisticsMap);
    }

    public Map<String, String> createStatisticsMap(Map<String, String> map) {
        Map<String, Long> statisticsMap = new HashMap<>();
        // 文章总数
        statisticsMap.put(StatisticsConstants.ARTICLE_COUNT, (long) map.size());
        // 分类总数
        statisticsMap.put(StatisticsConstants.CATEGORY_COUNT,
                categoryMapper.selectCount(null));
        // 标签总数
        statisticsMap.put(StatisticsConstants.TAG_COUNT,
                tagMapper.selectCount(null));
        // 文章浏览量总和
        statisticsMap.put(StatisticsConstants.VIEW_COUNT,
                map.values().stream()
                        .mapToLong(Long::parseLong)
                        .sum());
        // 今日浏览量
        statisticsMap.put(StatisticsConstants.TODAY_VIEW_COUNT,
                0L);
        // 评论总数
        statisticsMap.put(StatisticsConstants.COMMENT_COUNT,
                commentMapper.selectCount(null));
        // 本月新增文章
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ge(Article::getCreateTime, LocalDate.now().minusMonths(1));
        statisticsMap.put(StatisticsConstants.MONTHLY_ARTICLE_COUNT, articleMapper.selectCount(queryWrapper));
        // 本月新增评论
        LambdaQueryWrapper<Comment> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.ge(Comment::getCreateTime, LocalDate.now().minusMonths(1));
        statisticsMap.put(StatisticsConstants.MONTHLY_COMMENT_COUNT, commentMapper.selectCount(queryWrapper1));

        return statisticsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().toString()));
    }
}
