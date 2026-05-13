package com.yanxin.zhijian.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@TableName("quality_task")
public class QualityTask {
    @TableId(type = IdType.AUTO) private Long id;
    private Long orderId; 
    private String serviceType;
    private Long staffId; 
    private String staffName; 
    private String status;
    private BigDecimal aiScore; 
    private BigDecimal manualScore;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") private LocalDateTime submitTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") private LocalDateTime updateTime;
    @TableLogic private Integer deleted;

    @TableField(exist = false)
    private java.util.List<QualityImageRecord> imageRecords;

    public String getTaskNo() {
        return id != null ? "QAT-" + id : null;
    }

    public String getOrderNo() {
        return orderId != null ? "ORD-" + orderId : null;
    }
}