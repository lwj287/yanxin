package com.yanxin.keyingmen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.keyingmen.common.Result;
import com.yanxin.keyingmen.entity.PointRecord;
import com.yanxin.keyingmen.service.IPointRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/point-record")
@RequiredArgsConstructor
public class PointRecordController {
    private final IPointRecordService service;

    @GetMapping("/page")
    public Result<Page<PointRecord>> page(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestHeader(value = "Authorization", required = false) String token) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PointRecord> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        // MVP阶段解析token获取当前登录的memberId
        if (token != null && token.startsWith("token_")) {
            try {
                Long memberId = Long.parseLong(token.substring(6));
                queryWrapper.eq(PointRecord::getMemberId, memberId);
            } catch (Exception ignored) {
            }
        }
        queryWrapper.orderByDesc(PointRecord::getCreateTime);
        return Result.success(service.page(new Page<>(page, pageSize), queryWrapper));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody PointRecord entity) {
        return Result.success(service.save(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody PointRecord entity) {
        return Result.success(service.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(service.removeById(id));
    }
}
