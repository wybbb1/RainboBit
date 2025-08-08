package com.wybbb.rainbobit.pojo.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 分类表(Category)表实体类
 *
 * @author Ra1nbot
 * @since 2025-07-25 10:35:26
 */
@Schema(description = "分类实体")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rbb_category")
public class Category  {
    @Schema(description = "分类ID")
    @TableId
    private Long id;

    @Schema(description = "分类名")
    private String name;

    @Schema(description = "父分类id，如果没有父分类为-1")
    private Long pid;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "状态（0:正常,1禁用）", example = "0")
    private String status;

    @Schema(description = "创建人ID")
    private Long createBy;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新人ID")
    private Long updateBy;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "删除标志（0代表未删除，1代表已删除）", example = "0")
    private Integer delFlag;

    @Schema(description = "引用次数")
    private Integer refer_cnt;

}

