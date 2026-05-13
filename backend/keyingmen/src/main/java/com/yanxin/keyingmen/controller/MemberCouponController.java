package com.yanxin.keyingmen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.keyingmen.common.Result;
import com.yanxin.keyingmen.entity.MemberCoupon;
import com.yanxin.keyingmen.service.IMemberCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member-coupon")
@RequiredArgsConstructor
public class MemberCouponController {
    private final IMemberCouponService service;

    @GetMapping("/page")
    public Result<Page<MemberCoupon>> page(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(service.page(new Page<>(page, pageSize)));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody MemberCoupon entity) {
        return Result.success(service.save(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody MemberCoupon entity) {
        return Result.success(service.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(service.removeById(id));
    }
}
