package com.yanxin.zhijian.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("quality_image_record")
public class QualityImageRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;
    
    private String fileName;

    private String imageUrl;
    
    private Long fileSize;

    private String aiStatus; // PASSED, FAILED, UNCHECKED
    private String aiReason;
    private String aiDetectResult; // JSON string
    private BigDecimal aiScore;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
