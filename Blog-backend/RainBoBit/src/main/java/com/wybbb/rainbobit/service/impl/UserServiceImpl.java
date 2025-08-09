package com.wybbb.rainbobit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wybbb.rainbobit.common.constants.JwtClaimsConstant;
import com.wybbb.rainbobit.common.constants.RoleConstants;
import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import com.wybbb.rainbobit.common.prop.JwtProperties;
import com.wybbb.rainbobit.common.utils.JwtUtil;
import com.wybbb.rainbobit.common.utils.R2OssUtil;
import com.wybbb.rainbobit.common.utils.RedisCacheHelper;
import com.wybbb.rainbobit.common.utils.SecurityUtils;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.mapper.RoleMapper;
import com.wybbb.rainbobit.mapper.UserMapper;
import com.wybbb.rainbobit.pojo.dto.UserDTO;
import com.wybbb.rainbobit.pojo.dto.UserLoginDTO;
import com.wybbb.rainbobit.pojo.entity.Article;
import com.wybbb.rainbobit.pojo.entity.Role;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.vo.*;
import com.wybbb.rainbobit.pojo.entity.LoginUser;
import com.wybbb.rainbobit.pojo.entity.User;
import com.wybbb.rainbobit.service.MenuService;
import com.wybbb.rainbobit.service.RoleService;
import com.wybbb.rainbobit.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

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
    @Resource
    private MenuService menuService;
    @Resource
    private RoleService roleService;
    @Resource
    private UserMapper userMapper;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public UserLoginVo login(Integer type, UserLoginDTO userLoginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userLoginDTO.getUserName(), userLoginDTO.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);

        // 如果认证成功，获取用户信息，生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        Long userId = loginUser.getUser().getId();
        Map<String, Object> claims = new HashMap<>();
        if (type == UserConstants.ADMIN_LOGIN){
            claims.put(JwtClaimsConstant.USER_TYPE, UserConstants.ADMIN_LOGIN);
        }
        else if (type == UserConstants.USER_LOGIN){
            claims.put(JwtClaimsConstant.USER_TYPE, UserConstants.USER_LOGIN);
        }else{
            throw new SystemException(UserConstants.LOGIN_TYPE_ERROR);
        }

        claims.put(JwtClaimsConstant.USER_ID, userId);

        // 将用户信息存入Redis
        redisCacheHelper.setCacheObject(UserConstants.USER_CACHE_KEY + userId.toString(), LoginUser.class, loginUser);

        String token = JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);

        // 把token和userInfo封装成返回对象
        UserInfoVO userInfoVo = new UserInfoVO();
        BeanUtils.copyProperties(loginUser.getUser(), userInfoVo);

        return new UserLoginVo(token, userInfoVo);
    }

    @Override
    public void logout() {
        // 获取当前用户的ID
        Long userId = SecurityUtils.getUserId();

        if (Objects.isNull(userId)) {
            throw new SystemException(UserConstants.USER_NEED_LOGIN);
        }

        // 从Redis中删除用户信息
        redisCacheHelper.delCacheObject(UserConstants.USER_CACHE_KEY + userId.toString());

        // 清除SecurityContext中的认证信息
        SecurityContextHolder.clearContext();
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

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        save(user);
    }

    @Transactional
    @Override
    public AdminUserInfoVO adminGetInfo() {
        // 获取当前登录用户信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 获取用户权限信息
        List<String> perms = menuService.selectPermsByUserId(loginUser.getUser().getId());
        // 获取用户角色信息
        List<String> roles = roleService.selectRoleKeyByUserId(loginUser.getUser().getId());

        return new AdminUserInfoVO(
                perms,
                roles,
                BeanUtil.copyProperties(loginUser.getUser(), UserInfoVO.class)
        );
    }

    @Override
    public PageResult<UserVO> listUser(PageQuery query, String username, String phonenumber, String status) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(username != null && !username.isBlank(), User::getUserName, username)
                .eq(phonenumber != null && !phonenumber.isBlank(), User::getPhonenumber, phonenumber)
                .eq(status != null && !status.isBlank(), User::getStatus, status)
                .eq(User::getDelFlag, UserConstants.USER_NOT_DELETED) // 只查询未删除的用户
                .eq(User::getStatus, UserConstants.USER_NORMAL) // 只查询正常状态的用户
                .orderByDesc(User::getCreateTime);

        Page<User> page = new Page<>(query.getPage(), query.getPageSize());
        page(page, queryWrapper);

        List<UserVO> userVOS = BeanUtil.copyToList(page.getRecords(), UserVO.class);
        return new PageResult<>(page.getTotal(), userVOS);
    }

    @Override
    public void add(UserDTO userDTO) {
        if (userDTO.getUsername() == null || userDTO.getUsername().isBlank()){
            throw new SystemException(UserConstants.USERNAME_IS_NULL);
        }
        // 检查用户名是否已存在
        if (userMapper.exists(new LambdaQueryWrapper<>(User.class).eq(User::getUserName, userDTO.getUsername()))) {
            throw new SystemException(UserConstants.USERNAME_ALREADY_EXISTS);
        }
        // 检查邮箱是否已存在
        if (userMapper.exists(new LambdaQueryWrapper<>(User.class).eq(User::getEmail, userDTO.getEmail()))) {
            throw new SystemException(UserConstants.EMAIL_ALREADY_EXISTS);
        }
        // 检查手机号是否已存在
        if (userMapper.exists(new LambdaQueryWrapper<>(User.class).eq(User::getPhonenumber, userDTO.getPhonenumber()))) {
            throw new SystemException(UserConstants.PHONE_ALREADY_EXISTS);
        }

        User user = BeanUtil.copyProperties(userDTO, User.class);
        // 设置密码
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setUserName(userDTO.getUsername());
        user.setNickName(userDTO.getNickname());

        save(user);

        List<Long> roleIds = userDTO.getRoleIds();
        if (roleIds != null && !roleIds.isEmpty()){
            roleMapper.saveRolesByUserId(user.getId(), roleIds);
        }

    }

    @Override
    public void delete(Long id) {
        // 检查用户是否存在
        User user = getById(id);
        if (user == null) {
            throw new SystemException(UserConstants.USER_NOT_FOUND);
        }

        // 检查用户是否为超级管理员
        if (UserConstants.SUPER_ADMIN.equals(user.getId())) {
            throw new SystemException(UserConstants.CANNOT_DELETE_SUPER_ADMIN);
        }

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(User::getDelFlag, UserConstants.USER_DELETED)
                .eq(User::getId, id);
        update(updateWrapper);
    }

    @Override
    public UserUpdateVO getUser(Long id) {
        if (id == null) {
            throw new SystemException(UserConstants.User_ID_IS_NULL);
        }

        List<Role> roleIds = null;
        if (id.equals(UserConstants.SUPER_ADMIN)) {
            LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Role::getStatus, RoleConstants.STATUS_NORMAL)
                    .eq(Role::getDelFlag, RoleConstants.NOT_DELETED)
                    .orderByAsc(Role::getRoleSort);
            roleIds = roleMapper.selectList(queryWrapper);
        }else {
            roleIds = roleMapper.selectByUserId(id);
        }
        List<Long> roleIdList = roleIds.stream().map(Role::getId).toList();

        // 查询所有正常状态的角色
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getStatus, RoleConstants.STATUS_NORMAL)
                .eq(Role::getDelFlag, RoleConstants.NOT_DELETED)
                .orderByAsc(Role::getRoleSort);
        List<Role> roles = roleMapper.selectList(queryWrapper);
        // 将角色列表转换为VO对象
        List<RoleVO> roleVOS = BeanUtil.copyToList(roles, RoleVO.class);

        UserVO userVO = BeanUtil.copyProperties(userMapper.selectById(id), UserVO.class);
        return new UserUpdateVO(roleIdList, roleVOS, userVO);
    }

    @Transactional
    @Override
    public void update(UserDTO userDTO) {
        User user = BeanUtil.copyProperties(userDTO, User.class);
        user.setNickName(userDTO.getNickname());
        updateById(user);

        // 更新文章标签
        Long id = user.getId();
        List<Long> roleIds = userDTO.getRoleIds();
        roleMapper.deleteByUserId(id);

        if (!roleIds.isEmpty()){
            roleMapper.saveRolesByUserId(id, roleIds);
        }
    }


}

