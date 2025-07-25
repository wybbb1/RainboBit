package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.ResponseResult;
import com.wybbb.rainbobit.pojo.entity.Category;
import com.wybbb.rainbobit.pojo.vo.CategoryVO;

import java.util.List;


/**
 * 分类表(Category)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-07-25 10:34:54
 */
public interface CategoryService extends IService<Category> {

    List<CategoryVO> listCategory();
}

