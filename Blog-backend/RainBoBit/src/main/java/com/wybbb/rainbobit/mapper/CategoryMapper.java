package com.wybbb.rainbobit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wybbb.rainbobit.pojo.entity.Category;
import org.apache.ibatis.annotations.Mapper;


/**
 * 分类表(Category)表数据库访问层
 *
 * @author Ra1nbot
 * @since 2025-07-25 10:34:47
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    int relateToArticle(Long categoryId);
}

