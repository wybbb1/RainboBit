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

    // ========================== 查询相关 ==========================
    
    /**
     * 获取文章列表
     */
    PageResult<ArticleListVO> page(Long categoryId, PageQuery pageQuery);

    /**
     * 获取文章详情
     */
    ArticleDetail articleDetail(Long id);

    /**
     * 获取文章列表（管理端）
     */
    PageResult<Article> adminPage(PageQuery pageQuery, String title, String summary);

    /**
     * 获取已删除文章列表
     */
    PageResult<ArticleListVO> listDeletedArticles(PageQuery pageQuery, String title, String summary);

    // ========================== 添加相关 ==========================
    
    /**
     * 添加文章
     */
    void add(ArticleDTO articleDTO);

    // ========================== 更新相关 ==========================
    
    /**
     * 更新浏览量
     */
    void updateViewCount(Long id);

    /**
     * 更新文章
     */
    void update(ArticleDetail articleDetail);

    /**
     * 恢复文章
     */
    void restoreArticle(Long id);

    /**
     * 批量恢复文章
     */
    void batchRestoreArticles(List<Long> ids);

    // ========================== 删除相关 ==========================
    
    /**
     * 软删除文章
     */
    void delete(Long id);

    /**
     * 永久删除文章
     */
    void permanentDeleteArticle(Long id);

    /**
     * 批量永久删除文章
     */
    void batchPermanentDeleteArticles(List<Long> ids);
}
