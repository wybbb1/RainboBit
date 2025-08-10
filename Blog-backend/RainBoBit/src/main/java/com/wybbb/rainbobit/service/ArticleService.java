package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.dto.ArticleDTO;
import com.wybbb.rainbobit.pojo.entity.Article;
import com.wybbb.rainbobit.pojo.other.ArticleDetail;
import com.wybbb.rainbobit.pojo.vo.ArticleListVO;
import com.wybbb.rainbobit.pojo.vo.HotArticleVO;

import java.util.List;

/**
 * 文章表(Article)表服务接口
 *
 * @author makejava
 * @since 2025-07-24 15:14:18
 */
public interface ArticleService extends IService<Article> {

    List<HotArticleVO> hotAricleList();

    PageResult<ArticleListVO> articleList(Long categoryId, PageQuery pageQuery);

    ArticleDetail articleDetail(Long id);

    void updateViewCount(Long id);

    void add(ArticleDTO articleDTO);

    PageResult<Article> listArticle(PageQuery pageQuery, String title, String summary);

    void update(ArticleDetail articleDetail);

    void delete(Long id);

    PageResult<ArticleListVO> listDeletedArticles(PageQuery pageQuery, String title, String summary);

    void restoreArticle(Long id);

    void permanentDeleteArticle(Long id);

    void batchRestoreArticles(java.util.List<Long> ids);

    void batchPermanentDeleteArticles(java.util.List<Long> ids);
}
