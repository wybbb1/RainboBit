package com.wybbb.rainbobit.controller;

import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.pojo.ResponseResult;
import com.wybbb.rainbobit.pojo.dto.UserLoginDTO;
import com.wybbb.rainbobit.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



/**
 * 用户表(SysUser)表控制层
 *
 * @author Ra1nbot
 * @since 2025-07-26 11:11:33
 */
@Slf4j
@RestController("blogLoginController")
public class BlogLoginController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody UserLoginDTO userLoginDTO) {
        String userName = userLoginDTO.getUserName();
        if (userName == null || userName.isBlank()){
            log.error("用户名不能为空");
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }

        return ResponseResult.okResult(userService.login(userLoginDTO));
    }

    @PostMapping("/logout")
    public ResponseResult logout() {
        userService.logout();
        return ResponseResult.okResult();
    }

}

