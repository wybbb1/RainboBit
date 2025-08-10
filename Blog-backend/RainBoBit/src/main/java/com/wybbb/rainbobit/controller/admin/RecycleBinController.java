package com.wybbb.rainbobit.controller.admin;

import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.service.ArticleService;
import com.wybbb.rainbobit.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "管理员回收站管理", description = "管理员回收站管理相关接口")
@PreAuthorize("@ps.hasPermission('content:recyclebin:list')")
@RestController
@RequestMapping("/content/recyclebin")
public class RecycleBinController {
    
    @Resource
    private ArticleService articleService;
    
    @Resource
    private LinkService linkService;

    @Operation(summary = "查询回收站文章列表", description = "分页查询已删除的文章列表")
    @PreAuthorize("@ps.hasPermission('content:recyclebin:query')")
    @GetMapping("/articles")
    public ResponseResult<?> listDeletedArticles(
            @Parameter(description = "分页参数") PageQuery pageQuery,
            @Parameter(description = "文章标题") String title,
            @Parameter(description = "文章摘要") String summary) {
        return ResponseResult.okResult(articleService.listDeletedArticles(pageQuery, title, summary));
    }

    @Operation(summary = "查询回收站友链列表", description = "分页查询已删除的友链列表")
    @PreAuthorize("@ps.hasPermission('content:recyclebin:query')")
    @GetMapping("/links")
    public ResponseResult<?> listDeletedLinks(
            @Parameter(description = "分页参数") PageQuery pageQuery,
            @Parameter(description = "友链名称") String name,
            @Parameter(description = "友链网址") String address) {
        return ResponseResult.okResult(linkService.listDeletedLinks(pageQuery, name, address));
    }

    @Operation(summary = "恢复文章", description = "从回收站恢复指定文章")
    @PreAuthorize("@ps.hasPermission('content:recyclebin:restore')")
    @PutMapping("/articles/{id}/restore")
    public ResponseResult<?> restoreArticle(@Parameter(description = "文章ID") @PathVariable Long id) {
        articleService.restoreArticle(id);
        return ResponseResult.okResult();
    }

    @Operation(summary = "恢复友链", description = "从回收站恢复指定友链")
    @PreAuthorize("@ps.hasPermission('content:recyclebin:restore')")
    @PutMapping("/links/{id}/restore")
    public ResponseResult<?> restoreLink(@Parameter(description = "友链ID") @PathVariable Long id) {
        linkService.restoreLink(id);
        return ResponseResult.okResult();
    }

    @Operation(summary = "永久删除文章", description = "永久删除回收站中的指定文章")
    @PreAuthorize("@ps.hasPermission('content:recyclebin:delete')")
    @DeleteMapping("/articles/{id}")
    public ResponseResult<?> permanentDeleteArticle(@Parameter(description = "文章ID") @PathVariable Long id) {
        articleService.permanentDeleteArticle(id);
        return ResponseResult.okResult();
    }

    @Operation(summary = "永久删除友链", description = "永久删除回收站中的指定友链")
    @PreAuthorize("@ps.hasPermission('content:recyclebin:delete')")
    @DeleteMapping("/links/{id}")
    public ResponseResult<?> permanentDeleteLink(@Parameter(description = "友链ID") @PathVariable Long id) {
        linkService.permanentDeleteLink(id);
        return ResponseResult.okResult();
    }

    @Operation(summary = "批量恢复文章", description = "批量恢复回收站中的文章")
    @PreAuthorize("@ps.hasPermission('content:recyclebin:restore')")
    @PutMapping("/articles/batch-restore/{ids}")
    public ResponseResult<?> batchRestoreArticles(@Parameter(description = "文章ID列表，用逗号分隔") @PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        articleService.batchRestoreArticles(idList);
        return ResponseResult.okResult();
    }

    @Operation(summary = "批量恢复友链", description = "批量恢复回收站中的友链")
    @PreAuthorize("@ps.hasPermission('content:recyclebin:restore')")
    @PutMapping("/links/batch-restore/{ids}")
    public ResponseResult<?> batchRestoreLinks(@Parameter(description = "友链ID列表，用逗号分隔") @PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        linkService.batchRestoreLinks(idList);
        return ResponseResult.okResult();
    }

    @Operation(summary = "批量永久删除文章", description = "批量永久删除回收站中的文章")
    @PreAuthorize("@ps.hasPermission('content:recyclebin:delete')")
    @DeleteMapping("/articles/batch-delete/{ids}")
    public ResponseResult<?> batchPermanentDeleteArticles(@Parameter(description = "文章ID列表，用逗号分隔") @PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        articleService.batchPermanentDeleteArticles(idList);
        return ResponseResult.okResult();
    }

    @Operation(summary = "批量永久删除友链", description = "批量永久删除回收站中的友链")
    @PreAuthorize("@ps.hasPermission('content:recyclebin:delete')")
    @DeleteMapping("/links/batch-delete/{ids}")
    public ResponseResult<?> batchPermanentDeleteLinks(@Parameter(description = "友链ID列表，用逗号分隔") @PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        linkService.batchPermanentDeleteLinks(idList);
        return ResponseResult.okResult();
    }
}
