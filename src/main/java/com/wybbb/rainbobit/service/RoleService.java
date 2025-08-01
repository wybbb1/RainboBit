package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.entity.Role;

import java.util.List;


/**
 * 角色信息表(SysRole)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-08-01 14:26:14
 */
public interface RoleService extends IService<Role> {

    List<String> selectRoleKeyByUserId(Long id);
}

