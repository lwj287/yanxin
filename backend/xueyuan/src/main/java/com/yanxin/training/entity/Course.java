package com.yanxin.training.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course {

    @TableId(value = "course_id", type = IdType.ASSIGN_UUID)
    private String courseId;

    @TableField("course_name")
    private String courseName;

    @TableField("course_type")
    private String courseType;

    private String difficulty;

    @TableField("course_duration")
    private Integer courseDuration;

    @TableField("course_content")
    private String courseContent;

    @TableField("course_cover")
    private String courseCover;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField(exist = false)
    private Integer enrolled;
}
