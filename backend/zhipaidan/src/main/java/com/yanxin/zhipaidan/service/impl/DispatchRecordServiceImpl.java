package com.yanxin.zhipaidan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.zhipaidan.entity.DispatchRecord;
import com.yanxin.zhipaidan.mapper.DispatchRecordMapper;
import com.yanxin.zhipaidan.service.IDispatchRecordService;
import org.springframework.stereotype.Service;

@Service
public class DispatchRecordServiceImpl extends ServiceImpl<DispatchRecordMapper, DispatchRecord> implements IDispatchRecordService {
}
