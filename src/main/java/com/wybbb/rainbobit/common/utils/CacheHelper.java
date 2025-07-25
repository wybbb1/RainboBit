package com.wybbb.rainbobit.common.utils;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
public class CacheHelper {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public <T> List<T> list(String key, Class<T> type, Supplier<List<T>> function) {
        List<String> t = stringRedisTemplate.opsForList().range(key, 0, -1);

        if (t != null && !t.isEmpty()) {
            return t.stream()
                    .map(typeJson -> JSONUtil.toBean(typeJson, type))
                    .collect(Collectors.toList());
        }

        // 如果缓存中没有，则查询数据库
        List<T> list = function.get();

        t = list.stream()
                .map(JSONUtil::toJsonStr)
                .collect(Collectors.toList());
        // 将查询结果存入缓存
        stringRedisTemplate.opsForList().rightPushAll(key, t);

        return list;
    }

}
