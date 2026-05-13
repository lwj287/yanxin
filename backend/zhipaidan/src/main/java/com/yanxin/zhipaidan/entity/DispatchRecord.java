package com.yanxin.zhipaidan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dispatch_record")
public class DispatchRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    private String orderNo;
    private Long staffId;
    private String staffName;
    private LocalDateTime planStartTime;
    private LocalDateTime planEndTime;
    
    /**
     * 派单类型:0-系统推荐人工确认,1-完全自动派单,2-人工强行指派
     */
    private Integer dispatchType;
    
    private Integer estimatedDistance;
    
    /**
     * 状态:0-待接单(可选),1-已接单/生效中,2-已完工,3-已撤销
     */
    private Integer status;
    
    @TableLogic
    private Integer isDeleted;
    
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
