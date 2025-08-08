package com.wybbb.rainbobit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wybbb.rainbobit.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;


/**
 * 用户表(SysUser)表数据库访问层
 *
 * @author Ra1nbot
 * @since 2025-07-26 11:11:33
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}

