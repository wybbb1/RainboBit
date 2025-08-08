package com.wybbb.rainbobit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wybbb.rainbobit.pojo.entity.Link;
import org.apache.ibatis.annotations.Mapper;


/**
 * 友链(Link)表数据库访问层
 *
 * @author Ra1nbot
 * @since 2025-07-25 17:52:07
 */
@Mapper
public interface LinkMapper extends BaseMapper<Link> {

}

