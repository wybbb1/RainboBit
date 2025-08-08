package com.wybbb.rainbobit.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String nickname;
    private String password;
    private String phonenumber;
    private String email;
    private String sex;
    private String status; // 用户状态

    private List<Long> roleIds;
}
