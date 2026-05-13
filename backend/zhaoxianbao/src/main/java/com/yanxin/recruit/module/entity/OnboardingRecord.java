package com.yanxin.recruit.module.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yanxin.recruit.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("onboarding_record")
public class OnboardingRecord extends BaseEntity {
    private Long resumeId;
    private Long hrId;
    private String onboardStatus;
    private Integer docsVerified;
    private LocalDate onboardingDate;
    private String contractNo;
    private String remark;
}
