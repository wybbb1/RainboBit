package com.wybbb.rainbobit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wybbb.rainbobit.pojo.dto.UserLoginDTO;
import com.wybbb.rainbobit.pojo.entity.BlogUserLoginVo;
import com.wybbb.rainbobit.pojo.entity.User;


/**
 * 用户表(SysUser)表服务接口
 *
 * @author Ra1nbot
 * @since 2025-07-26 11:11:33
 */
public interface UserService extends IService<User> {

    BlogUserLoginVo login(UserLoginDTO userLoginDTO);

    void logout();
}

