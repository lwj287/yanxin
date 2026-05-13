package com.yanxin.suoguanjia.vo;

import lombok.Data;
import java.util.List;

@Data
public class DashboardStatVO {
    private Long totalDevices;    // 总设备数
    private Long onlineDevices;   // 在线设备数
    private Long faultDevices;    // 故障报警数
    private Long totalLogs;       // 开锁/日志记录数
    
    private List<String> trendDates; // 趋势图日期 (如 "05-07", "05-08")
    private List<Integer> trendUnlockCounts; // 趋势图开锁次数
}
