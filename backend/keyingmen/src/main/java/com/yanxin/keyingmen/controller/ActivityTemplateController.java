package com.yanxin.keyingmen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.keyingmen.common.Result;
import com.yanxin.keyingmen.entity.ActivityTemplate;
import com.yanxin.keyingmen.entity.CouponTemplate;
import com.yanxin.keyingmen.entity.MemberActivity;
import com.yanxin.keyingmen.entity.MemberCoupon;
import com.yanxin.keyingmen.service.IActivityTemplateService;
import com.yanxin.keyingmen.service.ICouponTemplateService;
import com.yanxin.keyingmen.service.IMemberActivityService;
import com.yanxin.keyingmen.service.IMemberCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/activity-template")
@RequiredArgsConstructor
public class ActivityTemplateController {
    private final IActivityTemplateService service;
    private final IMemberActivityService memberActivityService;
    private final IMemberCouponService memberCouponService;
    private final ICouponTemplateService couponTemplateService;

    @GetMapping("/page")
    public Result<Page<ActivityTemplate>> page(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                           @RequestParam(value = "activityName", required = false) String activityName,
                                           @RequestParam(value = "activityStatus", required = false) Integer activityStatus) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ActivityTemplate> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        queryWrapper.like(activityName != null && !activityName.isEmpty(), ActivityTemplate::getActivityName, activityName);
        
        // 动态根据时间过滤状态
        java.util.Date now = new java.util.Date();
        if (activityStatus != null) {
            if (activityStatus == 0) {
                // 未开始: now < start_time
                queryWrapper.gt(ActivityTemplate::getStartTime, now);
            } else if (activityStatus == 1) {
                // 进行中: now >= start_time AND now <= end_time
                queryWrapper.le(ActivityTemplate::getStartTime, now).ge(ActivityTemplate::getEndTime, now);
            } else if (activityStatus == 2) {
                // 已结束: now > end_time
                queryWrapper.lt(ActivityTemplate::getEndTime, now);
            }
        }
        
        queryWrapper.orderByDesc(ActivityTemplate::getCreateTime);
        return Result.success(service.page(new Page<>(page, pageSize), queryWrapper));
    }

    @GetMapping("/{id}")
    public Result<ActivityTemplate> getById(@PathVariable("id") Long id) {
        ActivityTemplate activity = service.getById(id);
        if (activity != null && activity.getCouponId() != null) {
            CouponTemplate coupon = couponTemplateService.getById(activity.getCouponId());
            if (coupon != null) {
                activity.setCouponName(coupon.getCouponName());
                activity.setFaceValue(coupon.getFaceValue());
                activity.setConditionAmount(coupon.getConditionAmount());
            }
        }
        return Result.success(activity);
    }

    @PostMapping("/{id}/participate")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> participate(@PathVariable("id") Long id,
                                      @RequestHeader(value = "Authorization", required = false) String token) {
        // 1. 验证用户登录状态
        if (token == null || !token.startsWith("token_")) {
            return Result.error(401, "请先登录");
        }
        Long memberId;
        try {
            memberId = Long.parseLong(token.substring(6));
        } catch (Exception e) {
            return Result.error(401, "无效的登录状态");
        }

        // 2. 获取活动信息并验证状态
        ActivityTemplate activity = service.getById(id);
        if (activity == null) {
            return Result.error(404, "活动不存在");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartTime())) {
            return Result.error(400, "活动尚未开始");
        }
        if (now.isAfter(activity.getEndTime())) {
            return Result.error(400, "活动已结束");
        }

        // 3. 检查是否已经参与过
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MemberActivity> checkQw = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        checkQw.eq(MemberActivity::getMemberId, memberId)
               .eq(MemberActivity::getActivityId, id);
        if (memberActivityService.count(checkQw) > 0) {
            return Result.error(400, "您已经参与过该活动了");
        }

        // 4. 执行参与逻辑 (activityType: 1-赠送, 2-抽奖)
        boolean isWin = true; // 默认赠送必中
        if (activity.getActivityType() != null && activity.getActivityType() == 2) {
            // 抽奖逻辑：简单模拟 50% 中奖率
            isWin = Math.random() < 0.5;
        }

        // 5. 记录参与情况
        MemberActivity memberActivity = new MemberActivity();
        memberActivity.setMemberId(memberId);
        memberActivity.setActivityId(id);
        memberActivity.setParticipateStatus(isWin ? 1 : 0);
        memberActivity.setParticipateTime(now);
        
        if (isWin) {
            memberActivity.setObtainedCouponId(activity.getCouponId());
            
            // 6. 发放优惠券
            MemberCoupon memberCoupon = new MemberCoupon();
            memberCoupon.setMemberId(memberId);
            memberCoupon.setCouponId(activity.getCouponId());
            memberCoupon.setStatus(0); // 0-未使用
            memberCoupon.setReceiveTime(now);
            memberCouponService.save(memberCoupon);
            
            // 更新活动的已兑换数量
            activity.setRedeemCount((activity.getRedeemCount() == null ? 0 : activity.getRedeemCount()) + 1);
            service.updateById(activity);
        }
        
        memberActivityService.save(memberActivity);

        return Result.success(isWin ? "恭喜你，已发放优惠券！" : "很遗憾，未中奖，再接再厉！");
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody ActivityTemplate entity) {
        return Result.success(service.save(entity));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody ActivityTemplate entity) {
        return Result.success(service.updateById(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable("id") Long id) {
        return Result.success(service.removeById(id));
    }
}
