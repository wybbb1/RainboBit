package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.constants.ArticleConstants;
import com.wybbb.rainbobit.mapper.ArticleMapper;
import com.wybbb.rainbobit.pojo.ResponseResult;
import com.wybbb.rainbobit.pojo.entity.Article;
import com.wybbb.rainbobit.pojo.vo.HotArticleVO;
import com.wybbb.rainbobit.service.ArticleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 文章表(Article)表服务实现类
 *
 * @author Ra1nbot
 * @since 2025-07-24 15:14:19
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Override
    public ResponseResult<List<HotArticleVO>> hotAricleList() {

        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, ArticleConstants.ARTICLE_STATUS_NORMAL) // 0表示正常状态
                .eq(Article::getDelFlag, ArticleConstants.ARTICLE_STATUS_NOT_DELETED)
                .orderByDesc(Article::getViewCount); // 按照浏览量降序

        Page<Article> page = new Page<>(1, ArticleConstants.HOT_ARTICLES_SHOW_PER_PAGE);
        page(page, queryWrapper);

        List<Article> articles = page.getRecords();
        if (articles.isEmpty()) {
            return ResponseResult.okResult(new ArrayList<>());
        }

        List<HotArticleVO> hotArticleVOS = BeanUtil.copyToList(articles, HotArticleVO.class);

        return ResponseResult.okResult(hotArticleVOS);
    }
}
