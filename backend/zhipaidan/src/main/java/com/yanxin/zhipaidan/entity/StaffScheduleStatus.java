package com.yanxin.zhipaidan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "staff_schedule_status", autoResultMap = true)
public class StaffScheduleStatus {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long staffId;
    private String staffName;
    private String staffPhone;
    
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> skills;
    
    /**
     * 当前状态:0-空闲,1-服务中,2-休息/请假
     */
    private Integer currentStatus;
    
    private BigDecimal lastLongitude;
    private BigDecimal lastLatitude;
    private LocalDateTime locationUpdateTime;
    
    @TableLogic
    private Integer isDeleted;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
