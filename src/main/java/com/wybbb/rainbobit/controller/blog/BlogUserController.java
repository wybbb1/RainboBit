package com.wybbb.rainbobit.controller.blog;

import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.pojo.ResponseResult;
import com.wybbb.rainbobit.pojo.entity.User;
import com.wybbb.rainbobit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "博客用户", description = "博客用户相关接口")
@RestController
@RequestMapping("/user")
public class BlogUserController {

    @Resource
    private UserService userService;

    @Operation(summary = "获取用户信息", description = "获取当前登录用户的信息")
    @GetMapping("/userInfo")
    public ResponseResult userInfo() {
        return ResponseResult.okResult(userService.getUserInfo());
    }

    @Operation(summary = "上传用户头像", description = "上传用户头像图片")
    @PostMapping("/upload")
    public ResponseResult upload(
            @Parameter(description = "头像图片文件") MultipartFile img) {
        if (img == null || img.isEmpty()) {
            throw new SystemException(UserConstants.FILE_IS_NULL);
        }
        String url = userService.upload(img);
        return ResponseResult.okResult(url);
    }

    @Operation(summary = "更新用户信息", description = "更新当前登录用户的个人信息")
    @PutMapping("/userInfo")
    public ResponseResult updateUserInfo(
            @Parameter(description = "用户信息") @RequestBody User user) {
        userService.updateUserInfo(user);
        return ResponseResult.okResult();
    }

    @Operation(summary = "用户注册", description = "新用户注册接口")
    @PostMapping("/register")
    public ResponseResult register(
            @Parameter(description = "用户信息") @RequestBody User user){
        userService.register(user);
        return ResponseResult.okResult();
    }

}
