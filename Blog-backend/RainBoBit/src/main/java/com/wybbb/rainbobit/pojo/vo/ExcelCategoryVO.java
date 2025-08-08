package com.wybbb.rainbobit.pojo.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ExcelCategoryVO {
    @ExcelProperty(value = "分类名称", index = 0)
    private String name;
    @ExcelProperty(value = "分类描述", index = 1)
    private String description;
    @ExcelProperty(value = "分类状态(0:正常， 1：禁用)", index = 2)
    private String status;

}
