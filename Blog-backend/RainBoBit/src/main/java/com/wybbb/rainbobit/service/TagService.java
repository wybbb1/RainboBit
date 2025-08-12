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

    // ========== 查询操作 ==========
    
    /**
     * 分页查询标签
     * @param tagDTO 标签查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<Tag> getTag(TagDTO tagDTO, PageQuery pageQuery);

    /**
     * 根据ID查询标签详情
     * @param id 标签ID
     * @return 标签详情
     */
    TagVO getTagById(Long id);

    /**
     * 查询所有标签列表
     * @return 标签列表
     */
    List<TagVO> listAllTags();

    // ========== 添加操作 ==========
    
    /**
     * 添加标签
     * @param tagDTO 标签信息
     */
    void addTag(TagDTO tagDTO);

    // ========== 更新操作 ==========
    
    /**
     * 更新标签
     * @param tagDTO 标签信息
     */
    void updateTag(TagDTO tagDTO);

    // ========== 删除操作 ==========
    
    /**
     * 根据ID删除标签
     * @param id 标签ID
     */
    void removeTagById(Long id);
}

