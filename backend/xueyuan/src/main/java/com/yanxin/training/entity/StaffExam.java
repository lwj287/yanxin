package com.yanxin.training.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("staff_exam")
public class StaffExam {

    @TableId(value = "staff_exam_id", type = IdType.ASSIGN_UUID)
    private String staffExamId;

    @TableField("staff_id")
    private String staffId;

    @TableField("exam_id")
    private String examId;

    @TableField("exam_score")
    private Integer examScore;

    @TableField("exam_status")
    private String examStatus;

    @TableField("exam_time")
    private LocalDateTime examTime;

    @TableField("makeup_count")
    private Integer makeupCount;
}