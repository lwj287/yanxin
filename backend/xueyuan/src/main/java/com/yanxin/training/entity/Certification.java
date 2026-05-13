package com.yanxin.training.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("skill_certification")
public class Certification {

    @TableId(value = "cert_id", type = IdType.ASSIGN_UUID)
    private String certId;

    @TableField("staff_id")
    private String staffId;

    @TableField("exam_id")
    private String examId;

    @TableField("cert_name")
    private String certName;

    @TableField("cert_status")
    private String certStatus;

    @TableField("apply_time")
    private LocalDateTime applyTime;

    @TableField("audit_time")
    private LocalDateTime auditTime;

    @TableField("cert_validity")
    private LocalDateTime certValidity;

    @TableField("cert_url")
    private String certUrl;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String examName;

    @TableField(exist = false)
    private Integer examScore;

    @TableField(exist = false)
    private String level;
}