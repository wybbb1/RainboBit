package com.wybbb.rainbobit.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "用户登录数据传输对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {

    @Schema(description = "用户名", required = true, example = "admin")
    private String userName;

    @Schema(description = "密码", required = true, example = "password")
    private String password;
}
