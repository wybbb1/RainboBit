package com.wybbb.rainbobit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wybbb.rainbobit.pojo.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 标签(Tag)表数据库访问层
 *
 * @author Ra1nbot
 * @since 2025-07-31 17:32:48
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    void saveTagsBatch(Long articleId, List<Long> tagIds);

    Long relateToArticle(Long id);

    void deleteByArticleId(Long id);

    List<Long> getTagsBatch(Long articleId);
}

