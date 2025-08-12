package com.wybbb.rainbobit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wybbb.rainbobit.common.constants.UserConstants;
import com.wybbb.rainbobit.common.enums.AppHttpCodeEnum;
import com.wybbb.rainbobit.exception.SystemException;
import com.wybbb.rainbobit.mapper.MenuMapper;
import com.wybbb.rainbobit.mapper.UserMapper;
import com.wybbb.rainbobit.pojo.entity.LoginUser;
import com.wybbb.rainbobit.pojo.entity.Menu;
import com.wybbb.rainbobit.pojo.entity.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private MenuMapper menuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, username)
                .eq(User::getDelFlag, UserConstants.NOT_DELETED);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null){
            throw new UsernameNotFoundException(UserConstants.NOT_FOUND);
        }

        if (Objects.equals(user.getType(), String.valueOf(UserConstants.ADMIN_LOGIN))) {
            List<String> list = menuMapper.selectByUserId(user.getId()).stream()
                    .map(Menu::getPerms).toList();
            return new LoginUser(user, list);
        }
        return new LoginUser(user, null);
    }
}
