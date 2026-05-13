package com.yanxin.suoguanjia.service;

import com.yanxin.suoguanjia.dto.DeviceReportDTO;
import com.yanxin.suoguanjia.dto.UnlockReqDTO;
import com.yanxin.suoguanjia.vo.Result;

public interface DeviceControlService {
    
    /**
     * 远程开锁逻辑
     */
    Result<Boolean> remoteUnlock(UnlockReqDTO req);

    /**
     * 设备状态上报 (Mock设备主动调用)
     */
    Result<Boolean> reportStatus(DeviceReportDTO req);
}
