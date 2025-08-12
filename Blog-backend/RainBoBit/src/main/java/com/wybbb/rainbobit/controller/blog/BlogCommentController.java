package com.wybbb.rainbobit.controller.blog;

import com.wybbb.rainbobit.common.constants.CommentConstant;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.pojo.entity.Comment;
import com.wybbb.rainbobit.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评论", description = "文章评论相关接口")
@RestController
@RequestMapping("/comment")
public class BlogCommentController {

    @Resource
    private CommentService commentService;

    @Operation(summary = "获取文章评论列表", description = "分页获取指定文章的评论列表")
    @GetMapping("/commentList")
    public ResponseResult<?> commentList(
            @Parameter(description = "文章ID") Long articleId,
            @Parameter(description = "分页参数") PageQuery pageQuery){
        return ResponseResult.okResult(commentService.commentList(CommentConstant.ARTICLE_COMMENT, articleId, pageQuery));
    }
    
    @Operation(summary = "获取友链评论列表", description = "分页获取友链评论列表")
    @GetMapping("/linkCommentList")
    public ResponseResult<?> linkCommentList(
            @Parameter(description = "分页参数") PageQuery pageQuery){
        return ResponseResult.okResult(commentService.commentList(CommentConstant.LINK_COMMENT, null, pageQuery));
    }

    @Operation(summary = "添加评论", description = "添加文章评论或友链评论")
    @PostMapping
    public ResponseResult<?> addComment(
            @Parameter(description = "评论信息") @RequestBody Comment comment) {
        commentService.addComment(comment);
        return ResponseResult.okResult();
    }
}
