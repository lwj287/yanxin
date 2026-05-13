package com.yanxin.suoguanjia.dto;

import lombok.Data;

@Data
public class DeviceReportDTO {
    private String deviceSn;
    private Integer batteryLevel;
    private Integer status; // 0-离线, 1-在线, 2-故障
    private String faultCode;
    private String faultMsg;
}
