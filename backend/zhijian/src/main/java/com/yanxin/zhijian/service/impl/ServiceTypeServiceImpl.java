package com.yanxin.zhijian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.zhijian.entity.ServiceType;
import com.yanxin.zhijian.mapper.ServiceTypeMapper;
import com.yanxin.zhijian.service.IServiceTypeService;
import org.springframework.stereotype.Service;

@Service
public class ServiceTypeServiceImpl extends ServiceImpl<ServiceTypeMapper, ServiceType> implements IServiceTypeService {
}
