package com.yanxin.zhipaidan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanxin.zhipaidan.common.Result;
import com.yanxin.zhipaidan.entity.DispatchRecord;
import com.yanxin.zhipaidan.entity.ServiceOrder;
import com.yanxin.zhipaidan.entity.StaffScheduleStatus;
import com.yanxin.zhipaidan.service.IDispatchRecordService;
import com.yanxin.zhipaidan.service.IServiceOrderService;
import com.yanxin.zhipaidan.service.IStaffScheduleStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/zhipaidan/dashboard")
public class DashboardController {

    @Autowired
    private IServiceOrderService serviceOrderService;

    @Autowired
    private IStaffScheduleStatusService staffScheduleStatusService;

    @Autowired
    private IDispatchRecordService dispatchRecordService;

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> data = new HashMap<>();
        
        // 1. 待派订单数 (status = 0)
        long dispatchOrders = serviceOrderService.count(new QueryWrapper<ServiceOrder>().eq("status", 0));
        data.put("dispatchOrders", dispatchOrders);
        
        // 2. 今日已派发订单数 (status = 1 且 create_time 为今天)
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        long todayDispatched = dispatchRecordService.count(new QueryWrapper<DispatchRecord>()
                .ge("create_time", startOfDay)
                .le("create_time", endOfDay));
        data.put("todayDispatched", todayDispatched);
        
        // 3. 当前空闲家政员数 (current_status = 0)
        long freeStaff = staffScheduleStatusService.count(new QueryWrapper<StaffScheduleStatus>().eq("current_status", 0));
        data.put("freeStaff", freeStaff);
        
        // 4. 当前服务中家政员数 (current_status = 1)
        long workingStaff = staffScheduleStatusService.count(new QueryWrapper<StaffScheduleStatus>().eq("current_status", 1));
        data.put("workingStaff", workingStaff);

        // 5. 近期派单趋势 (过去7天)
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.format(formatter));
            
            LocalDateTime start = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);
            long count = dispatchRecordService.count(new QueryWrapper<DispatchRecord>()
                    .ge("create_time", start)
                    .le("create_time", end));
            counts.add(count);
        }
        
        Map<String, Object> trendData = new HashMap<>();
        trendData.put("dates", dates);
        trendData.put("counts", counts);
        data.put("trendData", trendData);

        return Result.success(data);
    }
}