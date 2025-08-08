package com.wybbb.rainbobit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wybbb.rainbobit.pojo.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 角色信息表(SysRole)表数据库访问层
 *
 * @author Ra1nbot
 * @since 2025-08-01 14:26:14
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
    List<Role> selectByUserId(Long userId);

    void saveRolesByUserId(Long userId, List<Long> roleIds);

    void deleteByUserId(Long userId);
}

