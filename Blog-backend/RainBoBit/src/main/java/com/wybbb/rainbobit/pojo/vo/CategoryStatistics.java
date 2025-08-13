package com.wybbb.rainbobit.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatistics {

    private Long categoryId;
    private String categoryName;
    private Long articleCount;

}
