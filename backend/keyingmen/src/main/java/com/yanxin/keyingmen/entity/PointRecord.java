package com.yanxin.keyingmen.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
@TableName("point_record")
public class PointRecord {
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long recordId;
    private Long memberId;
    private Long orderId;
    private Integer pointsChange;
    private Integer changeRate;
    @com.baomidou.mybatisplus.annotation.TableLogic
    private Integer isDeleted;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private java.time.LocalDateTime createTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private java.time.LocalDateTime updateTime;
}
