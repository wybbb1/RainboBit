package com.wybbb.rainbobit.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文章表(Article)实体类
 *
 * @author Ra1nbot
 * @since 2025-07-24 15:14:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotArticleVO implements Serializable {
    private static final long serialVersionUID = 684844833199262384L;

    private Long id;
/**
     * 标题
     */
    private String title;
/**
     * 访问量
     */
    private Long viewCount;

}

