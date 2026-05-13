package com.yanxin.anxinbao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("claim")
public class Claim {
    @TableId(type = IdType.AUTO)
    private Long claimId;
    private Long staffId;
    private Long orderId;
    private BigDecimal claimAmount;
    private String materialUrl;
    private Integer claimStatus;
    private LocalDateTime applyTime;
    private LocalDateTime claimTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}