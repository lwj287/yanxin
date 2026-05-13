package com.yanxin.anxinbao.controller;

import com.yanxin.anxinbao.common.Result;
import com.yanxin.anxinbao.entity.Staff;
import com.yanxin.anxinbao.service.IStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private IStaffService staffService;

    @GetMapping("/list")
    public Result<List<Staff>> list() {
        return Result.success(staffService.list());
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Staff staff) {
        staffService.save(staff);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Staff staff) {
        staffService.updateById(staff);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        staffService.removeById(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Staff staff = staffService.getById(id);
        if (staff != null) {
            staff.setStatus(status);
            staffService.updateById(staff);
        }
        return Result.success();
    }
}
