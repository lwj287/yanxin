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
                   .ne("status", 4) // 排除已撤销的
                   .orderByDesc("create_time")
                   .last("limit 1");
        return Result.success(dispatchRecordService.getOne(queryWrapper));
    }

    @PutMapping("/{id}/start")
    public Result<?> startService(@PathVariable Long id) {
        try {
            boolean success = dispatchRecordService.startService(id);
            if (success) {
                return Result.success();
            }
            return Result.error("开始服务失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/finish")
    public Result<?> finishService(@PathVariable Long id) {
        try {
            boolean success = dispatchRecordService.finishService(id);
            if (success) {
                return Result.success();
            }
            return Result.error("完成服务失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
