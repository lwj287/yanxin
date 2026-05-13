package com.yanxin.zhipaidan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanxin.zhipaidan.common.Result;
import com.yanxin.zhipaidan.entity.ServiceOrder;
import com.yanxin.zhipaidan.entity.vo.MatchedStaffVO;
import com.yanxin.zhipaidan.service.IServiceOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/zhipaidan/order")
public class ServiceOrderController {

    @Autowired
    private IServiceOrderService serviceOrderService;

    @GetMapping("/list")
    public Result<List<ServiceOrder>> list(@RequestParam(required = false) Integer status) {
        QueryWrapper<ServiceOrder> queryWrapper = new QueryWrapper<>();
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        return Result.success(serviceOrderService.list(queryWrapper));
    }

    @GetMapping("/{id}")
    public Result<ServiceOrder> getById(@PathVariable Long id) {
        return Result.success(serviceOrderService.getById(id));
    }

    /**
     * 为订单智能匹配合适的家政员
     */
    @GetMapping("/{id}/match-staff")
    public Result<List<MatchedStaffVO>> matchStaff(@PathVariable Long id) {
        try {
            List<MatchedStaffVO> matchedStaffs = serviceOrderService.matchStaffForOrder(id);
            return Result.success(matchedStaffs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 人工确认派单
     */
    @PostMapping("/{id}/dispatch")
    public Result<Boolean> dispatchOrder(@PathVariable Long id, @RequestParam Long staffId) {
        try {
            boolean success = serviceOrderService.dispatchOrder(id, staffId);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
