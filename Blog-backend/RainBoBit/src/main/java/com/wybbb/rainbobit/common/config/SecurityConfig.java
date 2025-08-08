package com.wybbb.rainbobit.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.wybbb.rainbobit.common.filter.JwtAuthenticationFilter;

import jakarta.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Resource
    private AuthenticationEntryPoint authenticationEntryPoint; // 自定义认证入口
    @Resource
    private AccessDeniedHandler accessDeniedHandler; // 自定义授权处理器

//    @Bean
//    public UserDetailsService userDetailsService() {
//        // 在内存中创建一个用户，用户名为 "user"，密码为 "password"，角色为 "USER"
//        UserDetails user = User.withUsername("user")
//                .password(passwordEncoder().encode("password"))
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }

//    @Bean
//    public UserDetailsService userDetailsService(){
//        return new UserDetailServiceImpl();
//    }


    // 密码编码器：用于加密和验证密码
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 配置安全过滤器链
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 配置会话管理策略为无状态
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorize -> authorize
                        // 对于登录接口 允许匿名访问
                        .requestMatchers("/login", "/user/login").permitAll()
                        .anyRequest().permitAll()
                ).logout(AbstractHttpConfigurer::disable);


        //把jwtAuthenticationTokenFilter添加到SpringSecurity的过滤器链中
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        //
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint) // 配置认证入口点
                .accessDeniedHandler(accessDeniedHandler) // 配置访问拒绝处理器
        );

            // 禁用 CSRF 保护 (仅用于测试或某些特殊场景，生产环境不建议禁用)
            // .csrf(csrf -> csrf.disable());

            // 禁用 H2-console 的 X-Frame-Options 保护，如果你使用 H2 数据库
            // .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }
}