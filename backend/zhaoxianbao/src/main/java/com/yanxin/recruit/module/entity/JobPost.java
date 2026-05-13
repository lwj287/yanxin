package com.yanxin.recruit.module.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yanxin.recruit.common.base.BaseEntity;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("job_post")
public class JobPost extends BaseEntity {
    private String title;
    private String jobTypeCode;
    private String cityCode;
    private String areaCode;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String educationCode;
    private Integer minAge;
    private Integer maxAge;
    private Integer minExperienceYears;
    private String requiredSkills;
    private String description;
    private Integer publishStatus;
    private Long recruiterId;
}
