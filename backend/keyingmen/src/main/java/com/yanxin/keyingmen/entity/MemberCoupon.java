package com.yanxin.keyingmen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
@TableName("member_coupon")
public class MemberCoupon {
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long id;
    private Long memberId;
    private Long couponId;
    private Long orderId;
    private Integer status;
    private java.time.LocalDateTime receiveTime;
    private java.time.LocalDateTime useTime;
    
    @TableField(exist = false)
    private String couponName;
    @TableField(exist = false)
    private java.math.BigDecimal faceValue;
    @TableField(exist = false)
    private java.math.BigDecimal conditionAmount;
    
    @com.baomidou.mybatisplus.annotation.TableLogic
    private Integer isDeleted;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private java.time.LocalDateTime createTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private java.time.LocalDateTime updateTime;
}
