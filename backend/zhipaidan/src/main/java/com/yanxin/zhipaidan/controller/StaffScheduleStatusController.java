package com.yanxin.zhipaidan.controller;

import com.yanxin.zhipaidan.common.Result;
import com.yanxin.zhipaidan.entity.StaffScheduleStatus;
import com.yanxin.zhipaidan.service.IStaffScheduleStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/zhipaidan/staff")
public class StaffScheduleStatusController {

    @Autowired
    private IStaffScheduleStatusService staffScheduleStatusService;

    @GetMapping("/list")
    public Result<List<StaffScheduleStatus>> list() {
        return Result.success(staffScheduleStatusService.list());
    }

    @GetMapping("/{id}")
    public Result<StaffScheduleStatus> getById(@PathVariable Long id) {
        return Result.success(staffScheduleStatusService.getById(id));
    }
}
