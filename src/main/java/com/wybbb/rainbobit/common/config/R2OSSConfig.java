package com.wybbb.rainbobit.common.config;

import com.wybbb.rainbobit.common.prop.R2OssProperties;
import com.wybbb.rainbobit.common.utils.R2OssUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class R2OSSConfig {

    @Bean
    @ConditionalOnMissingBean
    public R2OssUtil r2OssUtil(R2OssProperties r2OssProperties) {
        return new R2OssUtil(r2OssProperties);
    }
}
