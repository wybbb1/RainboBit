package com.wybbb.rainbobit.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.CommentConstant;
import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.mapper.CommentMapper;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.entity.Comment;
import com.wybbb.rainbobit.pojo.vo.CommentVO;
import com.wybbb.rainbobit.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author Ra1nbot
 * @since 2025-07-28 20:47:27
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Transactional
    @Override
    public PageResult<CommentVO> commentList(int type, Long articleId, PageQuery pageQuery) {
        if (type != CommentConstant.ARTICLE_COMMENT && type != CommentConstant.LINK_COMMENT) {
            throw new SystemException(CommentConstant.INVALID_TYPE);
        }

        Page<CommentVO> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
        Page<CommentVO> rootCommentVOList = commentMapper.listRootComment(page, articleId, type);

        if (rootCommentVOList.getRecords() != null) {
            for (CommentVO commentVO : rootCommentVOList.getRecords()) {
                // 获取根评论的子评论
                List<CommentVO> childCommentVOList = commentMapper.listChildComment(commentVO.getId());
                commentVO.setChildren(childCommentVOList);
            }
        }

        return new PageResult<>(
                rootCommentVOList.getTotal(),
                rootCommentVOList.getRecords()
        );
    }

    @Override
    public void addComment(Comment comment) {
        if (comment.getContent() == null || comment.getContent().isBlank()){
            throw new SystemException(CommentConstant.CONTENT_IS_NULL);
        }
        save(comment);
    }

}

