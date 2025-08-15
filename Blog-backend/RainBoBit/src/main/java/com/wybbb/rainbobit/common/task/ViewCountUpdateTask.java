package com.wybbb.rainbobit.common.task;

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
import com.wybbb.rainbobit.service.ArticleService;
import com.wybbb.rainbobit.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ViewCountUpdateTask {

    @Resource
    private ArticleService articleService;
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private RedisCacheHelper redisCacheHelper;

    @Scheduled(cron = "0/10 * * * * *")
    public void updateViewCount(){
        Map<String, String> cacheMap = redisCacheHelper.getCacheMap(ArticleConstants.VIEW_COUNT_CACHE_KEY, String.class, String.class);
        List<Article> list = cacheMap.entrySet().stream()
                .map(entry -> new Article( Long.valueOf(entry.getKey()), Long.valueOf(entry.getValue())))
                .toList();

        articleService.updateBatchById(list);

        Map<String, String> statisticsMap = createStatisticsMap(cacheMap);
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
        /*statisticsMap.put(StatisticsConstants.TODAY_VIEW_COUNT,
                redisCacheHelper.getCacheMapValue(StatisticsConstants.STATISTICS_CACHE_KEY, StatisticsConstants.TODAY_VIEW_COUNT, Long.class));*/
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

    @Scheduled(cron = "0 0 8 * * ?")
    public void todayViewCountReset() {
        // 每天早上8点重置浏览量
        redisCacheHelper.deleteMapValue(StatisticsConstants.STATISTICS_CACHE_KEY, StatisticsConstants.TODAY_VIEW_COUNT);
        redisCacheHelper.setCacheMapValue(StatisticsConstants.STATISTICS_CACHE_KEY, StatisticsConstants.TODAY_VIEW_COUNT, Long.class, 0L);
    }
}
