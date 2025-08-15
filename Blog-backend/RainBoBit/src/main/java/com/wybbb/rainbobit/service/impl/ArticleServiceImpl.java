package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.ArticleConstants;
import com.wybbb.rainbobit.common.constants.StatisticsConstants;
import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.common.utils.RedisCacheHelper;
import com.wybbb.rainbobit.common.utils.SecurityUtils;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.mapper.ArticleMapper;
import com.wybbb.rainbobit.mapper.TagMapper;
import com.wybbb.rainbobit.pojo.dto.ArticleDTO;
import com.wybbb.rainbobit.pojo.entity.Article;
import com.wybbb.rainbobit.pojo.other.ArticleDetail;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.vo.ArticleListVO;
import com.wybbb.rainbobit.service.ArticleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


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
    private TagMapper tagMapper;
    @Resource
    private RedisCacheHelper redisCacheHelper;

    @Transactional
    @Override
    public PageResult<ArticleListVO> page(Long categoryId, PageQuery pageQuery) {

        LambdaQueryWrapper<Article> articleQueryWrapper = new LambdaQueryWrapper<>();
        articleQueryWrapper.eq(Article::getStatus, ArticleConstants.STATUS_NORMAL) // 0表示正常状态
                .eq(Article::getDelFlag, ArticleConstants.NOT_DELETED) // 0表示未删除状态
                .eq(Objects.nonNull(categoryId) && categoryId > 0, Article::getCategoryId, categoryId) // 如果categoryId不为null且大于0，则添加分类条件
                .orderByDesc(Article::getIsTop) // 先按照置顶状态降序
                .orderByDesc(Article::getCreateTime); // 再按照创建时间降序

        Page<Article> articlePage = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
        page(articlePage, articleQueryWrapper);

        // 将分类信息转换为Map
        List<ArticleListVO> articleListVOS = BeanUtil.copyToList(articlePage.getRecords(), ArticleListVO.class);
        // 遍历文章列表，设置分类名称和标签id信息
        articleListVOS.forEach(articleListVO -> {
            articleListVO.setTagIds(tagMapper.getTagsBatch(articleListVO.getId()));
        });

        return new PageResult<>(articlePage.getTotal(), articleListVOS);
    }

    @Override
    public ArticleDetail articleDetail(Long id) {
        Article article = articleMapper.selectById(id);
        Long viewCount = redisCacheHelper.getCacheMapValue(
                ArticleConstants.VIEW_COUNT_CACHE_KEY, id.toString(), Long.class);
        if (Objects.isNull(viewCount)) {
            // 如果缓存中没有浏览量，则从数据库获取
            viewCount = article.getViewCount();
            Map<String, String> viewCountMap = new HashMap<>();
            viewCountMap.put(id.toString(), viewCount.toString());
            // 同时更新缓存
            redisCacheHelper.setCacheMap(ArticleConstants.VIEW_COUNT_CACHE_KEY, viewCountMap);
        }
        article.setViewCount(viewCount);

        if (!article.getDelFlag().toString().equals(ArticleConstants.NOT_DELETED)) {
            throw new SystemException(ArticleConstants.STATUS_ERROR);
        }

        ArticleDetail articleDetail = BeanUtil.copyProperties(article, ArticleDetail.class);

        // 获取文章标签
        List<Long> tags = tagMapper.getTagsBatch(id);
        articleDetail.setTagIds(tags);

        return articleDetail;
    }

    @Override
    public void updateViewCount(Long id) {
        // 更新文章浏览量
        redisCacheHelper.incrementCacheMapValue(ArticleConstants.VIEW_COUNT_CACHE_KEY, id.toString(), 1L);
        // 更新今日浏览量
        redisCacheHelper.incrementCacheMapValue(StatisticsConstants.STATISTICS_CACHE_KEY, StatisticsConstants.TODAY_VIEW_COUNT, 1L);
    }

    @Transactional
    @Override
    public void add(ArticleDTO articleDTO) {
        Article article = BeanUtil.copyProperties(articleDTO, Article.class);
        save(article);

        Long id = article.getId();
        List<Long> tagIds = articleDTO.getTags();

        if (!tagIds.isEmpty()){
            tagMapper.saveTagsBatch(id, tagIds);
        }
    }

    @Override
    public PageResult<Article> adminPage(PageQuery pageQuery, String title, String summary) {
        Long id = SecurityUtils.getUserId();

        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .like(Objects.nonNull(title) && !title.isBlank(), Article::getTitle, title)
                .like(Objects.nonNull(summary) && !summary.isBlank(), Article::getSummary, summary)
                .eq(Article::getDelFlag, ArticleConstants.NOT_DELETED)
                .orderByDesc(Article::getCreateTime); // 按照创建时间降序
        if (id != UserConstants.ADMIN_LOGIN){
            queryWrapper.eq(Article::getCreateBy, id); // 如果不是管理员，则只查询当前用户的文章
        }

        Page<Article> articlePage = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
        page(articlePage, queryWrapper);

        return new PageResult<>(articlePage.getTotal(), articlePage.getRecords());
    }

    @Transactional
    @Override
    public void update(ArticleDetail articleDetail) {
        Article article = BeanUtil.copyProperties(articleDetail, Article.class);
        updateById(article);

        // 更新文章标签
        Long id = article.getId();
        List<Long> tagIds = articleDetail.getTagIds();
        tagMapper.deleteByArticleId(id);

        if (!tagIds.isEmpty()){
            tagMapper.saveTagsBatch(id, tagIds);
        }
    }

    @Transactional
    @Override
    public void delete(Long id) {
        // 逻辑删除文章
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Article::getDelFlag, ArticleConstants.DELETED)
                .eq(Article::getId, id);
        update(updateWrapper);
    }

    @Override
    public PageResult<ArticleListVO> listDeletedArticles(PageQuery pageQuery, String title, String summary) {
        LambdaQueryWrapper<Article> articleQueryWrapper = new LambdaQueryWrapper<>();
        articleQueryWrapper
                .eq(Article::getDelFlag, ArticleConstants.DELETED) // 0表示未删除状态
                .orderByDesc(Article::getIsTop) // 先按照置顶状态降序
                .orderByDesc(Article::getCreateTime); // 再按照创建时间降序

        Page<Article> articlePage = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
        page(articlePage, articleQueryWrapper);

        List<ArticleListVO> articleListVOS = BeanUtil.copyToList(articlePage.getRecords(), ArticleListVO.class);
        // 遍历文章列表，设置分类名称和标签id信息
        articleListVOS.forEach(articleListVO -> {
            articleListVO.setTagIds(tagMapper.getTagsBatch(articleListVO.getId()));
        });

        return new PageResult<>(articlePage.getTotal(), articleListVOS);
    }

    @Override
    public List<ArticleListVO> getRecentArticles(int limit) {
        // 获取近一个月的文章
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getDelFlag, ArticleConstants.NOT_DELETED) // 0表示未删除状态
                .orderByDesc(Article::getCreateTime) // 按照创建时间降序
                .last("LIMIT " + limit); // 限制数量

        List<Article> articles = list(queryWrapper);
        return BeanUtil.copyToList(articles, ArticleListVO.class).stream()
                .peek(articleListVO -> {
                    // 设置标签id
                    articleListVO.setTagIds(tagMapper.getTagsBatch(articleListVO.getId()));
                })
                .toList();
    }

    @Override
    public void restoreArticle(Long id) {
        // 恢复文章（逻辑删除标记改为未删除）
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Article::getDelFlag, ArticleConstants.NOT_DELETED)
                .eq(Article::getId, id);
        update(updateWrapper);

    }

    @Transactional
    @Override
    public void permanentDeleteArticle(Long id) {
        // 永久删除文章（物理删除）
        removeById(id);
        // 删除文章标签关联
        tagMapper.deleteByArticleId(id);
    }

    @Override
    public void batchRestoreArticles(java.util.List<Long> ids) {
        // 批量恢复文章
        if (ids != null && !ids.isEmpty()) {
            LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.set(Article::getDelFlag, ArticleConstants.NOT_DELETED)
                    .in(Article::getId, ids);
            update(updateWrapper);
        }
    }

    @Transactional
    @Override
    public void batchPermanentDeleteArticles(java.util.List<Long> ids) {
        // 批量永久删除文章
        if (ids != null && !ids.isEmpty()) {
            // 批量物理删除文章
            removeByIds(ids);
            // 批量删除文章标签关联
            for (Long id : ids) {
                tagMapper.deleteByArticleId(id);
            }
        }
    }
}
