package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.MenuConstants;
import com.wybbb.rainbobit.common.constants.RoleConstants;
import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.mapper.MenuMapper;
import com.wybbb.rainbobit.pojo.entity.Menu;
import com.wybbb.rainbobit.pojo.vo.MenuRoutesVo;
import com.wybbb.rainbobit.pojo.vo.MenuTreeVO;
import com.wybbb.rainbobit.pojo.vo.MenuVO;
import com.wybbb.rainbobit.pojo.vo.RoleMenuTreeVO;
import com.wybbb.rainbobit.service.MenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author Ra1nbot
 * @since 2025-07-31 23:12:29
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Resource
    private MenuMapper menuMapper;

    @Transactional
    @Override
    public MenuRoutesVo getRouters(Long userId) {
        if (Objects.equals(userId, UserConstants.SUPER_ADMIN_ID)) {
            return selectAdminRouters();
        }else{
            if (Objects.nonNull(userId)) {
                return selectUserRouters(userId);
            }
        }
        return null;
    }

    private MenuRoutesVo selectUserRouters(Long userId) {
        List<Menu> menus = menuMapper.selectMCByUserId(userId);

        return buildMenuRoutes(menus);
    }

    private MenuRoutesVo selectAdminRouters(){
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Menu::getMenuType, MenuConstants.MENU_TYPE_BUTTON)
                .eq(Menu::getDelFlag, MenuConstants.MENU_NOT_DELETED);

        List<Menu> menus = list(queryWrapper);

        return buildMenuRoutes(menus);
    }

    private MenuRoutesVo buildMenuRoutes(List<Menu> menus){
        List<Menu> rootMenus = list().stream()
                .filter(menu -> menu.getParentId() == 0)
                .filter(menu -> menu.getDelFlag().equals(MenuConstants.MENU_NOT_DELETED))
                .toList();

        List<Menu> tree = recurMenu(rootMenus, menus);

        return new MenuRoutesVo(tree);
    }

    private List<Menu> recurMenu(List<Menu> rootMenus, List<Menu> menus) {
        if (rootMenus == null || rootMenus.isEmpty()) {
            return null;
        }

        return rootMenus.stream()
                .peek(menu -> {
                    List<Menu> childMenus = menus.stream()
                            .filter(child -> child.getParentId().equals(menu.getId()))
                            .toList();
                    menu.setChildren(recurMenu(childMenus, menus));
                })
                .toList();
    }

    @Override
    public List<String> selectPermsByUserId(Long id) {
        List<Menu> menus = selectMenusByUserId(id);

        return menus.stream()
                .map(Menu::getPerms)
                .filter(perm -> perm != null && !perm.isEmpty())
                .toList();
    }

    @Override
    public List<MenuVO> getMenuList(Integer status, String menuName) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(status != null, Menu::getStatus, status)
                .eq(Menu::getDelFlag, MenuConstants.MENU_NOT_DELETED)
                .like(menuName != null && !menuName.isBlank(), Menu::getMenuName, menuName)
                .orderByAsc(Menu::getParentId)
                .orderByAsc(Menu::getOrderNum);

        List<Menu> menus = list(queryWrapper);

        return BeanUtil.copyToList(menus, MenuVO.class);
    }

    @Override
    public MenuVO selectById(Long id) {
        Menu menu = getById(id);
        if (menu == null) {
            throw new SystemException(MenuConstants.MENU_NOT_FOUND);
        }
        return BeanUtil.copyProperties(menu, MenuVO.class);
    }

    @Override
    public void updateMenu(Menu menu) {
        if (menu.getId() == null) {
            throw new SystemException(MenuConstants.MENU_ID_NULL);
        }
        if (menu.getId().equals(menu.getParentId())) {
            throw new SystemException(MenuConstants.MENU_PARENT_ID_CANNOT_BE_SELF);
        }

        updateById(menu);
    }

    @Override
    public void removeMenu(Long menuId) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId, menuId);
        if (menuMapper.exists(queryWrapper)) {
            throw new SystemException(MenuConstants.MENU_HAS_CHILDREN);
        }

        LambdaUpdateWrapper<Menu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Menu::getDelFlag, MenuConstants.MENU_DELETED)
                .eq(Menu::getId, menuId);
        update(updateWrapper);
    }

    @Transactional
    @Override
    public void saveMenu(Menu menu) {
        if (menu.getParentId() == 0L) {
            if (menu.getMenuType().equals(MenuConstants.MENU_TYPE_BUTTON)) {
                throw new SystemException(MenuConstants.MENU_TYPE_MISMATCH);
            }else{
                save(menu);
            }
        }else{
            Menu parentMenu = getById(menu.getParentId());
            String pType = parentMenu.getMenuType();
            String type = menu.getMenuType();
            if ((pType.equals(MenuConstants.MENU_TYPE_MENU) && type.equals(MenuConstants.MENU_TYPE_BUTTON))
                    || pType.equals(MenuConstants.MENU_TYPE_DIR) && (type.equals(MenuConstants.MENU_TYPE_MENU) || type.equals(MenuConstants.MENU_TYPE_DIR))) {
                save(menu);
            }else{
                throw new SystemException(MenuConstants.MENU_TYPE_MISMATCH);
            }
        }

    }

    private List<Menu> selectMenusByUserId(Long userId){
        List<Menu> menus = null;

        if (Objects.equals(userId, UserConstants.SUPER_ADMIN_ID)){
            // 如果是超级管理员，返回所有权限标识
            LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Menu::getStatus, MenuConstants.MENU_STATUS_NORMAL)
                    .in(Menu::getMenuType, MenuConstants.MENU_TYPE_MENU, MenuConstants.MENU_TYPE_BUTTON);
            menus = list(queryWrapper);
        }else{
            menus = menuMapper.selectByUserId(userId);
        }

        return menus;
    }

    @Override
    public List<MenuTreeVO> getMenuTree() {
        // 获取所有菜单
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getDelFlag, MenuConstants.MENU_NOT_DELETED)
                .eq(Menu::getStatus, MenuConstants.MENU_STATUS_NORMAL)
                .orderByAsc(Menu::getOrderNum);
        List<Menu> menus = list(queryWrapper);
        // 获取首级目录
        List<Menu> rootMenus = menus.stream()
                .filter(menu -> menu.getParentId() == 0)
                .toList();
        // 递归获取子菜单
        List<Menu> finalMenus = recurMenu(rootMenus, menus);

        return finalMenus.stream()
                .map(menu -> BeanUtil.copyProperties(menu, MenuTreeVO.class))
                .toList();
    }

    @Override
    public RoleMenuTreeVO getRoleMenuTree(Long id) {
        List<Long> checkedKeys = null;
        if (Objects.equals(id, RoleConstants.ADMIN_ID)){
            checkedKeys = menuMapper.selectList(new LambdaQueryWrapper<Menu>()
                    .eq(Menu::getDelFlag, MenuConstants.MENU_NOT_DELETED)
                    .eq(Menu::getStatus, MenuConstants.MENU_STATUS_NORMAL))
                    .stream()
                    .map(Menu::getId)
                    .toList();
        }else {
            checkedKeys = menuMapper.selectMenusByRoleId(id).stream()
                    .map(Menu::getId)
                    .toList();
        }

        RoleMenuTreeVO roleMenuTreeVO = new RoleMenuTreeVO();
        roleMenuTreeVO.setMenus(getMenuTree());
        roleMenuTreeVO.setCheckedKeys(checkedKeys);

        return roleMenuTreeVO;
    }

}

