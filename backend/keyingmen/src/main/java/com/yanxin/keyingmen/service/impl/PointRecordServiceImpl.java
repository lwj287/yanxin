package com.yanxin.keyingmen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.keyingmen.entity.PointRecord;
import com.yanxin.keyingmen.mapper.PointRecordMapper;
import com.yanxin.keyingmen.service.IPointRecordService;
import org.springframework.stereotype.Service;

@Service
public class PointRecordServiceImpl extends ServiceImpl<PointRecordMapper, PointRecord> implements IPointRecordService {
}
