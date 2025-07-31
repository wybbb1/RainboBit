package com.wybbb.rainbobit;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableScheduling
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "RainBoBit博客系统API", version = "1.0", description = "RainBoBit个人博客系统接口文档"))
public class RainBoBitApplication {

    public static void main(String[] args) {
        SpringApplication.run(RainBoBitApplication.class, args);
    }

}
