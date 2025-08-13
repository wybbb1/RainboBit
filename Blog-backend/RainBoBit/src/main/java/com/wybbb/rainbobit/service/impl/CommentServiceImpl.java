package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.CommentConstants;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.mapper.CommentMapper;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.entity.Comment;
import com.wybbb.rainbobit.pojo.vo.CommentListVO;
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
        if (type != CommentConstants.ARTICLE_COMMENT && type != CommentConstants.LINK_COMMENT) {
            throw new SystemException(CommentConstants.INVALID_TYPE);
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
    public List<CommentListVO> getRecentComments(int limit) {
        // 获取近一个月的评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId, CommentConstants.ROOT_COMMENT) // 根评论
                .orderByDesc(Comment::getCreateTime) // 按照创建时间降序
                .last("LIMIT " + limit); // 限制数量

        List<Comment> comments = list(queryWrapper);
        return BeanUtil.copyToList(comments, CommentListVO.class);
    }

    @Override
    public void addComment(Comment comment) {
        if (comment.getContent() == null || comment.getContent().isBlank()){
            throw new SystemException(CommentConstants.CONTENT_IS_NULL);
        }
        save(comment);
    }

}

