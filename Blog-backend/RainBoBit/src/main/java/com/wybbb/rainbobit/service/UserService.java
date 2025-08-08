package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.dto.UserDTO;
import com.wybbb.rainbobit.pojo.dto.UserLoginDTO;
import com.wybbb.rainbobit.pojo.other.PageResult;
import com.wybbb.rainbobit.pojo.vo.*;
import com.wybbb.rainbobit.pojo.entity.User;
import com.wybbb.rainbobit.pojo.other.PageQuery;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * 用户表(SysUser)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-07-26 11:11:33
 */
public interface UserService extends IService<User> {

    UserLoginVo login(Integer type, UserLoginDTO userLoginDTO);

    void logout();

    UserInfoVO getUserInfo();

    String upload(MultipartFile img);

    void updateUserInfo(User user);

    void register(User user);

    AdminUserInfoVO adminGetInfo();

    PageResult<UserVO> listUser(PageQuery query, String username, String phonenumber, String status);

    void add(UserDTO userDTO);

    void delete(Long id);

    UserUpdateVO getUser(Long id);

    void update(UserDTO userDTO);
}

