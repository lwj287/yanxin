package com.yanxin.suoguanjia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanxin.suoguanjia.dto.DeviceReportDTO;
import com.yanxin.suoguanjia.dto.UnlockReqDTO;
import com.yanxin.suoguanjia.entity.DeviceInfo;
import com.yanxin.suoguanjia.entity.DeviceLog;
import com.yanxin.suoguanjia.entity.DeviceUserRel;
import com.yanxin.suoguanjia.entity.MaintenanceTicket;
import com.yanxin.suoguanjia.service.DeviceControlService;
import com.yanxin.suoguanjia.service.DeviceInfoService;
import com.yanxin.suoguanjia.service.DeviceLogService;
import com.yanxin.suoguanjia.service.DeviceUserRelService;
import com.yanxin.suoguanjia.service.MaintenanceTicketService;
import com.yanxin.suoguanjia.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DeviceControlServiceImpl implements DeviceControlService {

    @Autowired
    private DeviceInfoService deviceInfoService;
    @Autowired
    private DeviceUserRelService deviceUserRelService;
    @Autowired
    private DeviceLogService deviceLogService;
    @Autowired
    private MaintenanceTicketService maintenanceTicketService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> remoteUnlock(UnlockReqDTO req) {
        // 1. 检查设备是否存在及状态
        DeviceInfo device = deviceInfoService.getById(req.getDeviceId());
        if (device == null) {
            return Result.error(404, "设备不存在");
        }
        if (device.getStatus() != 1) {
            return Result.error(400, "设备当前不可用(离线或故障)");
        }

        // 2. 权限校验
        LambdaQueryWrapper<DeviceUserRel> authQuery = new LambdaQueryWrapper<>();
        authQuery.eq(DeviceUserRel::getDeviceId, req.getDeviceId())
                 .eq(DeviceUserRel::getUserId, req.getUserId());
        List<DeviceUserRel> authList = deviceUserRelService.list(authQuery);

        boolean hasAuth = false;
        LocalDateTime now = LocalDateTime.now();
        for (DeviceUserRel auth : authList) {
            if (auth.getAuthType() == 1) {
                // 永久授权
                hasAuth = true;
                break;
            } else if (auth.getAuthType() == 2) {
                // 临时授权，检查时间
                if (auth.getValidStartTime() != null && auth.getValidEndTime() != null
                        && now.isAfter(auth.getValidStartTime()) && now.isBefore(auth.getValidEndTime())) {
                    hasAuth = true;
                    break;
                }
            }
        }

        if (!hasAuth) {
            // 记录无权开锁的异常日志
            saveLog(device.getId(), 2, "unlock_failed", req.getUserId(), "{\"reason\":\"No valid authorization\"}");
            return Result.error(403, "无开锁权限或授权已过期");
        }

        // 3. 模拟下发硬件开锁指令 (Mock Device)
        // TODO: 真实场景这里会调用第三方IoT云平台API，并处理异步回调。目前直接默认成功。
        
        // 4. 记录开锁成功日志
        saveLog(device.getId(), 2, "unlock", req.getUserId(), "{\"method\":\"remote_api\", \"success\":true}");

        return Result.success(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> reportStatus(DeviceReportDTO req) {
        // 1. 查找设备
        LambdaQueryWrapper<DeviceInfo> query = new LambdaQueryWrapper<>();
        query.eq(DeviceInfo::getDeviceSn, req.getDeviceSn());
        DeviceInfo device = deviceInfoService.getOne(query);
        if (device == null) {
            return Result.error(404, "未知设备序列号");
        }

        // 2. 更新设备状态
        device.setBatteryLevel(req.getBatteryLevel());
        device.setStatus(req.getStatus());
        deviceInfoService.updateById(device);

        // 3. 记录日志
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("battery", req.getBatteryLevel());
        if (req.getFaultCode() != null) {
            contentMap.put("error_code", req.getFaultCode());
            contentMap.put("msg", req.getFaultMsg());
        }

        int logType = (req.getStatus() == 2) ? 3 : 1;
        String action = (req.getStatus() == 2) ? "fault_alert" : "status_report";
        
        DeviceLog log = new DeviceLog();
        log.setDeviceId(device.getId());
        log.setLogType(logType);
        log.setAction(action);
        log.setContent(contentMap);
        deviceLogService.save(log);

        // 4. 如果是故障，自动生成维修工单
        if (req.getStatus() == 2) {
            // 检查是否已有待处理工单，避免重复报修
            LambdaQueryWrapper<MaintenanceTicket> ticketQuery = new LambdaQueryWrapper<>();
            ticketQuery.eq(MaintenanceTicket::getDeviceId, device.getId())
                       .in(MaintenanceTicket::getStatus, 0, 1);
            long count = maintenanceTicketService.count(ticketQuery);
            
            if (count == 0) {
                MaintenanceTicket ticket = new MaintenanceTicket();
                String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                ticket.setTicketSn("TKT" + dateStr + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
                ticket.setDeviceId(device.getId());
                ticket.setFaultDesc("设备自上报故障: " + (req.getFaultMsg() != null ? req.getFaultMsg() : "未知异常"));
                ticket.setStatus(0); // 待处理
                maintenanceTicketService.save(ticket);
            }
        }

        return Result.success(true);
    }

    private void saveLog(Long deviceId, Integer logType, String action, Long operatorId, String jsonContent) {
        DeviceLog log = new DeviceLog();
        log.setDeviceId(deviceId);
        log.setLogType(logType);
        log.setAction(action);
        log.setOperatorId(operatorId);
        try {
            // 简单转Map
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> map = mapper.readValue(jsonContent, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            log.setContent(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        deviceLogService.save(log);
    }
}
