package com.yanxin.suoguanjia.controller;

import com.yanxin.suoguanjia.entity.MaintenanceTicket;
import com.yanxin.suoguanjia.service.MaintenanceTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 维修工单 前端控制器
 */
@RestController
@RequestMapping("/maintenanceticket")
public class MaintenanceTicketController {

    @Autowired
    private MaintenanceTicketService service;

    @GetMapping("/list")
    public List<MaintenanceTicket> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public MaintenanceTicket getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping("/save")
    public boolean save(@RequestBody MaintenanceTicket entity) {
        return service.save(entity);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody MaintenanceTicket entity) {
        return service.updateById(entity);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return service.removeById(id);
    }
}
