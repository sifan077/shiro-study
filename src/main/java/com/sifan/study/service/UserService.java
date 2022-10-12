package com.sifan.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sifan.study.domain.User;

import java.util.List;

/**
 * @author 思凡
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2022-10-12 16:04:33
 */
public interface UserService extends IService<User> {
    // 用户登陆
    User getUserInfoByName(String name);
    //获取用户的角色信息
    List<String> getUserRoleInfo(String principal);
}
