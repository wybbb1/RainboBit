package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.PageQuery;
import com.wybbb.rainbobit.pojo.PageResult;
import com.wybbb.rainbobit.pojo.dto.TagDTO;
import com.wybbb.rainbobit.pojo.entity.Tag;


/**
 * 标签(Tag)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-07-31 17:32:48
 */
public interface TagService extends IService<Tag> {

    PageResult<Tag> getTag(TagDTO tagDTO, PageQuery pageQuery);
}

