package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.dto.TagDTO;
import com.wybbb.rainbobit.pojo.entity.Tag;
import com.wybbb.rainbobit.pojo.vo.TagVO;

import java.util.List;


/**
 * 标签(Tag)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-07-31 17:32:48
 */
public interface TagService extends IService<Tag> {

    PageResult<Tag> getTag(TagDTO tagDTO, PageQuery pageQuery);

    void addTag(TagDTO tagDTO);

    TagVO getTagById(Long id);

    void updateTag(TagDTO tagDTO);

    List<TagVO> listAllTags();

    void removeTagById(Long id);
}

