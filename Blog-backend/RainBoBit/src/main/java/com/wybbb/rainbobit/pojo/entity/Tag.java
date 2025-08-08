package com.wybbb.rainbobit.pojo.entity;

import java.util.Date;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
/**
 * 标签(Tag)表实体类
 *
 * @author Ra1nbot
 * @since 2025-07-31 17:32:32
 */
@Schema(description = "标签实体")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rbb_tag")
public class Tag  {
    @Schema(description = "标签ID")
    @TableId
    private Long id;

    @Schema(description = "标签名")
    private String name;

    @Schema(description = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @Schema(description = "更新人ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Schema(description = "删除标志（0代表未删除，1代表已删除）", example = "0")
    private Integer delFlag;

    @Schema(description = "备注")
    private String remark;



}

