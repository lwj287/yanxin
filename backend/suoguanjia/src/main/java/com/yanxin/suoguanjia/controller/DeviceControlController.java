package com.yanxin.suoguanjia.controller;

import com.yanxin.suoguanjia.dto.DeviceReportDTO;
import com.yanxin.suoguanjia.dto.UnlockReqDTO;
import com.yanxin.suoguanjia.service.DeviceControlService;
import com.yanxin.suoguanjia.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设备核心控制与状态上报 API
 */
@RestController
@RequestMapping("/control")
public class DeviceControlController {

    @Autowired
    private DeviceControlService deviceControlService;

    /**
     * 远程一键开锁 (前端/小程序调用)
     */
    @PostMapping("/unlock")
    public Result<Boolean> remoteUnlock(@RequestBody UnlockReqDTO req) {
        if (req.getDeviceId() == null || req.getOperatorId() == null) {
            return Result.error(400, "参数不完整");
        }
        return deviceControlService.remoteUnlock(req);
    }

    /**
     * 接收设备状态上报 (Mock硬件设备调用)
     */
    @PostMapping("/report")
    public Result<Boolean> reportStatus(@RequestBody DeviceReportDTO req) {
        if (req.getDeviceSn() == null || req.getStatus() == null) {
            return Result.error(400, "参数不完整");
        }
        return deviceControlService.reportStatus(req);
    }

    /**
     * 锁定设备 (例如发现异常时，管理员紧急锁定)
     */
    @PostMapping("/lock")
    public Result<Boolean> lockDevice(@RequestBody UnlockReqDTO req) {
        return deviceControlService.lockDevice(req);
    }

    /**
     * 设备重启 (软重启)
     */
    @PostMapping("/reboot")
    public Result<Boolean> rebootDevice(@RequestBody UnlockReqDTO req) {
        return deviceControlService.rebootDevice(req);
    }

    /**
     * 人工申请维修
     */
    @PostMapping("/repair")
    public Result<Boolean> applyRepair(@RequestBody UnlockReqDTO req) {
        return deviceControlService.applyRepair(req);
    }
}
