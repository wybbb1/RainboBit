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

    // ========== 查询操作 ==========
    
    /**
     * 根据用户ID查询角色权限标识
     * @param id 用户ID
     * @return 角色权限标识列表
     */
    List<String> selectRoleKeyByUserId(Long id);

    /**
     * 分页查询角色列表
     * @param pageQuery 分页参数
     * @param roleName 角色名称
     * @param status 角色状态
     * @return 分页结果
     */
    PageResult<RoleListVO> pageRole(PageQuery pageQuery, String roleName, String status);

    /**
     * 根据ID查询角色详情
     * @param id 角色ID
     * @return 角色详情
     */
    RoleVO getRoleById(Long id);

    /**
     * 查询所有角色列表
     * @return 角色列表
     */
    List<RoleVO> listAllRole();

    // ========== 添加操作 ==========
    
    /**
     * 添加角色
     * @param roleDTO 角色信息
     */
    void add(RoleDTO roleDTO);

    // ========== 更新操作 ==========
    
    /**
     * 修改角色状态
     * @param roleStatusDTO 角色状态信息
     */
    void changeStatus(RoleStatusDTO roleStatusDTO);

    /**
     * 更新角色信息
     * @param roleDTO 角色信息
     */
    void updateRole(RoleDTO roleDTO);

    // ========== 删除操作 ==========
    
    /**
     * 删除角色
     * @param id 角色ID
     */
    void delete(Long id);
}

