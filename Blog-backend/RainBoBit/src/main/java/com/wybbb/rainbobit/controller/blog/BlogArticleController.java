package com.wybbb.rainbobit.controller.blog;

import com.wybbb.rainbobit.common.constants.ArticleConstants;
import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.pojo.other.ArticleDetail;
import com.wybbb.rainbobit.pojo.vo.HotArticleVO;
import com.wybbb.rainbobit.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Tag(name = "文章", description = "文章相关接口")
@RestController
@RequestMapping("/article")
public class BlogArticleController {

    @Resource
    private ArticleService articleService;

    @Operation(summary = "获取文章列表", description = "分页获取文章列表，可按分类筛选")
    @GetMapping("/articleList")
    public ResponseResult<?> page(
            @Parameter(description = "分类ID") Long categoryId, 
            @Parameter(description = "分页参数") PageQuery pageQuery) {
        return ResponseResult.okResult(articleService.page(categoryId, pageQuery));
    }

    @Operation(summary = "获取文章详情", description = "根据文章ID获取文章详细信息")
    @GetMapping("/{id}")
    public ResponseResult<?> articleDetail(
            @Parameter(description = "文章ID") @PathVariable Long id) {
        ArticleDetail article = articleService.articleDetail(id);
        if (Objects.isNull(article)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.ARTICLE_NOT_EXIST);
        }
        return ResponseResult.okResult(articleService.articleDetail(id));
    }

    @Operation(summary = "更新文章浏览量", description = "增加文章的浏览次数")
    @PutMapping("/updateViewCount/{id}")
    public ResponseResult<?> updateViewCount(
            @Parameter(description = "文章ID") @PathVariable Long id) {
        if (Objects.isNull(id)) {
            throw new SystemException(ArticleConstants.PARAM_INVALID);
        }
        articleService.updateViewCount(id);
        return ResponseResult.okResult();
    }

}
