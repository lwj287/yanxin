package com.yanxin.anxinbao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("insurance_order")
public class InsuranceOrder {
    @TableId(type = IdType.AUTO)
    private Long orderId;
    private Long staffId;
    private Long productId;
    private BigDecimal premium;
    private LocalDateTime insureTime;
    private LocalDateTime expireTime;
    private Integer claimStatus;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}