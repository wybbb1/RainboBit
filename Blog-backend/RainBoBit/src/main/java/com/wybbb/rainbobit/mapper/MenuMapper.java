package com.wybbb.rainbobit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wybbb.rainbobit.pojo.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 菜单权限表(Menu)表数据库访问层
 *
 * @author Ra1nbot
 * @since 2025-07-31 23:12:28
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {


    List<Menu> selectByUserId(Long id);

    List<Menu> selectMCByUserId(Long id);

    void saveMenusByRoleId(Long roleId, List<Long> menuIds);

    List<Menu> selectMenusByRoleId(Long id);

    void deleteByRoleId(Long id);
}

