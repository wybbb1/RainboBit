package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.CategoryConstants;
import com.wybbb.rainbobit.common.constants.SystemConstants;
import com.wybbb.rainbobit.common.constants.TagConstant;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
                CategoryConstants.CACHE_KEY,
                Long.class,
                String.class,
                () -> {
                    // 如果缓存中不存在，从数据库查询
                    LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(Category::getDelFlag, CategoryConstants.NOT_DELETED)
                            .gt(Category::getRefer_cnt, CategoryConstants.NO_REFERENCE);
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
            ResponseResult<?> result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result));
        }
    }

    @Override
    public void importCategory(MultipartFile file) {
        try {
            // 校验文件是否为空
            if (file.isEmpty()) {
                throw new SystemException("导入文件不能为空");
            }

            // 读取Excel文件
            List<ExcelCategoryVO> excelCategoryVOList = EasyExcel.read(file.getInputStream())
                    .head(ExcelCategoryVO.class)
                    .sheet()
                    .doReadSync();

            if (excelCategoryVOList.isEmpty()) {
                throw new SystemException("导入文件内容为空");
            }

            // 转换为Category实体
            List<Category> categories = BeanUtil.copyToList(excelCategoryVOList, Category.class);
            
            // 获取现有的分类名称，避免重复导入
            List<String> existingNames = list(new LambdaQueryWrapper<Category>()
                    .eq(Category::getDelFlag, CategoryConstants.NOT_DELETED))
                    .stream()
                    .map(Category::getName)
                    .toList();
            
            // 过滤掉已存在的分类名称和无效数据
            List<Category> categoriesToSave = categories.stream()
                    .filter(category -> category.getName() != null && !category.getName().trim().isEmpty())
                    .filter(category -> !existingNames.contains(category.getName()))
                    .collect(Collectors.toList());
            
            // 批量保存新分类
            if (!categoriesToSave.isEmpty()) {
                // 设置默认值
                categoriesToSave.forEach(category -> {
                    if (category.getStatus() == null) {
                        category.setStatus("0"); // 设置默认状态为正常
                    }
                    if (category.getDescription() == null) {
                        category.setDescription("");
                    }
                    category.setDelFlag(Integer.valueOf(CategoryConstants.NOT_DELETED));
                    category.setRefer_cnt(0); // 设置初始引用计数为0
                });
                
                saveBatch(categoriesToSave);
                
                // 清除分类缓存，让缓存重新加载
                redisCacheHelper.deleteMap(CategoryConstants.CACHE_KEY);
            }
            
        } catch (SystemException e) {
            throw e;
        } catch (Exception e) {
            throw new SystemException("导入分类失败：" + e.getMessage());
        }
    }

    @Override
    public PageResult<CategoryListVO> getCategoryList(PageQuery pageQuery, String name, String status) {
        // 构建查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getDelFlag, CategoryConstants.NOT_DELETED)
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
    public CategoryVO getCategoryById(Long id) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getId, id)
                .eq(Category::getDelFlag, CategoryConstants.NOT_DELETED);

        Category category = categoryMapper.selectOne(queryWrapper);
        return BeanUtil.copyProperties(category, CategoryVO.class);
    }

    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        Category category = BeanUtil.copyProperties(categoryDTO, Category.class);
        
        // 检查分类名称是否已存在（排除当前分类）
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, category.getName())
                .ne(Category::getId, category.getId())
                .eq(Category::getDelFlag, CategoryConstants.NOT_DELETED);
        
        if (categoryMapper.exists(queryWrapper)) {
            throw new SystemException(CategoryConstants.NAME_IS_EXIST);
        }
        
        updateById(category);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new SystemException(TagConstant.INVALID_TAG_ID);
        }
        if (categoryMapper.relateToArticle(id) > 0) {
            throw new SystemException(TagConstant.RELATED_TO_ARTICLE);
        }

        removeById(id);
    }
}

