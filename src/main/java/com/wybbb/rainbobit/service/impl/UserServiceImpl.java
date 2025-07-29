package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.JwtClaimsConstant;
import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import com.wybbb.rainbobit.common.prop.JwtProperties;
import com.wybbb.rainbobit.common.utils.JwtUtil;
import com.wybbb.rainbobit.common.utils.R2OssUtil;
import com.wybbb.rainbobit.common.utils.RedisCacheHelper;
import com.wybbb.rainbobit.common.utils.SecurityUtils;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.mapper.UserMapper;
import com.wybbb.rainbobit.pojo.dto.UserLoginDTO;
import com.wybbb.rainbobit.pojo.entity.BlogUserLoginVo;
import com.wybbb.rainbobit.pojo.entity.LoginUser;
import com.wybbb.rainbobit.pojo.entity.User;
import com.wybbb.rainbobit.pojo.vo.UserInfoVO;
import com.wybbb.rainbobit.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * 用户表(SysUser)表服务实现类
 *
 * @author Ra1nbot
 * @since 2025-07-26 11:11:33
 */
@Service("UserService")
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private RedisCacheHelper redisCacheHelper;
    @Resource
    private R2OssUtil r2OssUtil;

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

    @Override
    public UserInfoVO getUserInfo() {
        Long userId = SecurityUtils.getUserId();
        User user = getById(userId);
        if (Objects.isNull(user)) {
            throw new SystemException(AppHttpCodeEnum.NEED_LOGIN);
        }
        return BeanUtil.copyProperties(user, UserInfoVO.class);
    }

    @Override
    public String upload(MultipartFile img) {
        String originalFilename = img.getOriginalFilename();

        if (originalFilename != null && !originalFilename.endsWith(".png")) {
            throw new SystemException(UserConstants.FILE_TYPE_ERROR);
        }

        // 1. 获取文件基本信息
        long size = img.getSize();
        String contentType = img.getContentType();
        // 2. 生成文件名
        String objectName = img.getOriginalFilename();
        // 3. 上传文件到R2
        try (InputStream inputStream = img.getInputStream()) {
            String fileUrl = r2OssUtil.upload(inputStream, objectName, size, contentType);

            if (fileUrl != null) {
                log.info("文件上传成功: {}，访问URL: {}", originalFilename, fileUrl);
                return fileUrl;
            } else {
                log.error("文件上传失败: {}，R2OssUtil未能返回URL。", originalFilename);
                // R2OssUtil 内部应该已经记录了更详细的S3Exception日志
                throw new SystemException(UserConstants.FILE_UPLOAD_ERROR);
            }
        } catch (Exception e) {
            log.error("文件上传时发生未知错误: {} : {}", originalFilename, e.getMessage(), e);
            throw new SystemException(UserConstants.FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public void updateUserInfo(User user) {
        //TODO:或许存在安全问题，因为是直接传的用户id
        updateById(user);
    }

    @Override
    public void register(User user) {
        //TODO:可以更新为邮箱注册
        if (user.getUserName() == null || user.getUserName().isBlank()){
            throw new SystemException(UserConstants.USERNAME_IS_NULL);
        }
        if (user.getPassword() == null || user.getPassword().isBlank()){
            throw new SystemException(UserConstants.PASSWORD_IS_NULL);
        }
        if (user.getEmail() == null || user.getEmail().isBlank()){
            throw new SystemException(UserConstants.EMAIL_IS_NULL);
        }
        if (user.getNickName() == null || user.getNickName().isBlank()){
            throw new SystemException(UserConstants.NICKNAME_IS_NULL);
        }

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, user.getUserName());
        User existingUser = getOne(queryWrapper);
        if (existingUser != null) {
            throw new SystemException(UserConstants.USERNAME_ALREADY_EXISTS);
        }

        save(user);
    }


}

