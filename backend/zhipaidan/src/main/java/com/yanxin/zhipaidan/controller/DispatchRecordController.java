package com.yanxin.zhipaidan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanxin.zhipaidan.common.Result;
import com.yanxin.zhipaidan.entity.DispatchRecord;
import com.yanxin.zhipaidan.service.IDispatchRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/zhipaidan/dispatch")
public class DispatchRecordController {

    @Autowired
    private IDispatchRecordService dispatchRecordService;

    @GetMapping("/list")
    public Result<List<DispatchRecord>> list(@RequestParam(required = false) Long staffId) {
        QueryWrapper<DispatchRecord> queryWrapper = new QueryWrapper<>();
        if (staffId != null) {
            queryWrapper.eq("staff_id", staffId);
        }
        queryWrapper.orderByAsc("plan_start_time");
        return Result.success(dispatchRecordService.list(queryWrapper));
    }
    
    @GetMapping("/order/{orderId}")
    public Result<DispatchRecord> getByOrderId(@PathVariable Long orderId) {
        QueryWrapper<DispatchRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId)
                   .ne("status", 3) // 排除已撤销的
                   .orderByDesc("create_time")
                   .last("limit 1");
        return Result.success(dispatchRecordService.getOne(queryWrapper));
    }
}
