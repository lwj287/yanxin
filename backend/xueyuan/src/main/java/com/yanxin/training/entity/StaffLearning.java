package com.yanxin.training.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("staff_learning")
public class StaffLearning {

    @TableId(value = "learn_id", type = IdType.ASSIGN_UUID)
    private String learnId;

    @TableField("staff_id")
    private String staffId;

    @TableField("course_id")
    private String courseId;

    @TableField("learn_progress")
    private Integer learnProgress;

    @TableField("learn_status")
    private String learnStatus;

    @TableField("last_learn_time")
    private LocalDateTime lastLearnTime;

    @TableField("complete_time")
    private LocalDateTime completeTime;
}