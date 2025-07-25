package com.wybbb.rainbobit.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rbb_article")
public class CategoryVO {
    @TableId
    private Long id;

    //分类名
    private String name;

    public CategoryVO(String name) {
        this.id = -1L;
        this.name = name;
    }

}
