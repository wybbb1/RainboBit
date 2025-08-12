package com.wybbb.rainbobit.controller.admin;

import com.wybbb.rainbobit.common.utils.SecurityUtils;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.service.MenuService;
import com.wybbb.rainbobit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理员信息", description = "管理员基础信息相关接口")
@RestController
public class OtherInfoController {

    @Resource
    private MenuService menuService;
    @Resource
    private UserService userService;

    @Operation(summary = "获取管理员信息", description = "获取当前登录管理员的基本信息和权限")
    @GetMapping("/getInfo")
    public ResponseResult<?> adminGetInfo() {
        return ResponseResult.okResult(userService.adminGetInfo());
    }

    @Operation(summary = "获取路由信息", description = "获取当前用户的菜单路由信息")
    @GetMapping("/getRouters")
    public ResponseResult<?> getRouters() {
        Long userId = SecurityUtils.getUserId();
        return ResponseResult.okResult(menuService.getRouters(userId));
    }
}
