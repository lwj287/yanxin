package com.yanxin.zhipaidan.entity.vo;

import com.yanxin.zhipaidan.entity.StaffScheduleStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MatchedStaffVO extends StaffScheduleStatus {
    
    /**
     * 预估距离（米）
     */
    private Double distance;
    
    /**
     * 匹配度分数（可用于排序，距离越近、技能越匹配分数越高）
     */
    private Integer matchScore;
}
