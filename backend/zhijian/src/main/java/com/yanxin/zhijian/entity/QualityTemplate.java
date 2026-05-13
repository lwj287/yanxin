package com.yanxin.zhijian.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
@Data
@TableName("quality_template")
public class QualityTemplate {
    @TableId(type = IdType.AUTO) private Long id;
    private String serviceType; 
    @TableField("photo_requirements") private String requirements; 
    private Integer status;
    private String templateName;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") private LocalDateTime createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") private LocalDateTime updateTime;
    @TableLogic private Integer deleted;
}