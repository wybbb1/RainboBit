package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.CategoryConstants;
import com.wybbb.rainbobit.common.constants.SystemConstants;
import com.wybbb.rainbobit.common.constants.TagConstants;
import com.wybbb.rainbobit.common.utils.RedisCacheHelper;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.mapper.TagMapper;
import com.wybbb.rainbobit.pojo.entity.Category;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.dto.TagDTO;
import com.wybbb.rainbobit.pojo.entity.Tag;
import com.wybbb.rainbobit.pojo.vo.CategoryVO;
import com.wybbb.rainbobit.pojo.vo.TagVO;
import com.wybbb.rainbobit.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 标签(Tag)表服务实现类
 *
 * @author Ra1nbot
 * @since 2025-07-31 17:32:48
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Resource
    private TagMapper tagMapper;
    @Resource
    private RedisCacheHelper redisCacheHelper;

    @Override
    public PageResult<Tag> getTag(TagDTO tagDTO, PageQuery pageQuery) {
        Page<Tag> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());

        String name = tagDTO.getName();
        String remark = tagDTO.getRemark();
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getDelFlag, TagConstants.NOT_DELETED)
                .like(Objects.nonNull(name) && !name.isBlank(), Tag::getName, tagDTO.getName())
                .like(Objects.nonNull(remark) && !remark.isBlank(), Tag::getRemark, tagDTO.getRemark());

        page(page, wrapper);
        return new PageResult<>(page.getTotal(), page.getRecords());
    }

    @Override
    public void addTag(TagDTO tagDTO) {
        String name = tagDTO.getName();
        String remark = tagDTO.getRemark();

        if (Objects.nonNull(name) && !name.isBlank()
        && Objects.nonNull(remark) && !remark.isBlank()) {
            Tag tag = BeanUtil.copyProperties(tagDTO, Tag.class);
            save(tag);
        }else{
            throw new SystemException(TagConstants.NAME_OR_REMARK_NOT_BLANK);
        }
    }

    @Override
    public TagVO getTagById(Long id) {
        if (id == null || id <= 0) {
            throw new SystemException(TagConstants.INVALID_ID);
        }

        Tag tag = getById(id);
        if (Objects.isNull(tag)) {
            throw new SystemException(TagConstants.NOT_FOUND);
        }

        return BeanUtil.copyProperties(tag, TagVO.class);
    }

    @Override
    public void updateTag(TagDTO tagDTO) {
        Tag tag = BeanUtil.copyProperties(tagDTO, Tag.class);

        // 检查分类名称是否已存在（排除当前分类）
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getName, tag.getName())
                .ne(Tag::getId, tag.getId())
                .eq(Tag::getDelFlag, TagConstants.NOT_DELETED);

        if (tagMapper.exists(queryWrapper)) {
            throw new SystemException(TagConstants.NAME_IS_EXIST);
        }

        updateById(tag);

        redisCacheHelper.deleteMap(TagConstants.CACHE_KEY);
    }

//    @Override
//    public List<TagVO> listAllTags() {
//        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Tag::getDelFlag, TagConstants.NOT_DELETED)
//                .orderByAsc(Tag::getId);
//
//        List<Tag> tags = list(wrapper);
//        if (tags.isEmpty()) {
//            throw new SystemException(TagConstants.NOT_FOUND);
//        }
//
//        return BeanUtil.copyToList(tags, TagVO.class);
//    }

    @Override
    public List<TagVO> listAllTags() {
        // 从缓存中获取分类列表
        Map<Long, String> map = redisCacheHelper.getMap(
                TagConstants.CACHE_KEY,
                Long.class,
                String.class,
                () -> {
                    // 如果缓存中不存在，从数据库查询
                    LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(Tag::getDelFlag, TagConstants.NOT_DELETED);
                    List<Tag> tags = list(queryWrapper);

                    // 将查询结果转换为VO对象
                    List<TagVO> categoryVOs = BeanUtil.copyToList(tags, TagVO.class);

                    return categoryVOs.stream()
                            .collect(Collectors.toMap(
                                    TagVO::getId,
                                    TagVO::getName,
                                    (existing, replacement) -> existing));// 如果有重复键，保留第一个
                });

        if (map.isEmpty()) {
            throw new RuntimeException(SystemConstants.RUNTIME_ERROR);
        }

        // 将Map转换为List<TagVO>
        return map.entrySet().stream()
                .map(entry -> new TagVO(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Transactional
    @Override
    public void removeTagById(Long id) {
        if (id == null || id <= 0) {
            throw new SystemException(TagConstants.INVALID_ID);
        }
        if (tagMapper.relateToArticle(id) > 0) {
            throw new SystemException(TagConstants.RELATED_TO_ARTICLE);
        }

        removeById(id);

        redisCacheHelper.deleteMap(TagConstants.CACHE_KEY);
    }
}

