package com.wybbb.rainbobit.controller.admin;

import cn.hutool.core.bean.BeanUtil;
import com.wybbb.rainbobit.pojo.dto.CategoryDTO;
import com.wybbb.rainbobit.pojo.entity.Category;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.pojo.vo.CategoryVO;
import com.wybbb.rainbobit.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "管理员分类管理", description = "管理员分类管理相关接口")
@RestController
@RequestMapping("/content/category")
public class AdminCategoryController {

    @Resource
    private CategoryService categoryService;

    @Operation(summary = "获取所有分类", description = "获取所有分类列表")
    @PreAuthorize("@ps.hasPermission('content:category:list')")
    @GetMapping("/listAllCategory")
    public ResponseResult<?> listAllCategory() {
        return ResponseResult.okResult(categoryService.listCategory());
    }

    @Operation(summary = "分页获取分类列表", description = "分页获取分类列表，支持按名称和状态搜索")
    @PreAuthorize("@ps.hasPermission('content:category:list')")
    @GetMapping("/list")
    public ResponseResult<?> listCategory(
            @Parameter(description = "分页参数") PageQuery pageQuery,
            @Parameter(description = "分类名称") String name,
            @Parameter(description = "分类状态") String status) {
        return ResponseResult.okResult(categoryService.getCategoryList(pageQuery, name, status));
    }

    @Operation(summary = "根据ID获取分类", description = "根据分类ID获取分类详细信息")
    @PreAuthorize("@ps.hasPermission('content:category:list')")
    @GetMapping("/{id}")
    public ResponseResult<?> getCategoryById(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        Category category = categoryService.getById(id);
        CategoryVO categoryVO = BeanUtil.copyProperties(category, CategoryVO.class);
        return ResponseResult.okResult(categoryVO);
    }

    @Operation(summary = "添加分类", description = "添加新的分类")
    @PreAuthorize("@ps.hasPermission('content:category:add')")
    @PostMapping
    public ResponseResult<?> addCategory(
            @Parameter(description = "分类信息") @RequestBody CategoryDTO categoryDTO) {
        categoryService.addCategory(categoryDTO);
        return ResponseResult.okResult();
    }

    @Operation(summary = "更新分类", description = "更新分类信息")
    @PreAuthorize("@ps.hasPermission('content:category:edit')")
    @PutMapping
    public ResponseResult<?> updateCategory(
            @Parameter(description = "分类信息") @RequestBody CategoryDTO categoryDTO) {
        categoryService.updateCategory(categoryDTO);
        return ResponseResult.okResult();
    }

    @Operation(summary = "删除分类", description = "根据分类ID删除分类")
    @PreAuthorize("@ps.hasPermission('content:category:remove')")
    @DeleteMapping("/{id}")
    public ResponseResult<?> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        categoryService.delete(id);
        return ResponseResult.okResult();
    }

    @Operation(summary = "导出分类", description = "导出分类数据到Excel文件")
    @PreAuthorize("@ps.hasPermission('content:category:export')")
    @GetMapping("/export")
    public void downloadCategory(HttpServletResponse response) {
        categoryService.exportCategory(response);
    }

    @Operation(summary = "导入分类", description = "从Excel文件导入分类数据")
    @PreAuthorize("@ps.hasPermission('content:category:import')")
    @PostMapping("/import")
    public ResponseResult<?> importCategory(
            @Parameter(description = "Excel文件") @RequestParam("file") MultipartFile file) {
        categoryService.importCategory(file);
        return ResponseResult.okResult();

    }


}
