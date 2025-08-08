package com.wybbb.rainbobit.pojo.vo;

import com.wybbb.rainbobit.pojo.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuRoutesVo {
    private List<Menu> menus;
}
