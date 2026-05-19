package com.yanxin.suoguanjia.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 设备人员授权关联
 */
@Data
@TableName("device_user_rel")
public class DeviceUserRel {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long deviceId;
    private Long userId;
    private String userName;
    private Integer authType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime validStartTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime validEndTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}
