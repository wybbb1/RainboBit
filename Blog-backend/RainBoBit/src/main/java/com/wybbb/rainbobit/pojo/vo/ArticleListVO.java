package com.wybbb.rainbobit.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListVO {

    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 文章摘要
     */
    private String summary;
    /**
     * 缩略图
     */
    private String thumbnail;
    /**
     * 所属分类id
     */
    private Long categoryId;

    private List<Long> tagIds;
    /**
     * 访问量
     */
    private Long viewCount;

    private Date createTime;
}
