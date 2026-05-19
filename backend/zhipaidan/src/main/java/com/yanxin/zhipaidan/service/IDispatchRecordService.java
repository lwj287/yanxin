package com.yanxin.zhipaidan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanxin.zhipaidan.entity.DispatchRecord;

public interface IDispatchRecordService extends IService<DispatchRecord> {
    
    /**
     * 开始服务
     * @param id 派单记录ID
     * @return 是否成功
     */
    boolean startService(Long id);

    /**
     * 完成派单服务
     * @param id 派单记录ID
     * @return 是否成功
     */
    boolean finishService(Long id);
}
