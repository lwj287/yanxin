package com.yanxin.anxinbao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("realname_auth")
public class RealnameAuth {
    @TableId(type = IdType.AUTO)
    private Long authId;
    private Long staffId;
    private Integer staffType;
    private String idCard;
    private String faceMaterialUrl;
    private Integer authStatus;
    private LocalDateTime authTime;
    private String authReport;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer isDeleted;
}