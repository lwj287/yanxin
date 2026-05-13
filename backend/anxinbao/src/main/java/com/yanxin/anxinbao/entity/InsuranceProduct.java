package com.yanxin.anxinbao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("insurance_product")
public class InsuranceProduct {
    @TableId(type = IdType.AUTO)
    private Long productId;
    private String productName;
    private Integer insuranceType;
    private BigDecimal coverageAmount;
    private BigDecimal premium;
    private Integer durationDays;
    private String conditions;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}