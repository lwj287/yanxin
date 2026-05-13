package com.yanxin.recruit.module.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yanxin.recruit.common.base.BaseEntity;
import lombok.Data;

@Data
@TableName("candidate_resume_profile")
public class CandidateResumeProfile extends BaseEntity {
    private Long candidateId;
    private String realName;
    private String phone;
    private Integer age;
    private String educationText;
    private Integer experienceYears;
    private String skillsText;
    private String expectedCityText;
    private String selfIntroduction;
    private String photoUrl;
    private String idCardFrontUrl;
    private String idCardBackUrl;
}
