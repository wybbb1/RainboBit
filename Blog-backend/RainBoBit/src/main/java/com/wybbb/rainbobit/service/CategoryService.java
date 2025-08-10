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

    // ========================== 查询相关 ==========================
    
    /**
     * 获取分类列表
     */
    List<CategoryVO> listCategory();

    /**
     * 分页获取分类列表（管理端）
     */
    PageResult<CategoryListVO> getCategoryList(PageQuery pageQuery, String name, String status);

    /**
     * 根据ID获取分类详情
     */
    CategoryVO getCategoryById(Long id);

    /**
     * 导出分类数据
     */
    void exportCategory(HttpServletResponse response);

    /**
     * 导入分类数据
     */
    void importCategory(org.springframework.web.multipart.MultipartFile file);

    // ========================== 添加相关 ==========================
    
    /**
     * 添加分类
     */
    void addCategory(CategoryDTO categoryDTO);

    // ========================== 更新相关 ==========================
    
    /**
     * 更新分类
     */
    void updateCategory(CategoryDTO categoryDTO);

    // ========================== 删除相关 ==========================
    
    /**
     * 删除分类
     */
    void delete(Long id);
    

}

