package com.yanxin.training.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.training.common.Result;
import com.yanxin.training.entity.User;
import com.yanxin.training.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/page")
    public Result<Page<User>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Long roleId) {
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(realName)) {
            wrapper.like(User::getRealName, realName);
        }
        if (StringUtils.hasText(phone)) {
            wrapper.like(User::getPhone, phone);
        }
        if (roleId != null) {
            wrapper.eq(User::getRoleId, roleId);
        }
        // 不查询密码
        wrapper.select(User.class, info -> !info.getColumn().equals("password"));
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> pageInfo = userService.page(new Page<>(current, size), wrapper);
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody User user) {
        if (!StringUtils.hasText(user.getPassword())) {
            // 默认密码 123456 的明文（实际需用 BCrypt 加密）
            user.setPassword("123456"); 
        }
        return Result.success(userService.save(user));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody User user) {
        return Result.success(userService.updateById(user));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }
}