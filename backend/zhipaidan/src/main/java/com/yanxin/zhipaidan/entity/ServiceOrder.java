package com.yanxin.zhipaidan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("service_order")
public class ServiceOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    private String customerName;
    private String customerPhone;
    private String serviceAddress;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String serviceType;
    private LocalDateTime expectStartTime;
    private LocalDateTime expectEndTime;
    
    /**
     * 状态:0-待派单,1-已派单,2-服务中,3-已完成,4-已取消
     */
    private Integer status;
    
    @TableLogic
    private Integer isDeleted;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
