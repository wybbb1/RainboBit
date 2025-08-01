package com.wybbb.rainbobit.controller.admin;

import cn.hutool.json.JSONUtil;
import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.pojo.ResponseResult;
import com.wybbb.rainbobit.pojo.dto.UserLoginDTO;
import com.wybbb.rainbobit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class AdminLoginController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public ResponseResult login(
            @Parameter(description = "登录信息") @RequestBody UserLoginDTO userLoginDTO) {
        String userName = userLoginDTO.getUserName();
        if (userName == null || userName.isBlank()){
            log.error("用户名不能为空");
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }

        return ResponseResult.okResult(userService.login(UserConstants.ADMIN_LOGIN, userLoginDTO));
    }

    @PostMapping("/logout")
    public ResponseResult logout() {
        userService.logout();
        return ResponseResult.okResult();
    }
}
