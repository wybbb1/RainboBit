package com.wybbb.rainbobit.controller.admin;

import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.pojo.dto.ArticleDTO;
import com.wybbb.rainbobit.pojo.other.ArticleDetail;
import com.wybbb.rainbobit.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员文章管理", description = "管理员文章管理相关接口")
@PreAuthorize("@ps.hasPermission('content:article:list')")
@RestController
@RequestMapping("/content/article")
public class AdminArticleController {
    @Resource
    private ArticleService articleService;

    @Operation(summary = "添加文章", description = "管理员添加新文章")
    @PostMapping
    public ResponseResult add(
            @Parameter(description = "文章信息") @RequestBody ArticleDTO articleDTO) {
        articleService.add(articleDTO);
        return ResponseResult.okResult();
    }

    @Operation(summary = "获取文章列表", description = "分页获取文章列表，支持标题和摘要搜索")
    @GetMapping("/list")
    public ResponseResult list(
            @Parameter(description = "分页参数") PageQuery pageQuery, 
            @Parameter(description = "文章标题") String title, 
            @Parameter(description = "文章摘要") String summary) {
        return ResponseResult.okResult(articleService.listArticle(pageQuery, title, summary));
    }

    @Operation(summary = "获取文章详情", description = "根据文章ID获取文章详细信息")
    @GetMapping("/{id}")
    public ResponseResult getArticleDetail(
            @Parameter(description = "文章ID") @PathVariable Long id) {
        return ResponseResult.okResult(articleService.articleDetail(id));
    }

    @Operation(summary = "更新文章", description = "更新文章信息")
    @PutMapping
    public ResponseResult update(
            @Parameter(description = "文章详情") @RequestBody ArticleDetail articleDetail) {
        articleService.update(articleDetail);
        return ResponseResult.okResult();
    }

    @Operation(summary = "删除文章", description = "根据文章ID删除文章")
    @DeleteMapping("/{id}")
    public ResponseResult delete(
            @Parameter(description = "文章ID") @PathVariable Long id) {
        articleService.delete(id);
        return ResponseResult.okResult();
    }
}
