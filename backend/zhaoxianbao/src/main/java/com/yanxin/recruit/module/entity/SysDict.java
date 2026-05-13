package com.yanxin.recruit.module.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yanxin.recruit.common.base.BaseEntity;
import lombok.Data;

@Data
@TableName("sys_dict")
public class SysDict extends BaseEntity {
    private String dictType;
    private String dictCode;
    private String dictName;
    private Integer sortNo;
    private String extValue;
}
