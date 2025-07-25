package com.wybbb.rainbobit.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.LinkConstants;
import com.wybbb.rainbobit.common.utils.CacheHelper;
import com.wybbb.rainbobit.mapper.LinkMapper;
import com.wybbb.rainbobit.pojo.entity.Link;
import com.wybbb.rainbobit.pojo.vo.LinkVO;
import com.wybbb.rainbobit.service.LinkService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    private CacheHelper cacheHelper;

    @Override
    public List<LinkVO> getAllLink() {
        return cacheHelper.list(
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
}

