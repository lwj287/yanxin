package com.yanxin.suoguanjia.controller;

import com.yanxin.suoguanjia.entity.DeviceUserRel;
import com.yanxin.suoguanjia.service.DeviceUserRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备人员授权关联 前端控制器
 */
@RestController
@RequestMapping("/deviceuserrel")
public class DeviceUserRelController {

    @Autowired
    private DeviceUserRelService service;

    @GetMapping("/list")
    public List<DeviceUserRel> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public DeviceUserRel getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping("/save")
    public boolean save(@RequestBody DeviceUserRel entity) {
        return service.save(entity);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody DeviceUserRel entity) {
        return service.updateById(entity);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return service.removeById(id);
    }
}
