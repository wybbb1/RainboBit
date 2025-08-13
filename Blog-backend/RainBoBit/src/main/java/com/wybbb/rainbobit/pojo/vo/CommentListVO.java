package com.wybbb.rainbobit.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
    public class CommentListVO {

        private Long id;
        //文章id
        private Long articleId;
        //评论内容
        private String content;

        private String username;
    }
