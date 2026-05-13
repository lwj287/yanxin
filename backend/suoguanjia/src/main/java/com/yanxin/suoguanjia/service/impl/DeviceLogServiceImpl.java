package com.yanxin.suoguanjia.service.impl;

import com.yanxin.suoguanjia.entity.DeviceLog;
import com.yanxin.suoguanjia.mapper.DeviceLogMapper;
import com.yanxin.suoguanjia.service.DeviceLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 设备操作与状态日志 服务实现类
 */
@Service
public class DeviceLogServiceImpl extends ServiceImpl<DeviceLogMapper, DeviceLog> implements DeviceLogService {
}
