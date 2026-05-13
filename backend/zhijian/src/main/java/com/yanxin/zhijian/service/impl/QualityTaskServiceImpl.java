package com.yanxin.zhijian.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.zhijian.entity.QualityTask;
import com.yanxin.zhijian.mapper.QualityTaskMapper;
import com.yanxin.zhijian.service.IQualityTaskService;
import org.springframework.stereotype.Service;
@Service public class QualityTaskServiceImpl extends ServiceImpl<QualityTaskMapper, QualityTask> implements IQualityTaskService {}