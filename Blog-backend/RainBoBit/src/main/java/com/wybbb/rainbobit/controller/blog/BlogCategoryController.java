package com.wybbb.rainbobit.controller.blog;

import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.pojo.vo.CategoryVO;
import com.wybbb.rainbobit.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 分类表(Category)表控制层
 *
 * @author Ra1nbot
 * @since 2025-07-25 10:34:47
 */
@Tag(name = "分类", description = "分类相关接口")
@RestController(value = "blogCategoryController")
@RequestMapping("/category")
public class BlogCategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 获取分类列表
     *
     * @return 分类列表
     */
    @Operation(summary = "获取分类列表", description = "获取所有可用的文章分类")
    @GetMapping("/getCategoryList")
    public ResponseResult<List<CategoryVO>> getAllCategory() {
        return ResponseResult.okResult(categoryService.listCategory());
    }

    @GetMapping("/{id}")
    public ResponseResult getCategoryById(@PathVariable Long id) {
        return ResponseResult.okResult(categoryService.getCategoryById(id));
    }

}

