package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.CategoryConstants;
import com.wybbb.rainbobit.common.constants.SystemConstants;
import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import com.wybbb.rainbobit.common.utils.RedisCacheHelper;
import com.wybbb.rainbobit.common.utils.WebUtils;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.mapper.CategoryMapper;
import com.wybbb.rainbobit.pojo.dto.CategoryDTO;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.pojo.entity.Category;
import com.wybbb.rainbobit.pojo.vo.CategoryListVO;
import com.wybbb.rainbobit.pojo.vo.CategoryVO;
import com.wybbb.rainbobit.pojo.vo.ExcelCategoryVO;
import com.wybbb.rainbobit.service.CategoryService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 分类表(Category)表服务实现类
 *
 * @author Ra1nbot
 * @since 2025-07-25 10:34:54
 */
@Service("blogCategoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private RedisCacheHelper redisCacheHelper;
    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public List<CategoryVO> listCategory() {
        // 从缓存中获取分类列表
        Map<Long, String> map = redisCacheHelper.getMap(
                CategoryConstants.CATEGORY_CACHE_KEY,
                Long.class,
                String.class,
                () -> {
                    // 如果缓存中不存在，从数据库查询
                    LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(Category::getDelFlag, CategoryConstants.CATEGORY_NOT_DELETED)
                            .gt(Category::getRefer_cnt, CategoryConstants.CATEGORY_NO_REFERENCE);
                    List<Category> categories = list(queryWrapper);

                    // 将查询结果转换为VO对象
                    List<CategoryVO> categoryVOs = BeanUtil.copyToList(categories, CategoryVO.class);

                    return categoryVOs.stream()
                            .collect(Collectors.toMap(
                                    CategoryVO::getId,
                                    CategoryVO::getName,
                                    (existing, replacement) -> existing));// 如果有重复键，保留第一个
                });

        if (map.isEmpty()) {
            throw new RuntimeException(SystemConstants.RUNTIME_ERROR);
        }

        // 将Map转换为List<CategoryVO>
        return map.entrySet().stream()
                .map(entry -> new CategoryVO(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public void exportCategory(HttpServletResponse response) {
        try{
            //设置下载文件的响应头
            WebUtils.setDownLoadHeader(CategoryConstants.EXPORT_FILE_NAME, response);
            // 从缓存中获取分类列表
            List<Category> categories = list();
            List<ExcelCategoryVO> data = BeanUtil.copyToList(categories, ExcelCategoryVO.class);
            // 把数据写入到Excel
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVO.class)
                    .autoCloseStream(Boolean.FALSE)
                    .sheet(CategoryConstants.EXPORT_SHEET_NAME)
                    .doWrite(data);
        } catch (Exception e) {
            response.reset();
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result));
        }
    }

    @Override
    public PageResult<CategoryListVO> getCategoryList(PageQuery pageQuery, String name, String status) {
        // 构建查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getDelFlag, CategoryConstants.CATEGORY_NOT_DELETED)
                .like(name != null && !name.isEmpty(), Category::getName, name)
                .eq(status != null && !status.isEmpty(), Category::getStatus, status)
                .orderByAsc(Category::getCreateTime);

        // 执行分页查询
        Page<Category> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
        page(page, queryWrapper);

        List<CategoryListVO> categoryListVOS = BeanUtil.copyToList(page.getRecords(), CategoryListVO.class);
        // 返回结果
        return new PageResult<>(page.getTotal(), categoryListVOS);
    }

    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        Category category = BeanUtil.copyProperties(categoryDTO, Category.class);
        // 检查分类名称是否已存在
        if (categoryMapper.exists(new LambdaQueryWrapper<Category>().eq(Category::getName, category.getName()))) {
            throw new SystemException(CategoryConstants.NAME_IS_EXIST);
        }
        save(category);
    }

    @Override
    public void delete(Long id) {
        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Category::getDelFlag, CategoryConstants.CATEGORY_DELETED)
                .eq(Category::getId, id)
                .eq(Category::getDelFlag, CategoryConstants.CATEGORY_NOT_DELETED);

        update(updateWrapper);
    }

    @Override
    public CategoryVO getCategoryById(Long id) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getId, id)
                .eq(Category::getDelFlag, CategoryConstants.CATEGORY_NOT_DELETED);

        Category category = categoryMapper.selectOne(queryWrapper);
        return BeanUtil.copyProperties(category, CategoryVO.class);
    }

    /*@Override
    public List<CategoryVO> listCategory() {
        // 从缓存中获取分类列表
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(CategoryConstants.CATEGORY_CACHE_KEY);
        if (!entries.isEmpty()) {
            // 如果缓存中存在，直接返回
            List<CategoryVO> categoryList = entries.entrySet().stream()
                    .map(entry -> new CategoryVO(
                            Long.valueOf(entry.getKey().toString()),
                            entry.getValue().toString()))
                    .toList();

            return categoryList;
        }

        // 如果缓存中不存在，从数据库查询
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getDelFlag, CategoryConstants.CATEGORY_NOT_DELETED)
                .gt(Category::getRefer_cnt, CategoryConstants.CATEGORY_NO_REFERENCE);
        List<Category> categories = list(queryWrapper);

        // 将查询结果转换为VO对象
        List<CategoryVO> categoryVOs = BeanUtil.copyToList(categories, CategoryVO.class);

        // 将结果存入缓存
        Map<String, String> categoryCacheMap = categoryVOs.stream()
                .collect(Collectors.toMap(
                        categoryVO -> String.valueOf(categoryVO.getId()),
                        CategoryVO::getName,
                        (existing, replacement) -> existing)); // 如果有重复键，保留第一个

        stringRedisTemplate.opsForHash().putAll(CategoryConstants.CATEGORY_CACHE_KEY, categoryCacheMap);
        return categoryVOs;
    }*/

    // 使用Hash存储分类列表到Redis缓存来代替Set
    /*@Override
    public ResponseResult<List<CategoryVO>> listCategory() {
        // 从缓存中获取分类列表
        Set<String> categorySet = stringRedisTemplate.opsForSet().members(CategoryConstants.CATEGORY_LIST_KEY);
        if (categorySet != null && !categorySet.isEmpty()) {
            // 如果缓存中存在，直接返回
            List<CategoryVO> categoryList = categorySet.stream()
                    .map(StringUtils::categoryCacheOut)
                    .filter(parsedResult -> !"-1".equals(parsedResult[0]))
                    .map(parsedResult -> new CategoryVO(Long.valueOf(parsedResult[0]), parsedResult[1]))
                    .collect(Collectors.toList());

            return ResponseResult.okResult(categoryList);
        }

        // 如果缓存中不存在，从数据库查询
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getDelFlag, CategoryConstants.CATEGORY_NOT_DELETED)
                .gt(Category::getRefer_cnt, CategoryConstants.CATEGORY_NO_REFERENCE);
        List<Category> categories = list(queryWrapper);

        // 将查询结果转换为VO对象
        List<CategoryVO> categoryVOs = BeanUtil.copyToList(categories, CategoryVO.class);

        // 将结果存入缓存
        List<String> categoryCacheList = categoryVOs.stream()
                .map(StringUtils::categoryCacheIn)
                .filter(Objects::nonNull)
                .toList();

        stringRedisTemplate.opsForSet().add(CategoryConstants.CATEGORY_LIST_KEY, categoryCacheList.toArray(new String[0]));

        return ResponseResult.okResult(categoryVOs);
    }*/

}

