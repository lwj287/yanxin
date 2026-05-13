package com.yanxin.suoguanjia.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 维修工单
 */
@Data
@TableName("maintenance_ticket")
public class MaintenanceTicket {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ticketSn;
    private Long deviceId;
    private Long reporterId;
    private String faultDesc;
    private Integer status;
    private String resolveDesc;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}
