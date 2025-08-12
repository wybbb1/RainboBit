package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.entity.Link;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.vo.LinkVO;

import java.util.List;


/**
 * 友链(Link)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-07-25 17:51:14
 */
public interface LinkService extends IService<Link> {

    // ========== 查询操作 ==========
    
    /**
     * 获取所有友链
     * @return 友链列表
     */
    List<LinkVO> getAllLink();

    /**
     * 分页查询友链列表
     * @param pageQuery 分页参数
     * @param name 友链名称
     * @param status 友链状态
     * @return 分页结果
     */
    PageResult<LinkVO> getLinkList(PageQuery pageQuery, String name, String status);

    /**
     * 分页查询已删除的友链
     * @param pageQuery 分页参数
     * @param name 友链名称
     * @param url 友链地址
     * @return 分页结果
     */
    PageResult<LinkVO> listDeletedLinks(PageQuery pageQuery, String name, String url);

    // ========== 添加操作 ==========
    
    

    // ========== 更新操作 ==========
    
    /**
     * 恢复友链
     * @param id 友链ID
     */
    void restoreLink(Long id);

    /**
     * 批量恢复友链
     * @param ids 友链ID列表
     */
    void batchRestoreLinks(java.util.List<Long> ids);

    // ========== 删除操作 ==========
    
    /**
     * 删除友链（软删除）
     * @param id 友链ID
     */
    void delete(Long id);

    /**
     * 永久删除友链
     * @param id 友链ID
     */
    void permanentDeleteLink(Long id);

    /**
     * 批量永久删除友链
     * @param ids 友链ID列表
     */
    void batchPermanentDeleteLinks(java.util.List<Long> ids);
}

