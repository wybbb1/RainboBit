package com.wybbb.rainbobit.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryListVO {


    private Long id;
    private String name;
    private String status;
    private String description;
}
