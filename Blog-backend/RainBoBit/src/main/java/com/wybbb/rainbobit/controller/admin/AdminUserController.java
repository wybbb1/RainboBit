package com.wybbb.rainbobit.controller.admin;

import com.wybbb.rainbobit.pojo.dto.UserDTO;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员用户管理", description = "管理员用户管理相关接口")
@RequestMapping("/system/user")
@RestController
public class AdminUserController {

    @Resource
    private UserService userService;

    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持用户名、手机号、状态搜索")
    @GetMapping("/list")
    public ResponseResult list(
            @Parameter(description = "分页参数") PageQuery pageQuery, 
            @Parameter(description = "用户名") String username, 
            @Parameter(description = "手机号") String phonenumber, 
            @Parameter(description = "用户状态") String status) {
        return ResponseResult.okResult(userService.listUser(pageQuery, username, phonenumber, status));
    }

    @Operation(summary = "添加用户", description = "添加新用户")
    @PostMapping
    public ResponseResult add(
            @Parameter(description = "用户信息") @RequestBody UserDTO userDTO) {
        userService.add(userDTO);
        return ResponseResult.okResult();
    }

    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @DeleteMapping("/{id}")
    public ResponseResult delete(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        userService.delete(id);
        return ResponseResult.okResult();
    }

    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息")
    @GetMapping("/{id}")
    public ResponseResult getUser(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        return ResponseResult.okResult(userService.getUser(id));
    }

    @Operation(summary = "更新用户", description = "更新用户信息")
    @PutMapping
    public ResponseResult update(
            @Parameter(description = "用户信息") @RequestBody UserDTO userDTO) {
        userService.update(userDTO);
        return ResponseResult.okResult();
    }
}
