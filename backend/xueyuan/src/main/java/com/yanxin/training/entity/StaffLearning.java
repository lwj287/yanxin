package com.yanxin.training.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("last_learn_time")
    private LocalDateTime lastLearnTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("complete_time")
    private LocalDateTime completeTime;

    // DTO fields
    @TableField(exist = false)
    private String courseName;

    @TableField(exist = false)
    private String coverUrl;
}
