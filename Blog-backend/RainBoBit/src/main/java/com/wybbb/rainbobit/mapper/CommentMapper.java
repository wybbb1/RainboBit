package com.wybbb.rainbobit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wybbb.rainbobit.pojo.entity.Comment;
import com.wybbb.rainbobit.pojo.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 评论表(Comment)表数据库访问层
 *
 * @author Ra1nbot
 * @since 2025-07-28 20:47:27
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    Page<CommentVO> listRootComment(Page<CommentVO> page, Long articleId, int type);

    List<CommentVO> listChildComment(Long rootId);
}

