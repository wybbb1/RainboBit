package com.wybbb.rainbobit.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryVO {
    private Long id;

    //分类名
    private String name;

    //分类状态
    private String status = "0"; // 0表示正常，1表示禁用
    //分类描述
    private String description = "";

    private Date createTime;
    private Date updateTime;

    public CategoryVO(String name) {
        this.id = -1L;
        this.name = name;
    }
    public CategoryVO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
