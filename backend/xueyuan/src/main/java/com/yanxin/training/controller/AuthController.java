package com.yanxin.training.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.yanxin.training.common.Result;
import com.yanxin.training.entity.User;
import com.yanxin.training.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<String> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String token = userService.login(username, password);
        return Result.success(token);
    }

    @GetMapping("/info")
    public Result<User> info() {
        long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null); // 清除密码返回
        }
        return Result.success(user);
    }
}