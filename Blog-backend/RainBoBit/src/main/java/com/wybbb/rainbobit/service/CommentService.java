package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.entity.Comment;


/**
 * 评论表(Comment)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-07-28 20:47:27
 */
public interface CommentService extends IService<Comment> {

    PageResult commentList(int type, Long articleId, PageQuery pageQuery);

    void addComment(Comment comment);
}

