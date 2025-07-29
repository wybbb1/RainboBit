package com.wybbb.rainbobit.controller;

import com.wybbb.rainbobit.common.constants.CommentConstant;
import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.pojo.PageQuery;
import com.wybbb.rainbobit.pojo.ResponseResult;
import com.wybbb.rainbobit.pojo.entity.Comment;
import com.wybbb.rainbobit.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import static com.baomidou.mybatisplus.extension.toolkit.Db.save;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    @GetMapping("/commentList")
    public ResponseResult commentList(Long articleId,PageQuery pageQuery){
        return ResponseResult.okResult(commentService.commentList(CommentConstant.ARTICLE_COMMENT, articleId, pageQuery));
    }
    @GetMapping("/linkCommentList")
    public ResponseResult linkCommentList(PageQuery pageQuery){
        return ResponseResult.okResult(commentService.commentList(CommentConstant.LINK_COMMENT, null, pageQuery));
    }

    @PostMapping
    public ResponseResult addComment(@RequestBody Comment comment) {
        commentService.addComment(comment);
        return ResponseResult.okResult();
    }
}
