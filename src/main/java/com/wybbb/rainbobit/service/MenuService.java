package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.entity.Menu;
import com.wybbb.rainbobit.pojo.vo.MenuRoutesVo;

import java.util.List;


/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-07-31 23:12:28
 */
public interface MenuService extends IService<Menu> {

    MenuRoutesVo getRouters();

    List<String> selectPermsByUserId(Long id);
}

