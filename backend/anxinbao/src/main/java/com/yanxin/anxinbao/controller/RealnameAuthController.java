package com.yanxin.anxinbao.controller;

import com.yanxin.anxinbao.common.Result;
import com.yanxin.anxinbao.entity.RealnameAuth;
import com.yanxin.anxinbao.service.IRealnameAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/realname-auth")
public class RealnameAuthController {

    @Autowired
    private IRealnameAuthService realnameAuthService;

    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(realnameAuthService.list());
    }

    @PutMapping("/{id}/approve")
    public Result<?> approve(@PathVariable Long id) {
        RealnameAuth auth = realnameAuthService.getById(id);
        if (auth != null) {
            auth.setAuthStatus(1);
            auth.setAuthTime(java.time.LocalDateTime.now());
            realnameAuthService.updateById(auth);
        }
        return Result.success();
    }

    @PutMapping("/{id}/reject")
    public Result<?> reject(@PathVariable Long id, @RequestBody(required = false) String reason) {
        RealnameAuth auth = realnameAuthService.getById(id);
        if (auth != null) {
            auth.setAuthStatus(2);
            realnameAuthService.updateById(auth);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        realnameAuthService.removeById(id);
        return Result.success();
    }
}
