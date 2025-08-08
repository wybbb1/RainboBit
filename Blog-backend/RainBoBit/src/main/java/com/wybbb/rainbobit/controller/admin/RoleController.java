package com.wybbb.rainbobit.controller.admin;

import com.wybbb.rainbobit.pojo.dto.RoleDTO;
import com.wybbb.rainbobit.pojo.dto.RoleStatusDTO;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "角色管理", description = "角色管理相关接口")
@PreAuthorize("@ps.hasPermission('system:role:list')")
@RestController
@RequestMapping("/system/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @Operation(summary = "分页获取角色列表", description = "分页获取角色列表，支持按角色名和状态搜索")
    @GetMapping("/list")
    public ResponseResult page(
            @Parameter(description = "分页参数") PageQuery pageQuery, 
            @Parameter(description = "角色名称") String roleName, 
            @Parameter(description = "角色状态") String status) {
        return ResponseResult.okResult(roleService.pageRole(pageQuery, roleName, status));
    }

    @Operation(summary = "修改角色状态", description = "启用或停用角色")
    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(
            @Parameter(description = "角色状态信息") @RequestBody RoleStatusDTO roleStatusDTO) {
        roleService.changeStatus(roleStatusDTO);
        return ResponseResult.okResult();
    }

    @Operation(summary = "添加角色", description = "添加新的角色")
    @PostMapping
    public ResponseResult addRole(
            @Parameter(description = "角色信息") @RequestBody RoleDTO roleDTO) {
        roleService.add(roleDTO);
        return ResponseResult.okResult();
    }

    @Operation(summary = "根据ID获取角色", description = "根据角色ID获取角色详细信息")
    @GetMapping("/{id}")
    public ResponseResult getRoleById(
            @Parameter(description = "角色ID") @PathVariable Long id) {
        return ResponseResult.okResult(roleService.getRoleById(id));
    }

    @Operation(summary = "更新角色", description = "更新角色信息")
    @PutMapping
    public ResponseResult update(
            @Parameter(description = "角色信息") @RequestBody RoleDTO roleDTO) {
        roleService.updateRole(roleDTO);
        return ResponseResult.okResult();
    }

    @Operation(summary = "删除角色", description = "根据角色ID删除角色")
    @DeleteMapping("/{id}")
    public ResponseResult deleteRole(
            @Parameter(description = "角色ID") @PathVariable Long id) {
        roleService.delete(id);
        return ResponseResult.okResult();
    }

    @Operation(summary = "获取所有角色", description = "获取所有角色列表")
    @GetMapping("/listAllRole")
    public ResponseResult listAllRole() {
        return ResponseResult.okResult(roleService.listAllRole());
    }
}
