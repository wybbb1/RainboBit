package com.wybbb.rainbobit.common.utils;

import cn.hutool.json.JSONUtil;
import com.wybbb.rainbobit.common.constants.ArticleConstants;
import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.pojo.entity.LoginUser;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RedisCacheHelper {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public <K, V> Map<K, V> getMap(String key, Class<K> keyType, Class<V> valueType, Supplier<Map<K, V>> function) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);

        if (!entries.isEmpty()) {
            return entries.entrySet().stream()
                    .collect(Collectors.toMap(
                            // 修正点1：keyType 的转换，同上次的建议，根据实际类型来转换
                            entry -> convertKey(entry.getKey(), keyType),
                            // 修正点2：Value 的转换
                            // 如果 targetType 是 String，直接返回字符串；否则，进行 JSON 解析
                            entry -> convertValue(entry.getValue().toString(), valueType)
                    ));
        }

        // 如果缓存中没有，则查询数据库
        Map<K, V> map = function.get();

        // 将查询结果存入缓存
        Map<String, String> cacheMap = map.entrySet().stream()
                .collect(Collectors.toMap(
                        // 键的序列化：直接转换为字符串，而不是 JSONUtil.toJsonStr()
                        entry -> String.valueOf(entry.getKey()), // 对于 Long, Integer, String 都可以直接 String.valueOf
                        // 值的序列化：如果值是 String，直接存入；否则，JSON 序列化
                        entry -> (entry.getValue() instanceof String) ? (String) entry.getValue() : JSONUtil.toJsonStr(entry.getValue())
                ));
        stringRedisTemplate.opsForHash().putAll(key, cacheMap);

        return map;
    }

    public void deleteMap(String key) {
        if (key != null && !key.isBlank()) {
            stringRedisTemplate.delete(key);
        } else {
            log.error(UserConstants.CACHE_VALUE_NULL);
        }
    }

    // 辅助方法：将 Redis 中的 Key 转换为目标类型 K
    private <T> T convertKey(Object redisKey, Class<T> targetType) {
        if (redisKey == null) {
            return null;
        }
        String strKey = redisKey.toString();
        if (targetType == Long.class || targetType == long.class) {
            return targetType.cast(Long.parseLong(strKey));
        }
        if (targetType == Integer.class || targetType == int.class) {
            return targetType.cast(Integer.parseInt(strKey));
        }
        if (targetType == String.class) {
            return targetType.cast(strKey);
        }
        // 对于非基本类型或它们的包装类，如果键本身就是JSON字符串，才用JSONUtil.toBean
        // 但通常Redis Hash的键是简单的字符串
        throw new IllegalArgumentException("Unsupported key type for Redis conversion: " + targetType.getName());
    }

    // 辅助方法：将 Redis 中的 Value 转换为目标类型 V
    private <T> T convertValue(String redisValue, Class<T> targetType) {
        if (redisValue == null) {
            return null;
        }
        if (targetType == Long.class || targetType == long.class) {
            return targetType.cast(Long.parseLong(redisValue));
        }
        if (targetType == Integer.class || targetType == int.class) {
            return targetType.cast(Integer.parseInt(redisValue));
        }
        if (targetType == String.class) {
            return targetType.cast(redisValue); // 如果目标类型是 String，直接返回
        }
        // 如果目标类型不是 String，则认为是需要 JSON 解析的复杂对象
        return JSONUtil.toBean(redisValue, targetType);
    }

    public <T> List<T> getList(String key, Class<T> type, Supplier<List<T>> function) {
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

    public <T> Set<T> getSet(String key, Class<T> type, Supplier<Set<T>> function) {
        Set<String> t = stringRedisTemplate.opsForSet().members(key);

        if (t != null && !t.isEmpty()) {
            return t.stream()
                    .map(typeJson -> JSONUtil.toBean(typeJson, type))
                    .collect(Collectors.toSet());
        }

        // 如果缓存中没有，则查询数据库
        Set<T> set = function.get();

        t = set.stream()
                .map(JSONUtil::toJsonStr)
                .collect(Collectors.toSet());
        // 将查询结果存入缓存
        stringRedisTemplate.opsForSet().add(key, t.toArray(new String[0]));

        return set;
    }

    public <T> void setCacheObject(String s, Class<T> type, T value) {
        if (value != null) {
            stringRedisTemplate.opsForValue().set(s, reconvertValue(value, type));
        }else{
            log.error(UserConstants.CACHE_VALUE_NULL);
            throw new SystemException(UserConstants.CACHE_VALUE_NULL);
        }
    }

    private <T> String reconvertValue(T value, Class<T> type) {
        if (type == Long.class || type == long.class) {
            return String.valueOf(value); // 如果类型是 Long，直接转换为字符串
        } else if (type == Integer.class || type == int.class) {
            return String.valueOf(value); // 如果类型是 Integer，直接转换为字符串
        } else if (type == String.class) {
            return (String) value; // 如果类型是 String，直接返回
        } else {
            return JSONUtil.toJsonStr(value); // 否则进行 JSON 序列化
        }
    }

    public <T> T getCacheObject(String s, Class<T> type) {
        String value = stringRedisTemplate.opsForValue().get(s);
        if (value != null) {
            return convertValue(value, type);
        } else {
            log.error(UserConstants.CACHE_VALUE_NULL);
            throw new SystemException(UserConstants.NEED_LOGIN);
        }
    }

    public void delCacheObject(String s) {
        if (s != null && !s.isBlank()) {
            stringRedisTemplate.delete(s);
        } else {
            log.error(UserConstants.CACHE_VALUE_NULL);
            throw new SystemException(UserConstants.CACHE_VALUE_NULL);
        }
    }

    public <T> T getCacheMapValue(String viewCountCacheKey, String id, Class<T> type) {
        Object value = stringRedisTemplate.opsForHash().get(viewCountCacheKey, id);
        if (value != null) {
            return convertValue(value.toString(), type);
        }
        return null;
    }

    public void setCacheMap(String key, Map<String, String> map) {
        if (map != null && !map.isEmpty()) {
            stringRedisTemplate.opsForHash().putAll(key, map);
        } else {
            log.error(UserConstants.CACHE_VALUE_NULL);
            throw new SystemException(UserConstants.CACHE_VALUE_NULL);
        }
    }

    public void incrementCacheMapValue(String key,String hKey,long v){
        stringRedisTemplate.opsForHash().increment(key,hKey,v);
    }

    public void incrementCacheValue(String key, long v) {
        stringRedisTemplate.opsForValue().increment(key, v);
    }


    public <K, V> Map<K, V> getCacheMap(String CacheKey, Class<K> type1, Class<V> type2) {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(CacheKey);

        return entries.entrySet().stream().map(
                entry -> Map.entry(
                        convertKey(entry.getKey(), type1),
                        convertValue(entry.getValue().toString(), type2)
                )
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void deleteMapValue(String statisticsCacheKey, String todayViewCountCacheKey) {
        if (statisticsCacheKey != null && !statisticsCacheKey.isBlank() && todayViewCountCacheKey != null && !todayViewCountCacheKey.isBlank()) {
            stringRedisTemplate.opsForHash().delete(statisticsCacheKey, todayViewCountCacheKey);
        } else {
            log.error(UserConstants.CACHE_VALUE_NULL);
            throw new SystemException(UserConstants.CACHE_VALUE_NULL);
        }
    }


    public <T> void setCacheMapValue(String mapCacheKey, String valueName, Class<T> type, T value) {
        if (value != null) {
            String stringValue = reconvertValue(value, type);
            stringRedisTemplate.opsForHash().put(mapCacheKey, valueName, stringValue);
        } else {
            log.error(UserConstants.CACHE_VALUE_NULL);
            throw new SystemException(UserConstants.CACHE_VALUE_NULL);
        }

    }
}
