package com.yanxin.keyingmen.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.keyingmen.common.Result;
import com.yanxin.keyingmen.entity.ActivityTemplate;
import com.yanxin.keyingmen.service.IActivityTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/activity-template")
@RequiredArgsConstructor
public class ActivityTemplateController {
    private final IActivityTemplateService service;

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
