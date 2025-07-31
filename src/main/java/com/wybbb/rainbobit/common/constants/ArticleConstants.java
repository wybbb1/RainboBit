package com.wybbb.rainbobit.common.constants;

import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;

public class ArticleConstants {
    /**
     *  文章是草稿
     */
    public static final int ARTICLE_STATUS_DRAFT = 1;
    /**
     *  文章是正常分布状态
     */
    public static final int ARTICLE_STATUS_NORMAL = 0;

    public static final int HOT_ARTICLES_SHOW_PER_PAGE = 10;

    public static final int ARTICLE_STATUS_DELETED = 1;

    public static final int ARTICLE_STATUS_NOT_DELETED = 0;

    public static final String PARAM_INVALID = "参数错误";

    public static final String VIEW_COUNT_CACHE_KEY = "rainbobit:article:cache:view_count";

    public static final String ARTICLE_STATUS_ERROR = "文章状态错误";
}
