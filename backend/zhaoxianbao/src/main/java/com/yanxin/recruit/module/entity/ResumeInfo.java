package com.yanxin.recruit.module.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yanxin.recruit.common.base.BaseEntity;
import lombok.Data;

@Data
@TableName("resume_info")
public class ResumeInfo extends BaseEntity {
    private Long candidateId;
    private Long jobId;
    private String realName;
    private String phone;
    private Integer age;
    private String educationCode;
    private Integer experienceYears;
    private String skills;
    private String expectedCityCode;
    private String selfIntroduction;
    private String photoUrl;
    private String idCardFrontUrl;
    private String idCardBackUrl;
    private Integer screeningScore;
    private Integer screeningResult;
    private String screeningReason;
    private Integer resumeStatus;
}
