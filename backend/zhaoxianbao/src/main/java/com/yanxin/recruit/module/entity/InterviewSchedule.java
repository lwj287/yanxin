package com.yanxin.recruit.module.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yanxin.recruit.common.base.BaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("interview_schedule")
public class InterviewSchedule extends BaseEntity {
    private Long resumeId;
    private Long interviewerId;
    private LocalDateTime interviewTime;
    private Integer interviewType;
    private String interviewLocation;
    private String interviewLink;
    private Integer noticeSent;
    private String interviewStatus;
}
