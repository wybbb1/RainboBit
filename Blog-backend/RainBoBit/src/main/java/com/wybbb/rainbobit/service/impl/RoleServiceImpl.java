package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.ArticleConstants;
import com.wybbb.rainbobit.common.constants.RoleConstants;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.mapper.MenuMapper;
import com.wybbb.rainbobit.mapper.RoleMapper;
import com.wybbb.rainbobit.pojo.dto.RoleDTO;
import com.wybbb.rainbobit.pojo.dto.RoleStatusDTO;
import com.wybbb.rainbobit.pojo.entity.Article;
import com.wybbb.rainbobit.pojo.entity.Role;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.vo.RoleListVO;
import com.wybbb.rainbobit.pojo.vo.RoleVO;
import com.wybbb.rainbobit.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Resource
    private MenuMapper menuMapper;


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

    @Override
    public PageResult<RoleListVO> pageRole(PageQuery pageQuery, String roleName, String status) {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(roleName != null && !roleName.isEmpty(), Role::getRoleName, roleName)
                .eq(status != null && !status.isEmpty(), Role::getStatus, status)
                .eq(Role::getDelFlag, RoleConstants.NOT_DELETED)
                .orderByDesc(Role::getCreateTime)
                .orderByAsc(Role::getRoleSort);


        Page<Role> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
        page(page, queryWrapper);

        return new PageResult<>(
                page.getTotal(),
                BeanUtil.copyToList(page.getRecords(), RoleListVO.class)
        );
    }

    @Transactional
    @Override
    public void changeStatus(RoleStatusDTO roleStatusDTO) {
        Long roleId = roleStatusDTO.getRoleId();
        String status = roleStatusDTO.getStatus();

        Role role = roleMapper.selectById(roleId);

        if (role == null) {
            throw new SystemException(RoleConstants.NOT_EXIST);
        }
        if (RoleConstants.ADMIN_ROLE_KEY.equals(role.getRoleKey())) {
            throw new SystemException(RoleConstants.ADMIN_ROLE_CANNOT_CHANGE_STATUS);
        }
        if (!RoleConstants.STATUS_NORMAL.equals(status) && !RoleConstants.STATUS_DISABLE.equals(status)) {
            throw new SystemException(RoleConstants.INVALID_ROLE_STATUS);
        }

        role.setStatus(status);
        roleMapper.updateById(role);
    }

    @Override
    public void add(RoleDTO roleDTO) {
        Role role = BeanUtil.copyProperties(roleDTO, Role.class);
        save(role);

        if (roleDTO.getMenuIds() != null && !roleDTO.getMenuIds().isEmpty()) {
            // 如果有菜单权限，保存角色与菜单的关联关系
            menuMapper.saveMenusByRoleId(role.getId(), roleDTO.getMenuIds());
        }
    }

    @Override
    public RoleVO getRoleById(Long id) {
        Role role = getById(id);
        return BeanUtil.copyProperties(role, RoleVO.class);
    }

    @Override
    public void updateRole(RoleDTO roleDTO) {
        Role role = BeanUtil.copyProperties(roleDTO, Role.class);
        roleMapper.updateById(role);

        menuMapper.deleteByRoleId(roleDTO.getId());
        if (roleDTO.getMenuIds() != null && !roleDTO.getMenuIds().isEmpty()) {
            // 如果有菜单权限，更新角色与菜单的关联关系
            menuMapper.saveMenusByRoleId(roleDTO.getId(), roleDTO.getMenuIds());
        }
    }

    @Override
    public void delete(Long id) {
        LambdaUpdateWrapper<Role> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Role::getDelFlag, RoleConstants.DELETED)
                .eq(Role::getId, id);
        update(updateWrapper);

        /*// 删除角色与菜单的关联关系
        menuMapper.deleteByRoleId(id);*/
    }

    @Override
    public List<RoleVO> listAllRole() {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getDelFlag, RoleConstants.NOT_DELETED)
                .eq(Role::getStatus, RoleConstants.STATUS_NORMAL)
                .orderByAsc(Role::getRoleSort);

        List<Role> roles = list(queryWrapper);
        return BeanUtil.copyToList(roles, RoleVO.class);
    }
}

