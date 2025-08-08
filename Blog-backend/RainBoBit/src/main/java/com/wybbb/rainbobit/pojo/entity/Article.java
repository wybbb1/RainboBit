package com.wybbb.rainbobit.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;

/**
 * 文章表(Article)实体类
 *
 * @author Ra1nbot
 * @since 2025-07-24 15:14:07
 */
@Schema(description = "文章实体")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("rbb_article")
public class Article implements Serializable {

    @Schema(description = "文章ID")
    @TableId
    private Long id;
    
    @Schema(description = "标题")
    private String title;

    @Schema(description = "文章内容")
    private String content;

    @Schema(description = "文章摘要")
    private String summary;

    @Schema(description = "所属分类id")
    private Long categoryId;

    @Schema(description = "缩略图")
    private String thumbnail;

    @Schema(description = "是否置顶（0否，1是）", example = "0")
    private String isTop;

    @Schema(description = "状态（0已发布，1草稿）", example = "0")
    private String status;

    @Schema(description = "访问量")
    private Long viewCount;

    @Schema(description = "是否允许评论（1是，0否）", example = "1")
    private String isComment;

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

    public Article(Long key, Long value) {
        this.id = key;
        this.viewCount = value;
    }
}

