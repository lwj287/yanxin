package com.yanxin.recruit.module.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InterviewFinishReq {
    @NotNull
    private Integer pass;
    private Integer professionalScore;
    private Integer communicationScore;
    private Integer attitudeScore;
    private String comments;
}
