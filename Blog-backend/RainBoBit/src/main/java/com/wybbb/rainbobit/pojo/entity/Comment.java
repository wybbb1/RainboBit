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
 * 评论表(Comment)表实体类
 *
 * @author Ra1nbot
 * @since 2025-07-28 20:47:11
 */
@Schema(description = "评论实体")
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rbb_comment")
public class Comment  {
    @Schema(description = "评论ID")
    @TableId
    private Long id;

    @Schema(description = "评论类型（0代表文章评论，1代表友链评论）", example = "0")
    private String type;

    @Schema(description = "文章id")
    private Long articleId;

    @Schema(description = "根评论id")
    private Long rootId;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "所回复的目标评论的userid")
    private Long toCommentUserId;

    @Schema(description = "回复目标评论id")
    private Long toCommentId;

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



}

