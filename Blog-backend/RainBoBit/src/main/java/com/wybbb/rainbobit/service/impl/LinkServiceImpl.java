package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.LinkConstants;
import com.wybbb.rainbobit.common.utils.RedisCacheHelper;
import com.wybbb.rainbobit.mapper.LinkMapper;
import com.wybbb.rainbobit.pojo.entity.Link;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.vo.LinkVO;
import com.wybbb.rainbobit.service.LinkService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author Ra1nbot
 * @since 2025-07-25 17:51:14
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Resource
    private LinkMapper linkMapper;
    @Resource
    private RedisCacheHelper redisCacheHelper;

    @Override
    public List<LinkVO> getAllLink() {
        return redisCacheHelper.getList(
                LinkConstants.LINK_CACHE_KEY,
                LinkVO.class,
                () -> {
                    List<Link> links = linkMapper.selectList(new LambdaQueryWrapper<Link>()
                            .eq(Link::getStatus, LinkConstants.LINK_STATUS_NORMAL)
                            .eq(Link::getDelFlag, LinkConstants.LINK_NOT_DELETED)
                            .orderByAsc(Link::getCreateTime));

                    return BeanUtil.copyToList(links, LinkVO.class);
                }
        );
    }

    @Override
    public PageResult<LinkVO> getLinkList(PageQuery pageQuery, String name, String status) {
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Link::getName, name)
                .eq(status != null, Link::getStatus, status)
                .eq(Link::getDelFlag, LinkConstants.LINK_NOT_DELETED)
                .orderByDesc(Link::getCreateTime);

        Page<Link> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
        page(page, queryWrapper);

        List<LinkVO> linkVOList = BeanUtil.copyToList(page.getRecords(), LinkVO.class);
        return new PageResult<>(page.getTotal(), linkVOList);
    }

    @Override
    public void delete(Long id) {
        LambdaUpdateWrapper<Link> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Link::getDelFlag, LinkConstants.LINK_DELETED)
                .eq(Link::getId, id);

        update(wrapper);
    }

    @Override
    public PageResult<LinkVO> listDeletedLinks(PageQuery pageQuery, String name, String status) {
        Page<Link> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
        
        LambdaQueryWrapper<Link> wrapper = new LambdaQueryWrapper<>();
        // 查询已删除的友链
        wrapper.eq(Link::getDelFlag, LinkConstants.LINK_DELETED);
        
        // 根据名称模糊查询
        if (name != null && !name.isBlank()) {
            wrapper.like(Link::getName, name);
        }
        
        // 根据状态查询
        if (status != null && !status.isBlank()) {
            wrapper.eq(Link::getStatus, status);
        }
        
        // 按创建时间降序排列
        wrapper.orderByDesc(Link::getCreateTime);

        page(page, wrapper);
        
        List<Link> links = page.getRecords();
        List<LinkVO> linkVOs = BeanUtil.copyToList(links, LinkVO.class);
        
        return new PageResult<>(page.getTotal(), linkVOs);
    }

    @Override
    public void restoreLink(Long id) {
        // 恢复友链（逻辑删除标记改为未删除）
        LambdaUpdateWrapper<Link> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Link::getDelFlag, LinkConstants.LINK_NOT_DELETED)
                .eq(Link::getId, id);

        update(wrapper);
    }

    @Override
    public void permanentDeleteLink(Long id) {
        // 永久删除友链（物理删除）
        removeById(id);
    }

    @Override
    public void batchRestoreLinks(java.util.List<Long> ids) {
        // 批量恢复友链
        if (ids != null && !ids.isEmpty()) {
            LambdaUpdateWrapper<Link> wrapper = new LambdaUpdateWrapper<>();
            wrapper.set(Link::getDelFlag, LinkConstants.LINK_NOT_DELETED)
                    .in(Link::getId, ids);
            update(wrapper);
        }
    }

    @Override
    public void batchPermanentDeleteLinks(java.util.List<Long> ids) {
        // 批量永久删除友链
        if (ids != null && !ids.isEmpty()) {
            removeByIds(ids);
        }
    }
}

