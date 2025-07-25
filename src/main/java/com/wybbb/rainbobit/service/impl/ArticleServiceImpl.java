package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.constants.ArticleConstants;
import com.wybbb.rainbobit.constants.CategoryConstants;
import com.wybbb.rainbobit.mapper.ArticleMapper;
import com.wybbb.rainbobit.pojo.PageResult;
import com.wybbb.rainbobit.pojo.ResponseResult;
import com.wybbb.rainbobit.pojo.entity.Article;
import com.wybbb.rainbobit.pojo.vo.ArticleListVO;
import com.wybbb.rainbobit.pojo.vo.CategoryVO;
import com.wybbb.rainbobit.pojo.vo.HotArticleVO;
import com.wybbb.rainbobit.service.ArticleService;
import com.wybbb.rainbobit.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Override
    public PageResult<ArticleListVO> articleList(Integer page, Integer pageSize, Long categoryId) {

        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, ArticleConstants.ARTICLE_STATUS_NORMAL) // 0表示正常状态
                .eq(Article::getDelFlag, ArticleConstants.ARTICLE_STATUS_NOT_DELETED) // 0表示未删除状态
                .eq(Objects.nonNull(categoryId) && categoryId > 0, Article::getCategoryId, categoryId) // 如果categoryId不为null且大于0，则添加分类条件
                .orderByDesc(Article::getIsTop) // 先按照置顶状态降序
                .orderByDesc(Article::getCreateTime); // 再按照创建时间降序

        Page<Article> articlePage = new Page<>(page, pageSize);
        page(articlePage, queryWrapper);

        // 获取所有分类信息
        List<CategoryVO> categoryVOS = categoryService.listCategory();
        Map<Long, String> categoryMap = categoryVOS.stream()
                .collect(Collectors.toMap(CategoryVO::getId, CategoryVO::getName));
        // 如果分类信息不存在，则从数据库中获取,但是几乎不可能会需要查询数据库
        // 为Article添加categoryName属性
        List<Article> articles = articlePage.getRecords().stream()
                .map(article -> {
                    String name = categoryMap.get(article.getCategoryId());
                    if (Objects.isNull(name) || name.isBlank()) {
                        name = "未分类";
                    }
                    article.setCategoryName(name);
                    return article;
                }).toList();

        List<ArticleListVO> articleListVOS = BeanUtil.copyToList(articles, ArticleListVO.class);
        PageResult<ArticleListVO> pageResult = new PageResult<>(articlePage.getTotal(), articleListVOS);

        return pageResult;
    }
}
