package com.yanxin.suoguanjia.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanxin.suoguanjia.entity.DeviceInfo;
import com.yanxin.suoguanjia.entity.DeviceLog;
import com.yanxin.suoguanjia.service.DeviceInfoService;
import com.yanxin.suoguanjia.service.DeviceLogService;
import com.yanxin.suoguanjia.vo.DashboardStatVO;
import com.yanxin.suoguanjia.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页数据统计 API
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DeviceInfoService deviceInfoService;

    @Autowired
    private DeviceLogService deviceLogService;

    @GetMapping("/statistics")
    public Result<DashboardStatVO> getStatistics() {
        DashboardStatVO stat = new DashboardStatVO();

        // 1. 总设备数
        stat.setTotalDevices(deviceInfoService.count());

        // 2. 在线设备数 (status = 1)
        LambdaQueryWrapper<DeviceInfo> onlineQuery = new LambdaQueryWrapper<>();
        onlineQuery.eq(DeviceInfo::getStatus, 1);
        stat.setOnlineDevices(deviceInfoService.count(onlineQuery));

        // 3. 故障设备数 (status = 2)
        LambdaQueryWrapper<DeviceInfo> faultQuery = new LambdaQueryWrapper<>();
        faultQuery.eq(DeviceInfo::getStatus, 2);
        stat.setFaultDevices(deviceInfoService.count(faultQuery));

        // 4. 总日志数
        stat.setTotalLogs(deviceLogService.count());

        // 5. 最近7天开锁趋势 (完全真实数据)
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            dates.add(d.format(formatter));
            
            // 构造当天的时间范围
            LocalDateTime startOfDay = d.atStartOfDay();
            LocalDateTime endOfDay = d.atTime(LocalTime.MAX);
            
            // 真实查询当天的开锁记录数
            LambdaQueryWrapper<DeviceLog> dayUnlockQuery = new LambdaQueryWrapper<>();
            dayUnlockQuery.eq(DeviceLog::getAction, "unlock")
                    .ge(DeviceLog::getCreateTime, startOfDay)
                    .le(DeviceLog::getCreateTime, endOfDay);
                    
            long realUnlockCount = deviceLogService.count(dayUnlockQuery);
            counts.add((int) realUnlockCount);
        }
        stat.setTrendDates(dates);
        stat.setTrendUnlockCounts(counts);

        return Result.success(stat);
    }
}
