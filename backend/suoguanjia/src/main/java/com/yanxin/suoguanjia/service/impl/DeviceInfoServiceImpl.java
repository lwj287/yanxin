package com.yanxin.suoguanjia.service.impl;

import com.yanxin.suoguanjia.entity.DeviceInfo;
import com.yanxin.suoguanjia.mapper.DeviceInfoMapper;
import com.yanxin.suoguanjia.service.DeviceInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 设备基础信息 服务实现类
 */
@Service
public class DeviceInfoServiceImpl extends ServiceImpl<DeviceInfoMapper, DeviceInfo> implements DeviceInfoService {
}
