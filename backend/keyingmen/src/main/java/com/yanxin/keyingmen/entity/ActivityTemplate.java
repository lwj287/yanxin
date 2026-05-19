package com.yanxin.keyingmen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
@TableName("activity_template")
public class ActivityTemplate {
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long activityId;
    private Long couponId;
    
    @TableField(exist = false)
    private String couponName;
    @TableField(exist = false)
    private java.math.BigDecimal faceValue;
    @TableField(exist = false)
    private java.math.BigDecimal conditionAmount;
    
    private String activityName;
    private Integer activityType;
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.time.LocalDateTime startTime;
   @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.time.LocalDateTime endTime;
    private Integer issueCount;
    private Integer redeemCount;
    @com.baomidou.mybatisplus.annotation.TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
    @com.baomidou.mybatisplus.annotation.TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.time.LocalDateTime createTime;
    @com.baomidou.mybatisplus.annotation.TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.time.LocalDateTime updateTime;
}
