package com.wybbb.rainbobit.controller;

import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import com.wybbb.rainbobit.pojo.ResponseResult;
import com.wybbb.rainbobit.pojo.entity.Article;
import com.wybbb.rainbobit.pojo.vo.ArticleDetailVO;
import com.wybbb.rainbobit.pojo.vo.HotArticleVO;
import com.wybbb.rainbobit.service.ArticleService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @GetMapping("/hotArticleList")
    public ResponseResult<List<HotArticleVO>> hotAricleList() {
        return ResponseResult.okResult(articleService.hotAricleList());
    }

    @GetMapping("/articleList")
    public ResponseResult articleList(Integer page, Integer pageSize, Long categoryId) {
        return ResponseResult.okResult(articleService.articleList(page, pageSize, categoryId));
    }

    @GetMapping("/{id}")
    public ResponseResult articleDetail(@PathVariable Long id) {
        ArticleDetailVO article = articleService.articleDetail(id);
        if (Objects.isNull(article)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.ARTICLE_NOT_EXIST);
        }
        return ResponseResult.okResult(articleService.articleDetail(id));
    }

}
