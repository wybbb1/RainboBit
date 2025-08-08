package com.wybbb.rainbobit.controller.blog;

import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.pojo.dto.UserLoginDTO;
import com.wybbb.rainbobit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * 用户表(SysUser)表控制层
 *
 * @author Ra1nbot
 * @since 2025-07-26 11:11:33
 */
@Tag(name = "博客登录", description = "博客用户登录相关接口")
@Slf4j
@RestController("blogLoginController")
public class BlogLoginController {

    @Resource
    private UserService userService;

    @Operation(summary = "用户登录", description = "博客用户登录接口")
    @PostMapping("/login")
    public ResponseResult login(
            @Parameter(description = "登录信息") @RequestBody UserLoginDTO userLoginDTO) {
        String userName = userLoginDTO.getUserName();
        if (userName == null || userName.isBlank()){
            log.error("用户名不能为空");
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }

        return ResponseResult.okResult(userService.login(UserConstants.USER_LOGIN, userLoginDTO));
    }

    @Operation(summary = "用户登出", description = "博客用户退出登录接口")
    @PostMapping("/logout")
    public ResponseResult logout() {
        userService.logout();
        return ResponseResult.okResult();
    }

}

