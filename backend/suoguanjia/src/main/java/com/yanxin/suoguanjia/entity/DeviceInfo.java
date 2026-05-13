package com.yanxin.suoguanjia.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 设备基础信息
 */
@Data
@TableName("device_info")
public class DeviceInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String deviceSn;
    private String deviceName;
    private Integer deviceType;
    private Integer status;
    private Integer batteryLevel;
    private String location;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}
