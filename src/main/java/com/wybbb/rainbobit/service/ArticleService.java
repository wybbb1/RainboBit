package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.PageQuery;
import com.wybbb.rainbobit.pojo.PageResult;
import com.wybbb.rainbobit.pojo.ResponseResult;
import com.wybbb.rainbobit.pojo.entity.Article;
import com.wybbb.rainbobit.pojo.vo.ArticleDetailVO;
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

    ArticleDetailVO articleDetail(Long id);
}
