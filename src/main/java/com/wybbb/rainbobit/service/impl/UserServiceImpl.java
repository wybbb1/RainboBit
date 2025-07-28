package com.wybbb.rainbobit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.JwtClaimsConstant;
import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.common.prop.JwtProperties;
import com.wybbb.rainbobit.common.utils.JwtUtil;
import com.wybbb.rainbobit.common.utils.RedisCacheHelper;
import com.wybbb.rainbobit.mapper.UserMapper;
import com.wybbb.rainbobit.pojo.dto.UserLoginDTO;
import com.wybbb.rainbobit.pojo.entity.BlogUserLoginVo;
import com.wybbb.rainbobit.pojo.entity.LoginUser;
import com.wybbb.rainbobit.pojo.entity.User;
import com.wybbb.rainbobit.pojo.vo.UserInfoVO;
import com.wybbb.rainbobit.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 用户表(SysUser)表服务实现类
 *
 * @author Ra1nbot
 * @since 2025-07-26 11:11:33
 */
@Service("UserService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private RedisCacheHelper redisCacheHelper;
    @Resource
    private UserDetailsService userDetailsService;

    @Override
    public BlogUserLoginVo login(UserLoginDTO userLoginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userLoginDTO.getUserName(), userLoginDTO.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        // 如果认证成功，获取用户信息，生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        Long userId = loginUser.getUser().getId();
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.BLOG_USER_ID, userId);
        claims.put(JwtClaimsConstant.BLOG_USER_NAME, loginUser.getUser().getUserName());

        // 将用户信息存入Redis
        redisCacheHelper.setCacheObject(UserConstants.BLOG_USER_CACHE_KEY + userId.toString(), LoginUser.class, loginUser);

        String token = JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);

        // 把token和userInfo封装成返回对象
        UserInfoVO userInfoVo = new UserInfoVO();
        BeanUtils.copyProperties(loginUser.getUser(), userInfoVo);

        return new BlogUserLoginVo(token, userInfoVo);
    }

    @Override
    public void logout() {
        // 获取当前用户的ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUser().getId();

        if (Objects.isNull(userId)) {
            return;
        }

        // 从Redis中删除用户信息
        redisCacheHelper.delCacheObject(UserConstants.BLOG_USER_CACHE_KEY + userId.toString());

        // 清除SecurityContext中的认证信息
        SecurityContextHolder.clearContext();

        return;
    }


}

