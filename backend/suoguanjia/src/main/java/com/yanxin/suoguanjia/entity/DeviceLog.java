package com.yanxin.suoguanjia.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 设备操作与状态日志
 */
@Data
@TableName(value = "device_log", autoResultMap = true)
public class DeviceLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long deviceId;
    private Integer logType;
    private String action;
    private Long operatorId;
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private java.util.Map<String, Object> content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}
