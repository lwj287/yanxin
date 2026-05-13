package com.yanxin.recruit.module.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yanxin.recruit.common.base.BaseEntity;
import lombok.Data;

@Data
@TableName("sys_notice")
public class SysNotice extends BaseEntity {
    private String receiverType;
    private Long receiverId;
    private String title;
    private String content;
    private Integer readFlag;
}
