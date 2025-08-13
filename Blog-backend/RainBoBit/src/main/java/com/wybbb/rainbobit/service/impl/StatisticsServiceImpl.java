package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.wybbb.rainbobit.common.constants.StatisticsConstants;
import com.wybbb.rainbobit.common.utils.RedisCacheHelper;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.pojo.vo.DashboardStatisticsVO;
import com.wybbb.rainbobit.service.StatisticsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Resource
    private RedisCacheHelper redisCacheHelper;

    @Override
    public DashboardStatisticsVO getDashboardStatistics() {
        Map<String, Long> cacheMap = redisCacheHelper.getCacheMap(StatisticsConstants.STATISTICS_CACHE_KEY, String.class, Long.class);
        if (cacheMap == null || cacheMap.isEmpty()) {
            throw new SystemException(StatisticsConstants.DATA_NOT_FOUND_ERROR);
        }

        return BeanUtil.copyProperties(cacheMap, DashboardStatisticsVO.class);
    }
}
