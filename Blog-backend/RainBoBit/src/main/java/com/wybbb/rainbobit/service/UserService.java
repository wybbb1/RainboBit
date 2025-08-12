package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.dto.RegisterUserForm;
import com.wybbb.rainbobit.pojo.dto.UserDTO;
import com.wybbb.rainbobit.pojo.dto.UserLoginDTO;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.vo.*;
import com.wybbb.rainbobit.pojo.entity.User;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import org.springframework.web.multipart.MultipartFile;



/**
 * 用户表(SysUser)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-07-26 11:11:33
 */
public interface UserService extends IService<User> {

    // ========== 查询操作 ==========
    
    /**
     * 获取用户信息
     * @return 用户信息
     */
    UserInfoVO getUserInfo();

    /**
     * 管理员获取用户信息
     * @return 管理员用户信息
     */
    AdminUserInfoVO adminGetInfo();

    /**
     * 分页查询用户列表
     * @param query 分页参数
     * @param username 用户名
     * @param phonenumber 手机号
     * @param status 用户状态
     * @return 分页结果
     */
    PageResult<UserVO> listUser(PageQuery query, String username, String phonenumber, String status);

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户更新信息
     */
    UserUpdateVO getUser(Long id);

    // ========== 添加操作 ==========
    
    /**
     * 用户注册
     * @param user 注册用户信息
     */
    void register(RegisterUserForm user);

    /**
     * 添加用户
     * @param userDTO 用户信息
     */
    void add(UserDTO userDTO);

    // ========== 更新操作 ==========
    
    /**
     * 更新用户信息
     * @param user 用户信息
     */
    void updateUserInfo(User user);

    /**
     * 更新用户
     * @param userDTO 用户信息
     */
    void update(UserDTO userDTO);

    // ========== 删除操作 ==========
    
    /**
     * 删除用户
     * @param id 用户ID
     */
    void delete(Long id);

    // ========== 认证授权操作 ==========
    
    /**
     * 用户登录
     * @param type 登录类型
     * @param userLoginDTO 登录信息
     * @return 登录结果
     */
    UserLoginVo login(Integer type, UserLoginDTO userLoginDTO);

    /**
     * 用户登出
     */
    void logout();

    // ========== 工具操作 ==========
    
    /**
     * 文件上传
     * @param img 图片文件
     * @return 文件路径
     */
    String upload(MultipartFile img);

    /**
     * 发送验证码
     * @param email 邮箱地址
     */
    void sendCode(String email);
}

