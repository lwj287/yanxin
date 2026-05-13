package com.yanxin.training.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("exam_question")
public class ExamQuestion {

    @TableId(value = "question_id", type = IdType.ASSIGN_UUID)
    private String questionId;

    @TableField("exam_id")
    private String examId;

    @TableField("question_type")
    private String questionType;

    @TableField("question_content")
    private String questionContent;

    @TableField("option_a")
    private String optionA;

    @TableField("option_b")
    private String optionB;

    @TableField("option_c")
    private String optionC;

    @TableField("option_d")
    private String optionD;

    @TableField("correct_answer")
    private String correctAnswer;

    private Integer score;
}