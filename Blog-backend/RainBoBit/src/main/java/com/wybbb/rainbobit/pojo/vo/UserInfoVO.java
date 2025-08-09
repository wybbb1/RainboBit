package com.wybbb.rainbobit.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserInfoVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickName;

    private String userName;

    /**
     * 头像
     */
    private String avatar;

    private String sex;

    private String email;

}
