package com.wybbb.rainbobit.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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
@Document(indexName = "article")
public class Article implements Serializable {

    @Schema(description = "文章ID")
    @TableId(type = IdType.AUTO)
    @Id
    private Long id;
    
    @Schema(description = "标题")
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", copyTo = "all")
    private String title;

    @Schema(description = "文章内容")
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", copyTo = "all")
    private String content;

    @Schema(description = "文章摘要")
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart", index = false)
    private String summary;

    @Schema(description = "所属分类id")
    @Field(type = FieldType.Keyword)
    private Long categoryId;

    @Schema(description = "缩略图")
    @Field(type = FieldType.Text, index = false)
    private String thumbnail;

    @Schema(description = "是否置顶（0否，1是）", example = "0")
    @Field(type = FieldType.Keyword)
    private String isTop;

    @Schema(description = "状态（0已发布，1草稿）", example = "0")
    @Field(type = FieldType.Keyword)
    private String status;

    @Schema(description = "访问量")
    @Field(type = FieldType.Keyword)
    private Long viewCount;

    @Schema(description = "是否允许评论（1是，0否）", example = "1")
    @Field(type = FieldType.Keyword, index = false)
    private String isComment;

    @Schema(description = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    @Field(type = FieldType.Keyword, index = false)
    private Long createBy;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @Field(type = FieldType.Date, index = false, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "更新人ID")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Field(type = FieldType.Keyword, index = false)
    private Long updateBy;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Field(type = FieldType.Date, index = false, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Schema(description = "删除标志（0代表未删除，1代表已删除）", example = "0")
    @Field(type = FieldType.Keyword, index = false)
    private Integer delFlag;

    public Article(Long key, Long value) {
        this.id = key;
        this.viewCount = value;
    }
}

