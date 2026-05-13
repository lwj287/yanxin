package com.yanxin.recruit.module.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yanxin.recruit.common.base.BaseEntity;
import lombok.Data;

@Data
@TableName("sys_operation_log")
public class SysOperationLog extends BaseEntity {
    private Long userId;
    private String username;
    private String moduleName;
    private String operationType;
    private String requestUri;
    private String requestMethod;
    private String requestParam;
    private String responseData;
    private String ip;
    private Long costMs;
}
