package com.wybbb.rainbobit.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleMenuTreeVO {
    List<MenuTreeVO> menus;
    List<Long> checkedKeys;
}
