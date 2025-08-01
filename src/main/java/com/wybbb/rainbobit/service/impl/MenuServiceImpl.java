package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.MenuConstants;
import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.mapper.MenuMapper;
import com.wybbb.rainbobit.pojo.entity.Menu;
import com.wybbb.rainbobit.pojo.vo.MenuRoutesVo;
import com.wybbb.rainbobit.service.MenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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
    public MenuRoutesVo getRouters() {
        // 查询所有顶级菜单
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId, MenuConstants.MENU_ROOT_ID)
                .eq(Menu::getVisible, MenuConstants.MENU_VISIBLE)
                .eq(Menu::getStatus, MenuConstants.MENU_STATUS_NORMAL)
                .orderByAsc(Menu::getOrderNum);
        List<Menu> menus = list(queryWrapper);

        return new MenuRoutesVo(menus.stream()
                .map(menu -> {
                    // 设置子菜单
                    LambdaQueryWrapper<Menu> childQueryWrapper = new LambdaQueryWrapper<>();
                    childQueryWrapper.eq(Menu::getParentId, menu.getId())
                            .eq(Menu::getVisible, MenuConstants.MENU_VISIBLE)
                            .eq(Menu::getStatus, MenuConstants.MENU_STATUS_NORMAL)
                            .orderByAsc(Menu::getOrderNum);
                    List<Menu> childMenus = list(childQueryWrapper);
                    // 将子菜单转换为 MenuRoutesVo 列表
                    menu.setChildren(childMenus);

                    return menu;
                }).toList());
    }

    @Override
    public List<String> selectPermsByUserId(Long id) {
        List<Menu> menus = null;

        if (Objects.equals(id, UserConstants.SUPER_ADMIN_ID)){
            // 如果是超级管理员，返回所有权限标识
            LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Menu::getStatus, MenuConstants.MENU_STATUS_NORMAL)
                    .in(Menu::getMenuType, MenuConstants.MENU_TYPE_MENU, MenuConstants.MENU_TYPE_BUTTON);
            menus = list(queryWrapper);
        }else{
            menus = menuMapper.selectByUserId(id);
        }

        return menus.stream()
                .map(Menu::getPerms)
                .filter(perm -> perm != null && !perm.isEmpty())
                .toList();
    }
}

