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
 * 用户表(SysUser)表实体类
 *
 * @author Ra1nbot
 * @since 2025-07-26 11:11:12
 */
@Schema(description = "用户实体")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
public class User  {
    @Schema(description = "用户ID")
    @TableId
    private Long id;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "用户类型：0代表普通用户，1代表管理员", example = "0")
    private String type;

    @Schema(description = "账号状态（0正常 1停用）", example = "0")
    private String status;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phonenumber;

    @Schema(description = "用户性别（0男，1女，2未知）", example = "0")
    private String sex;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "创建人的用户ID")
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
}

