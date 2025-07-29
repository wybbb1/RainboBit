package com.wybbb.rainbobit.controller;

import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.pojo.ResponseResult;
import com.wybbb.rainbobit.pojo.entity.User;
import com.wybbb.rainbobit.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
public class BlogUserController {

    @Resource
    private UserService userService;

    @GetMapping("/userInfo")
    public ResponseResult userInfo() {
        return ResponseResult.okResult(userService.getUserInfo());
    }

    @PostMapping("/upload")
    public ResponseResult upload(MultipartFile img) {
        if (img == null || img.isEmpty()) {
            throw new SystemException(UserConstants.FILE_IS_NULL);
        }
        String url = userService.upload(img);
        return ResponseResult.okResult(url);
    }

    @PutMapping("/userInfo")
    public ResponseResult updateUserInfo(@RequestBody User user) {
        userService.updateUserInfo(user);
        return ResponseResult.okResult();
    }

    @PostMapping("/register")
    public ResponseResult register(@RequestBody User user){
        userService.register(user);
        return ResponseResult.okResult();
    }

}
