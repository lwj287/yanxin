package com.yanxin.keyingmen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.keyingmen.common.Result;
import com.yanxin.keyingmen.entity.CouponTemplate;
import com.yanxin.keyingmen.service.ICouponTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupon-template")
@RequiredArgsConstructor
public class CouponTemplateController {
    private final ICouponTemplateService service;

    @GetMapping("/page")
    public Result<Page<CouponTemplate>> page(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                           @RequestParam(value = "couponName", required = false) String couponName) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<CouponTemplate> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.like(couponName != null && !couponName.isEmpty(), CouponTemplate::getCouponName, couponName)
                    .orderByDesc(CouponTemplate::getCreateTime);
        return Result.success(service.page(new Page<>(page, pageSize), queryWrapper));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody CouponTemplate entity) {
        return Result.success(service.save(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody CouponTemplate entity) {
        return Result.success(service.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id) {
        return Result.success(service.removeById(id));
    }
}
