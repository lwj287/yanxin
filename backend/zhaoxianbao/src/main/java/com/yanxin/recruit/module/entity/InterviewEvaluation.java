package com.yanxin.recruit.module.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yanxin.recruit.common.base.BaseEntity;
import lombok.Data;

@Data
@TableName("interview_evaluation")
public class InterviewEvaluation extends BaseEntity {
    private Long interviewId;
    private Long evaluatorId;
    private Integer professionalScore;
    private Integer communicationScore;
    private Integer attitudeScore;
    private Integer totalScore;
    private Integer evaluateResult;
    private String comments;
}
