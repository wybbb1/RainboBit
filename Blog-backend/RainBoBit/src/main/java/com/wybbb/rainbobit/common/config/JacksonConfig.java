package com.wybbb.rainbobit.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson配置类
 * 主要用于解决前端JavaScript数字精度问题
 * 将Long类型序列化为字符串，避免大数值在前端丢失精度
 * 
 * @author Ra1nbot
 * @since 2025-08-09
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置ObjectMapper，将Long类型序列化为字符串
     * 这样可以避免JavaScript中大整数精度丢失的问题
     * 
     * @param builder Jackson2ObjectMapperBuilder
     * @return ObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        
        // 创建一个简单模块来处理Long类型的序列化
        SimpleModule simpleModule = new SimpleModule();
        
        // 将Long和long类型都序列化为字符串
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        
        // 注册模块
        objectMapper.registerModule(simpleModule);
        
        return objectMapper;
    }
}
