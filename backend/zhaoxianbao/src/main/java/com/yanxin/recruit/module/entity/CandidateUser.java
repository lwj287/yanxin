package com.yanxin.recruit.module.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yanxin.recruit.common.base.BaseEntity;
import lombok.Data;

@Data
@TableName("candidate_user")
public class CandidateUser extends BaseEntity {
    private String username;
    private String password;
    private String realName;
    private Integer gender;
    private String phone;
    private String avatarUrl;
    /**
     * 简历投递情况：0未投递，1已投递
     */
    private Integer resumeDeliveryStatus;
}
