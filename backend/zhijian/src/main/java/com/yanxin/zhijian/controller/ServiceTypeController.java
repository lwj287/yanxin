package com.yanxin.zhijian.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanxin.zhijian.common.Result;
import com.yanxin.zhijian.entity.ServiceType;
import com.yanxin.zhijian.service.IServiceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/zhijian/service-type")
public class ServiceTypeController {

    @Autowired
    private IServiceTypeService serviceTypeService;

    @GetMapping("/list")
    public Result<?> list(ServiceType query) {
        try {
            QueryWrapper<ServiceType> wrapper = new QueryWrapper<>();
            if (query.getName() != null && !query.getName().isEmpty()) {
                wrapper.like("name", query.getName());
            }
            if (query.getStatus() != null) {
                wrapper.eq("status", query.getStatus());
            }
            wrapper.orderByDesc("create_time");
            return Result.success(serviceTypeService.list(wrapper));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody ServiceType serviceType) {
        if (serviceType.getCreateTime() == null) {
            serviceType.setCreateTime(LocalDateTime.now());
        }
        serviceType.setUpdateTime(LocalDateTime.now());
        return Result.success(serviceTypeService.save(serviceType));
    }
    
    @PostMapping("/update")
    public Result<?> update(@RequestBody ServiceType serviceType) {
        if (serviceType.getId() == null) {
            return Result.error("ID不能为空");
        }
        serviceType.setUpdateTime(LocalDateTime.now());
        return Result.success(serviceTypeService.updateById(serviceType));
    }

    @PostMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        ServiceType serviceType = serviceTypeService.getById(id);
        if (serviceType == null) {
            return Result.error("服务类型不存在");
        }
        serviceType.setStatus(status);
        serviceType.setUpdateTime(LocalDateTime.now());
        return Result.success(serviceTypeService.updateById(serviceType));
    }
    
    @PostMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return Result.success(serviceTypeService.removeById(id));
    }
}
