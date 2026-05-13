package com.yanxin.recruit.common.enums;

public enum ResumeStatusEnum {
    SUBMITTED(1, "已投递"),
    SMART_SCREEN_PASS(2, "初筛已通过"),
    SMART_SCREEN_REJECT(3, "初筛未通过"),
    MANUAL_SCREEN_PASS(4, "简历已通过"),
    MANUAL_SCREEN_REJECT(5, "简历未通过"),
    INTERVIEW_PASS(6, "面试已通过"),
    INTERVIEW_REJECT(7, "面试未通过"),
    ONBOARDED(8, "已入职"),
    
    // Legacy support to prevent errors during refactor, mapped appropriately
    QUALIFIED(4, "合格"),
    REJECTED(5, "不合格");

    private final int code;
    private final String desc;

    ResumeStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int code() {
        return code;
    }

    public String desc() {
        return desc;
    }
}
