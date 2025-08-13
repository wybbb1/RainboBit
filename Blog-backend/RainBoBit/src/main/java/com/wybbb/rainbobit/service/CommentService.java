package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.entity.Comment;
import com.wybbb.rainbobit.pojo.vo.CommentListVO;
import com.wybbb.rainbobit.pojo.vo.CommentVO;

import java.util.List;


/**
 * 评论表(Comment)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-07-28 20:47:27
 */
public interface CommentService extends IService<Comment> {

    // ========== 查询操作 ==========
    
    /**
     * 分页查询评论列表
     * @param type 评论类型
     * @param articleId 文章ID
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<CommentVO> commentList(int type, Long articleId, PageQuery pageQuery);

    List<CommentListVO> getRecentComments(int limit);

    // ========== 添加操作 ==========
    
    /**
     * 添加评论
     * @param comment 评论信息
     */
    void addComment(Comment comment);

    // ========== 更新操作 ==========
    
    

    // ========== 删除操作 ==========
    
    // TODO: 添加评论删除相关方法
}

