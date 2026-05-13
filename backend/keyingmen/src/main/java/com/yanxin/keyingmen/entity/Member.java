package com.yanxin.keyingmen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
@TableName("member")
public class Member {
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long memberId;
    private String memberName;
    private String phone;
    private Integer memberLevel;
    private Integer accountBalance;
    private Integer memberStatus;
    private java.time.LocalDateTime registerTime;
    private Long inviterId;
    private Integer inviteCount;
    @com.baomidou.mybatisplus.annotation.TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private java.time.LocalDateTime createTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private java.time.LocalDateTime updateTime;
}
