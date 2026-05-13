package com.yanxin.keyingmen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
@TableName("orders")
public class Orders {
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long orderId;
    private Long memberId;
    
    @TableField(exist = false)
    private String memberName; // 关联的会员姓名，不映射到数据库表中
    
    private Long couponId;
    private String serviceItem;
    private java.math.BigDecimal totalAmount;
    private java.math.BigDecimal pointDiscountAmount;
    private java.math.BigDecimal couponDiscountAmount;
    private java.math.BigDecimal payAmount;
    private Integer payStatus;
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.time.LocalDateTime payTime;
    private Integer payMethod;
    @com.baomidou.mybatisplus.annotation.TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
    @com.baomidou.mybatisplus.annotation.TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.time.LocalDateTime createTime;
    @com.baomidou.mybatisplus.annotation.TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private java.time.LocalDateTime updateTime;
}
