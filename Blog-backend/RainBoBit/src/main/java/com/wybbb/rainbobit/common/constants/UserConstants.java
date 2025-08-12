package com.wybbb.rainbobit.common.constants;

import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import com.wybbb.rainbobit.pojo.dto.UserLoginDTO;

public class UserConstants {

    public static final String CACHE_KEY = "rainbobit:user:cache:";

    public static final String CACHE_VALUE_NULL = "尝试操作不存在的值";
    public static final String NEED_LOGIN = "用户未登录，请先登录";

    public static final String USERNAME_IS_NULL = "用户名不能为空";
    public static final String PASSWORD_IS_NULL = "密码不能为空";
    public static final String EMAIL_IS_NULL = "邮箱不能为空";
    public static final String NICKNAME_IS_NULL = "昵称不能为空";
    public static final String ID_IS_NULL = "用户ID不能为空";

    public static final String USERNAME_ALREADY_EXISTS = "用户名已存在，请更换用户名";
    public static final String EMAIL_ALREADY_EXISTS = "邮箱已存在，请更换邮箱";
    public static final String PHONE_ALREADY_EXISTS = "手机号已存在，请更换手机号";

    public static final String CANNOT_DELETE_SUPER_ADMIN = "不能删除超级管理员用户";

    public static final String EMAIL_ALREADY_SENT = "验证码已发送，请稍后再试";
    public static final String EMAIL_FORMAT_ERROR = "邮箱格式错误，请输入正确的邮箱地址";
    public static final String CODE_ERROR = "验证码错误";

    public static final String LOGIN_TYPE_ERROR = "登录异常";
    public static final String NOT_FOUND = "用户不存在";


    public static final int ADMIN_LOGIN = 1;
    public static final int USER_LOGIN = 0;

    public static final Long SUPER_ADMIN_ID = 1L;

    public static final String DELETED = "1";
    public static final String NOT_DELETED = "0";
    public static final String NORMAL = "0";
    public static final String LOGIN_CODE_KEY = "rainbobit:user:login:code:";
}
