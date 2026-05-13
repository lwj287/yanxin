package com.yanxin.suoguanjia.controller;

import com.yanxin.suoguanjia.entity.DeviceInfo;
import com.yanxin.suoguanjia.service.DeviceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备基础信息 前端控制器
 */
@RestController
@RequestMapping("/deviceinfo")
public class DeviceInfoController {

    @Autowired
    private DeviceInfoService service;

    @GetMapping("/list")
    public List<DeviceInfo> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public DeviceInfo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping("/save")
    public boolean save(@RequestBody DeviceInfo entity) {
        return service.save(entity);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody DeviceInfo entity) {
        return service.updateById(entity);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return service.removeById(id);
    }
}
