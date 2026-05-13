package com.yanxin.training.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam")
public class Exam {

    @TableId(value = "exam_id", type = IdType.ASSIGN_UUID)
    private String examId;

    @TableField("exam_name")
    private String examName;

    @TableField("course_id")
    private String courseId;

    @TableField("exam_time")
    private LocalDateTime examTime;

    @TableField("exam_duration")
    private Integer examDuration;

    @TableField("pass_score")
    private Integer passScore;

    @TableField("create_time")
    private LocalDateTime createTime;
}