package com.yanxin.keyingmen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.keyingmen.common.Result;
import com.yanxin.keyingmen.entity.MemberActivity;
import com.yanxin.keyingmen.service.IMemberActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member-activity")
@RequiredArgsConstructor
public class MemberActivityController {
    private final IMemberActivityService service;

    @GetMapping("/page")
    public Result<Page<MemberActivity>> page(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(service.page(new Page<>(page, pageSize)));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody MemberActivity entity) {
        return Result.success(service.save(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody MemberActivity entity) {
        return Result.success(service.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(service.removeById(id));
    }
}
