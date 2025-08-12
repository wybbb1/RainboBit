package com.wybbb.rainbobit.common.constants;

import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;

public class CategoryConstants {

    public static final String CACHE_KEY = "rainbobit:category:cache";

    public static final String NOT_DELETED = "0";
    public static final String DELETED = "1";

    public static final int NO_REFERENCE = 0;

    public static final String EXPORT_FILE_NAME = "分类列表.xlsx";
    public static final String EXPORT_SHEET_NAME = "分类列表";

    public static final String NAME_IS_EXIST = "分类名称已存在";
    public static final String INVALID_ID = "无效的分类ID";
    public static final String RELATED_TO_ARTICLE = "分类已关联文章，无法删除";
}
