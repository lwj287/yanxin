package com.yanxin.keyingmen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.keyingmen.common.Result;
import com.yanxin.keyingmen.entity.MemberCoupon;
import com.yanxin.keyingmen.entity.CouponTemplate;
import com.yanxin.keyingmen.service.IMemberCouponService;
import com.yanxin.keyingmen.service.ICouponTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/member-coupon")
@RequiredArgsConstructor
public class MemberCouponController {
    private final IMemberCouponService service;
    private final ICouponTemplateService couponTemplateService;

    @GetMapping("/page")
    public Result<Page<MemberCoupon>> page(@RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @RequestHeader(value = "Authorization", required = false) String token) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MemberCoupon> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        // MVP阶段解析token获取当前登录的memberId
        if (token != null && token.startsWith("token_")) {
            try {
                Long memberId = Long.parseLong(token.substring(6));
                queryWrapper.eq(MemberCoupon::getMemberId, memberId);
            } catch (Exception ignored) {
            }
        }
        queryWrapper.orderByDesc(MemberCoupon::getCreateTime);
        Page<MemberCoupon> pageResult = service.page(new Page<>(page, pageSize), queryWrapper);
        
        List<MemberCoupon> records = pageResult.getRecords();
        if (!records.isEmpty()) {
            List<Long> couponIds = records.stream().map(MemberCoupon::getCouponId).distinct().collect(Collectors.toList());
            if (!couponIds.isEmpty()) {
                List<CouponTemplate> templates = couponTemplateService.listByIds(couponIds);
                Map<Long, CouponTemplate> templateMap = templates.stream().collect(Collectors.toMap(CouponTemplate::getCouponId, t -> t));
                records.forEach(record -> {
                    CouponTemplate template = templateMap.get(record.getCouponId());
                    if (template != null) {
                        record.setCouponName(template.getCouponName());
                        record.setFaceValue(template.getFaceValue());
                        record.setConditionAmount(template.getConditionAmount());
                    }
                });
            }
        }
        
        return Result.success(pageResult);
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
