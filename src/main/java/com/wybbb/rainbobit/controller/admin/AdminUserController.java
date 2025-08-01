package com.wybbb.rainbobit.controller.admin;

import com.wybbb.rainbobit.pojo.ResponseResult;
import com.wybbb.rainbobit.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminUserController {

    @Resource
    private UserService userService;

    @GetMapping("/getInfo")
    public ResponseResult adminGetInfo() {
        return ResponseResult.okResult(userService.adminGetInfo());
    }
}
