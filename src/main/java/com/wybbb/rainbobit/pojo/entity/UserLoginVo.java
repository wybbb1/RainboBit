package com.wybbb.rainbobit.pojo.entity;

import com.wybbb.rainbobit.pojo.vo.UserInfoVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginVo {

    private String token;
    private UserInfoVO userInfo;
}