package com.wybbb.rainbobit.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI文档配置类
 *
 * @author wybbb
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rainBoBitOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RainBoBit博客系统API")
                        .description("RainBoBit个人博客系统接口文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("wybbb")
                                .url("https://github.com/wybbb1/RainboBit"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}