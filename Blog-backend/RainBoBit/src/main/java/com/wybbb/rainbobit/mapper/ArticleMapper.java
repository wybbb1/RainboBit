package com.wybbb.rainbobit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wybbb.rainbobit.pojo.entity.Article;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}
