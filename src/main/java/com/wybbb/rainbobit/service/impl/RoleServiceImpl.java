package com.wybbb.rainbobit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.mapper.RoleMapper;
import com.wybbb.rainbobit.pojo.entity.Role;
import com.wybbb.rainbobit.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色信息表(SysRole)表服务实现类
 *
 * @author Ra1nbot
 * @since 2025-08-01 14:26:14
 */
@Service("sysRoleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Override
    public List<String> selectRoleKeyByUserId(Long id) {
        if (id == 1L){
            // 超级管理员拥有所有权限
            List<String> roleKeys = new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }

        // 其他用户的角色权限查询逻辑
        List<Role> roles = roleMapper.selectByUserId(id);
        return roles.stream()
                .map(Role::getRoleKey)
                .filter(roleKey -> roleKey != null && !roleKey.isEmpty())
                .toList();
    }
}

