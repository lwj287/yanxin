package com.yanxin.suoguanjia.controller;

import com.yanxin.suoguanjia.entity.DeviceLog;
import com.yanxin.suoguanjia.service.DeviceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备操作与状态日志 前端控制器
 */
@RestController
@RequestMapping("/devicelog")
public class DeviceLogController {

    @Autowired
    private DeviceLogService service;

    @GetMapping("/list")
    public List<DeviceLog> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public DeviceLog getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping("/save")
    public boolean save(@RequestBody DeviceLog entity) {
        return service.save(entity);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody DeviceLog entity) {
        return service.updateById(entity);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return service.removeById(id);
    }
}
