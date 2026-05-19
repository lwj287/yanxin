package com.yanxin.suoguanjia.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanxin.suoguanjia.entity.DeviceUserRel;
import com.yanxin.suoguanjia.service.DeviceUserRelService;
import com.yanxin.suoguanjia.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 设备-人员授权关联表 前端控制器
 * </p>
 *
 * @author Trae
 * @since 2026-05-14
 */
@RestController
@RequestMapping("/deviceuserrel")
public class DeviceUserRelController {

    @Autowired
    private DeviceUserRelService deviceUserRelService;

    @GetMapping("/list")
    public Result<List<DeviceUserRel>> list() {
        return Result.success(deviceUserRelService.list());
    }

    /**
     * 新增授权
     */
    @PostMapping("/add")
    public Result<Boolean> addAuth(@RequestBody DeviceUserRel rel) {
        if (rel.getAuthType() == 2) { // 如果是临时授权，检查时间
            if (rel.getValidStartTime() == null || rel.getValidEndTime() == null) {
                return Result.error(400, "临时授权必须选择生效和失效时间");
            }
            if (rel.getValidEndTime().isBefore(LocalDateTime.now())) {
                return Result.error(400, "失效时间不能早于当前时间");
            }
        } else {
            // 如果是永久授权，手动设置极大的时间范围，防止数据库空指针或逻辑错误
            rel.setValidStartTime(LocalDateTime.now());
            rel.setValidEndTime(LocalDateTime.of(2099, 12, 31, 23, 59, 59));
        }
        
        // 检查是否已经存在授权
        LambdaQueryWrapper<DeviceUserRel> query = new LambdaQueryWrapper<>();
        query.eq(DeviceUserRel::getDeviceId, rel.getDeviceId())
             .eq(DeviceUserRel::getUserId, rel.getUserId());
        if (deviceUserRelService.count(query) > 0) {
            return Result.error(400, "该用户已拥有该设备的授权，请勿重复添加");
        }
        
        deviceUserRelService.save(rel);
        return Result.success(true);
    }

    /**
     * 撤销授权 (删除)
     */
    @DeleteMapping("/revoke/{id}")
    public Result<Boolean> revokeAuth(@PathVariable Long id) {
        deviceUserRelService.removeById(id);
        return Result.success(true);
    }
}
