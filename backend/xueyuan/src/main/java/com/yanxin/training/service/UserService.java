package com.yanxin.training.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanxin.training.entity.User;

public interface UserService extends IService<User> {
    String login(String username, String password);
}