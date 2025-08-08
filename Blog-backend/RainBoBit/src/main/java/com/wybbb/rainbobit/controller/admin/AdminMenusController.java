package com.wybbb.rainbobit.controller.admin;

import com.wybbb.rainbobit.common.utils.SecurityUtils;
import com.wybbb.rainbobit.pojo.entity.Menu;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "菜单管理", description = "菜单管理相关接口")
@PreAuthorize("@ps.hasPermission('system:menu:list')")
@RequestMapping("/system/menu")
@RestController
public class AdminMenusController {

    @Resource
    private MenuService menuService;

    @Operation(summary = "获取菜单列表", description = "获取菜单列表，支持按状态和菜单名搜索")
    @GetMapping("/list")
    public ResponseResult getMenuList(
            @Parameter(description = "菜单状态") Integer status, 
            @Parameter(description = "菜单名称") String menuName) {
        return ResponseResult.okResult(menuService.getMenuList(status, menuName));
    }

    @Operation(summary = "添加菜单", description = "添加新的菜单")
    @PostMapping
    public ResponseResult addMenu(
            @Parameter(description = "菜单信息") @RequestBody Menu menu) {
        menuService.saveMenu(menu);
        return ResponseResult.okResult();
    }

    @Operation(summary = "根据ID获取菜单", description = "根据菜单ID获取菜单详细信息")
    @GetMapping("/{id}")
    public ResponseResult getMenuById(
            @Parameter(description = "菜单ID") @PathVariable Long id) {
        return ResponseResult.okResult(menuService.selectById(id));
    }

    @Operation(summary = "更新菜单", description = "更新菜单信息")
    @PutMapping
    public ResponseResult update(
            @Parameter(description = "菜单信息") @RequestBody Menu menu) {
        menuService.updateMenu(menu);
        return ResponseResult.okResult();
    }

    @Operation(summary = "删除菜单", description = "根据菜单ID删除菜单")
    @DeleteMapping("/{id}")
    public ResponseResult deleteMenu(
            @Parameter(description = "菜单ID") @PathVariable Long id) {
        menuService.removeMenu(id);
        return ResponseResult.okResult();
    }

    @Operation(summary = "获取菜单树", description = "获取菜单树形结构")
    @GetMapping("/treeselect")
    public ResponseResult getMenuTree(){
        return ResponseResult.okResult(menuService.getMenuTree());
    }

    @Operation(summary = "获取角色菜单树", description = "根据角色ID获取角色关联的菜单树")
    @GetMapping("/roleMenuTreeselect/{id}")
    public ResponseResult getRoleMenuTree(
            @Parameter(description = "角色ID") @PathVariable Long id) {
        return ResponseResult.okResult(menuService.getRoleMenuTree(id));
    }

}
