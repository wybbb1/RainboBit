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
}

