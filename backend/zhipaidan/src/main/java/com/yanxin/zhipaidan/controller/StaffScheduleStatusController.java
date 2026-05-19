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
        // 由于前端传过来的是 staffId，而不是主键 id，这里修改为根据 staffId 查询
        StaffScheduleStatus status = staffScheduleStatusService.lambdaQuery().eq(StaffScheduleStatus::getStaffId, id).one();
        return Result.success(status);
    }

    @GetMapping("/phone/{phone}")
    public Result<StaffScheduleStatus> getByPhone(@PathVariable String phone) {
        return Result.success(staffScheduleStatusService.lambdaQuery().eq(StaffScheduleStatus::getStaffPhone, phone).one());
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody StaffScheduleStatus staffScheduleStatus) {
        staffScheduleStatusService.updateById(staffScheduleStatus);
        return Result.success();
    }
}
