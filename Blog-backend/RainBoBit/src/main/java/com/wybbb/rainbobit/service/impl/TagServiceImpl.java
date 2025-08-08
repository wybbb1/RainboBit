package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.TagConstant;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.mapper.TagMapper;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.dto.TagDTO;
import com.wybbb.rainbobit.pojo.entity.Tag;
import com.wybbb.rainbobit.pojo.vo.TagVO;
import com.wybbb.rainbobit.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 标签(Tag)表服务实现类
 *
 * @author Ra1nbot
 * @since 2025-07-31 17:32:48
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    private final TagMapper tagMapper;

    public TagServiceImpl(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @Override
    public PageResult<Tag> getTag(TagDTO tagDTO, PageQuery pageQuery) {
        Page<Tag> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());

        String name = tagDTO.getName();
        String remark = tagDTO.getRemark();
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getDelFlag, TagConstant.TAG_NOT_DELETED)
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
            throw new SystemException(TagConstant.TAG_NAME_OR_REMARK_NOT_BLANK);
        }
    }

    @Override
    public TagVO getTagById(Long id) {
        if (id == null || id <= 0) {
            throw new SystemException(TagConstant.INVALID_TAG_ID);
        }

        Tag tag = getById(id);
        if (Objects.isNull(tag)) {
            throw new SystemException(TagConstant.TAG_NOT_FOUND);
        }

        return BeanUtil.copyProperties(tag, TagVO.class);
    }

    @Override
    public void updateTag(TagDTO tagDTO) {
        if (Objects.isNull(tagDTO) || tagDTO.getId() == null || tagDTO.getId() <= 0) {
            throw new SystemException(TagConstant.INVALID_TAG_ID);
        }
        Tag tag = BeanUtil.copyProperties(tagDTO, Tag.class);
        updateById(tag);

    }

    @Override
    public List<TagVO> listAllTags() {
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getDelFlag, TagConstant.TAG_NOT_DELETED)
                .orderByAsc(Tag::getId);

        List<Tag> tags = list(wrapper);
        if (tags.isEmpty()) {
            throw new SystemException(TagConstant.NO_TAGS_FOUND);
        }

        return BeanUtil.copyToList(tags, TagVO.class);
    }

    @Transactional
    @Override
    public void removeTagById(Long id) {
        if (id == null || id <= 0) {
            throw new SystemException(TagConstant.INVALID_TAG_ID);
        }
        if (tagMapper.relateToArticle(id) > 0) {
            throw new SystemException(TagConstant.TAG_RELATED_TO_ARTICLE);
        }

        LambdaUpdateWrapper<Tag> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Tag::getDelFlag, TagConstant.TAG_DELETED)
                .eq(Tag::getId, id);
        update(wrapper);
    }
}

