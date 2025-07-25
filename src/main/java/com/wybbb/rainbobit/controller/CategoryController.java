package com.wybbb.rainbobit.controller;
import com.wybbb.rainbobit.pojo.ResponseResult;
import com.wybbb.rainbobit.pojo.vo.CategoryVO;
import com.wybbb.rainbobit.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 分类表(Category)表控制层
 *
 * @author Ra1nbot
 * @since 2025-07-25 10:34:47
 */
@RestController(value = "blogCategoryController")
@RequestMapping("category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 获取分类列表
     *
     * @return 分类列表
     */
    @GetMapping("/getCategoryList")
    public ResponseResult<List<CategoryVO>> getAllCategory() {
        return ResponseResult.okResult(categoryService.listCategory());
    }

}

