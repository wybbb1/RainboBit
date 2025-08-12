package com.wybbb.rainbobit.common.filter;

import com.alibaba.fastjson.JSON;
import com.wybbb.rainbobit.common.constants.JwtClaimsConstant;
import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import com.wybbb.rainbobit.common.prop.JwtProperties;
import com.wybbb.rainbobit.common.utils.JwtUtil;
import com.wybbb.rainbobit.common.utils.RedisCacheHelper;
import com.wybbb.rainbobit.common.utils.WebUtils;
import com.wybbb.rainbobit.pojo.other.ResponseResult;
import com.wybbb.rainbobit.pojo.entity.LoginUser;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private RedisCacheHelper redisCacheHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的 JWT
        String jwt = request.getHeader("token");
        if (Objects.isNull(jwt) || jwt.isBlank()) {
            // 如果没有 JWT，直接放行
            filterChain.doFilter(request, response);
            return;
        }
        // 解析 JWT
        Claims claims = null;
        try {
            claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(), jwt);
        } catch (Exception e) {
            //token超时  token非法
            //响应告诉前端需要重新登录
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            WebUtils.renderString(response, JSON.toJSONString(result));
            return;
        }

        String userId = claims.get(JwtClaimsConstant.ID).toString();
        LoginUser loginUser = redisCacheHelper.getCacheObject(UserConstants.CACHE_KEY + userId, LoginUser.class);
        // 存入SecurityContext
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 放行
        filterChain.doFilter(request, response);
    }
}
