package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.entity.Menu;
import com.wybbb.rainbobit.pojo.vo.MenuRoutesVo;
import com.wybbb.rainbobit.pojo.vo.MenuTreeVO;
import com.wybbb.rainbobit.pojo.vo.MenuVO;
import com.wybbb.rainbobit.pojo.vo.RoleMenuTreeVO;

import java.util.List;


/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-07-31 23:12:28
 */
public interface MenuService extends IService<Menu> {

    // ========== 查询操作 ==========
    
    /**
     * 获取用户路由信息
     * @param userId 用户ID
     * @return 菜单路由信息
     */
    MenuRoutesVo getRouters(Long userId);

    /**
     * 根据用户ID查询权限
     * @param id 用户ID
     * @return 权限列表
     */
    List<String> selectPermsByUserId(Long id);

    /**
     * 查询菜单列表
     * @param status 菜单状态
     * @param menuName 菜单名称
     * @return 菜单列表
     */
    List<MenuVO> getMenuList(Integer status, String menuName);

    /**
     * 根据ID查询菜单
     * @param id 菜单ID
     * @return 菜单信息
     */
    MenuVO selectById(Long id);

    /**
     * 获取菜单树
     * @return 菜单树列表
     */
    List<MenuTreeVO> getMenuTree();

    /**
     * 获取角色菜单树
     * @param id 角色ID
     * @return 角色菜单树
     */
    RoleMenuTreeVO getRoleMenuTree(Long id);

    // ========== 添加操作 ==========
    
    /**
     * 保存菜单
     * @param menu 菜单信息
     */
    void saveMenu(Menu menu);

    // ========== 更新操作 ==========
    
    /**
     * 更新菜单
     * @param menu 菜单信息
     */
    void updateMenu(Menu menu);

    // ========== 删除操作 ==========
    
    /**
     * 删除菜单
     * @param menuId 菜单ID
     */
    void removeMenu(Long menuId);
}

