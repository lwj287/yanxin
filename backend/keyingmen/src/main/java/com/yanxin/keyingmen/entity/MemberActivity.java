package com.yanxin.keyingmen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
@TableName("member_activity")
public class MemberActivity {
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long id;
    private Long memberId;
    private Long activityId;
    private Integer participateStatus;
    private java.time.LocalDateTime participateTime;
    private Long obtainedCouponId;
    @com.baomidou.mybatisplus.annotation.TableLogic
    private Integer isDeleted;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private java.time.LocalDateTime createTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private java.time.LocalDateTime updateTime;
}
