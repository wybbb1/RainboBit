package com.wybbb.rainbobit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.mapper.TagMapper;
import com.wybbb.rainbobit.pojo.PageQuery;
import com.wybbb.rainbobit.pojo.PageResult;
import com.wybbb.rainbobit.pojo.dto.TagDTO;
import com.wybbb.rainbobit.pojo.entity.Tag;
import com.wybbb.rainbobit.service.TagService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 标签(Tag)表服务实现类
 *
 * @author Ra1nbot
 * @since 2025-07-31 17:32:48
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public PageResult<Tag> getTag(TagDTO tagDTO, PageQuery pageQuery) {
        Page<Tag> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());

        String name = tagDTO.getName();
        String remark = tagDTO.getRemark();
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Objects.nonNull(name) && !name.isBlank(), Tag::getName, tagDTO.getName())
                .like(Objects.nonNull(remark) && !remark.isBlank(), Tag::getRemark, tagDTO.getRemark());

        page(page, wrapper);
        return new PageResult<>(page.getTotal(), page.getRecords());
    }
}

