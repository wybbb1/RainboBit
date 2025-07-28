package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.CategoryConstants;
import com.wybbb.rainbobit.common.constants.SystemConstants;
import com.wybbb.rainbobit.common.utils.RedisCacheHelper;
import com.wybbb.rainbobit.mapper.CategoryMapper;
import com.wybbb.rainbobit.pojo.entity.Category;
import com.wybbb.rainbobit.pojo.vo.CategoryVO;
import com.wybbb.rainbobit.service.CategoryService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
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

