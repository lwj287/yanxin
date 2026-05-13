package com.yanxin.zhijian.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Data
@TableName("service_type")
public class ServiceType {
    @TableId(type = IdType.AUTO) 
    private Long id;
    
    private String name;
    
    private String description;
    
    private Integer status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") 
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8") 
    private LocalDateTime updateTime;
    
    @TableLogic 
    private Integer deleted;
}
