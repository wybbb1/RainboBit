package com.wybbb.rainbobit.pojo.entity;

import java.util.Date;

import java.io.Serializable;
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
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rbb_tag")
public class Tag  {
    @TableId
    private Long id;

//标签名
    private String name;

    private Long createBy;

    private Date createTime;

    private Long updateBy;

    private Date updateTime;
//删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;
//备注
    private String remark;



}

