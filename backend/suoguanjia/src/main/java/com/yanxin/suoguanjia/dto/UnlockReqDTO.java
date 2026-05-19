package com.yanxin.suoguanjia.dto;

import lombok.Data;

@Data
public class UnlockReqDTO {
    private Long deviceId;
    private Long operatorId; // 实际场景中应该从Token中获取，MVP阶段先通过参数传递
}
