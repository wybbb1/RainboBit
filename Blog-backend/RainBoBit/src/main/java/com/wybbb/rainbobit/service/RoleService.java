package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.dto.RoleDTO;
import com.wybbb.rainbobit.pojo.dto.RoleStatusDTO;
import com.wybbb.rainbobit.pojo.entity.Role;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.vo.RoleListVO;
import com.wybbb.rainbobit.pojo.vo.RoleVO;

import java.util.List;


/**
 * 角色信息表(SysRole)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-08-01 14:26:14
 */
public interface RoleService extends IService<Role> {

    List<String> selectRoleKeyByUserId(Long id);

    PageResult<RoleListVO> pageRole(PageQuery pageQuery, String roleName, String status);

    void changeStatus(RoleStatusDTO roleStatusDTO);

    void add(RoleDTO roleDTO);

    RoleVO getRoleById(Long id);

    void updateRole(RoleDTO roleDTO);

    void delete(Long id);

    List<RoleVO> listAllRole();
}

