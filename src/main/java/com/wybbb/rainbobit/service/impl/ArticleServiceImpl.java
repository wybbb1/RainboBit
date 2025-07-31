package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.ArticleConstants;
import com.wybbb.rainbobit.common.constants.CategoryConstants;
import com.wybbb.rainbobit.common.constants.SystemConstants;
import com.wybbb.rainbobit.common.utils.RedisCacheHelper;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.mapper.ArticleMapper;
import com.wybbb.rainbobit.pojo.PageQuery;
import com.wybbb.rainbobit.pojo.PageResult;
import com.wybbb.rainbobit.pojo.entity.Article;
import com.wybbb.rainbobit.pojo.entity.Category;
import com.wybbb.rainbobit.pojo.vo.ArticleDetailVO;
import com.wybbb.rainbobit.pojo.vo.ArticleListVO;
import com.wybbb.rainbobit.pojo.vo.CategoryVO;
import com.wybbb.rainbobit.pojo.vo.HotArticleVO;
import com.wybbb.rainbobit.service.ArticleService;
import com.wybbb.rainbobit.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 文章表(Article)表服务实现类
 *
 * @author Ra1nbot
 * @since 2025-07-24 15:14:19
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private RedisCacheHelper redisCacheHelper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private CategoryService categoryService;

    @Override
    public List<HotArticleVO> hotAricleList() {

        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, ArticleConstants.ARTICLE_STATUS_NORMAL) // 0表示正常状态
                .eq(Article::getDelFlag, ArticleConstants.ARTICLE_STATUS_NOT_DELETED)
                .orderByDesc(Article::getViewCount); // 按照浏览量降序

        Page<Article> page = new Page<>(1, ArticleConstants.HOT_ARTICLES_SHOW_PER_PAGE);
        page(page, queryWrapper);

        List<Article> articles = page.getRecords();
        if (articles.isEmpty()) {
            return new ArrayList<>();
        }

        return BeanUtil.copyToList(articles, HotArticleVO.class);
    }

    @Transactional
    @Override
    public PageResult<ArticleListVO> articleList(Long categoryId, PageQuery pageQuery) {

        LambdaQueryWrapper<Article> articleQueryWrapper = new LambdaQueryWrapper<>();
        articleQueryWrapper.eq(Article::getStatus, ArticleConstants.ARTICLE_STATUS_NORMAL) // 0表示正常状态
                .eq(Article::getDelFlag, ArticleConstants.ARTICLE_STATUS_NOT_DELETED) // 0表示未删除状态
                .eq(Objects.nonNull(categoryId) && categoryId > 0, Article::getCategoryId, categoryId) // 如果categoryId不为null且大于0，则添加分类条件
                .orderByDesc(Article::getIsTop) // 先按照置顶状态降序
                .orderByDesc(Article::getCreateTime); // 再按照创建时间降序

        Page<Article> articlePage = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
        page(articlePage, articleQueryWrapper);

        // 获取所有分类信息
        Map<Long, String> finalEntries = redisCacheHelper.getMap(
                CategoryConstants.CATEGORY_CACHE_KEY,
                Long.class,
                String.class,
                () -> {
                    // 如果缓存中不存在，从数据库查询
                    LambdaQueryWrapper<Category> categoryQueryWrapper = new LambdaQueryWrapper<>();
                    categoryQueryWrapper.eq(Category::getDelFlag, CategoryConstants.CATEGORY_NOT_DELETED)
                            .gt(Category::getRefer_cnt, CategoryConstants.CATEGORY_NO_REFERENCE);
                    List<Category> categories = categoryService.list(categoryQueryWrapper);

                    // 将查询结果转换为VO对象
                    List<CategoryVO> categoryVOs = BeanUtil.copyToList(categories, CategoryVO.class);

                    return categoryVOs.stream()
                            .collect(Collectors.toMap(
                                    CategoryVO::getId,
                                    CategoryVO::getName,
                                    (existing, replacement) -> existing));// 如果有重复键，保留第一个
                });

        if (finalEntries.isEmpty()) {
            throw new RuntimeException(SystemConstants.RUNTIME_ERROR);
        }
        // 将分类信息转换为Map
        List<ArticleListVO> articleListVOS = BeanUtil.copyToList(articlePage.getRecords(), ArticleListVO.class);
        // 遍历文章列表，设置分类名称
        articleListVOS.forEach(articleListVO -> {
            articleListVO.setCategoryName(finalEntries.get(articleListVO.getCategoryId()));
        });

        return new PageResult<>(articlePage.getTotal(), articleListVOS);
    }

    @Override
    public ArticleDetailVO articleDetail(Long id) {
        Article article = articleMapper.selectById(id);
        Long viewCount = redisCacheHelper.getCacheMapValue(
                ArticleConstants.VIEW_COUNT_CACHE_KEY, id.toString(), Long.class);
        article.setViewCount(viewCount);

        if (Integer.parseInt(article.getStatus()) != ArticleConstants.ARTICLE_STATUS_NORMAL ||
                article.getDelFlag() != ArticleConstants.ARTICLE_STATUS_NOT_DELETED
        ) {
            throw new SystemException(ArticleConstants.ARTICLE_STATUS_ERROR);
        }

        ArticleDetailVO articleDetailVO = BeanUtil.copyProperties(article, ArticleDetailVO.class);
        // 获取文章分类名称
        Object categoryName = stringRedisTemplate.opsForHash().get(CategoryConstants.CATEGORY_CACHE_KEY, article.getCategoryId().toString());
        if (Objects.isNull(categoryName)){
            categoryName = categoryService.getById(article.getCategoryId()).getName();
        }
        articleDetailVO.setCategoryName(categoryName.toString());

        return articleDetailVO;
    }

    @Override
    public void updateViewCount(Long id) {
        redisCacheHelper.incrementCacheMapValue(ArticleConstants.VIEW_COUNT_CACHE_KEY, id.toString(), 1L);
    }
}
