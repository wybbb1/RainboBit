package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.dto.CategoryDTO;
import com.wybbb.rainbobit.pojo.entity.Category;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.vo.CategoryListVO;
import com.wybbb.rainbobit.pojo.vo.CategoryVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;


/**
 * 分类表(Category)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-07-25 10:34:54
 */
public interface CategoryService extends IService<Category> {

    List<CategoryVO> listCategory();

    void exportCategory(HttpServletResponse response);

    PageResult<CategoryListVO> getCategoryList(PageQuery pageQuery, String name, String status);

    void addCategory(CategoryDTO categoryDTO);

    void delete(Long id);

    CategoryVO getCategoryById(Long id);
}

