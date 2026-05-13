package com.yanxin.zhijian.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
@Data
@TableName("quality_appeal")
public class QualityAppeal {
    @TableId(type = IdType.AUTO) private Long id;
    private Long taskId; 
    private Long staffId;
    @TableField(exist = false) private String staffName; 
    private String reason; 
    private String status;
    private Long handlerId;
    private String handlerName;
    private String handleResult;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") private LocalDateTime handleTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") private LocalDateTime updateTime;
    @TableLogic private Integer deleted;

    public String getAppealNo() {
        return id != null ? "APL-" + id : null;
    }

    public String getTaskNo() {
        return taskId != null ? "QAT-" + taskId : null;
    }
}