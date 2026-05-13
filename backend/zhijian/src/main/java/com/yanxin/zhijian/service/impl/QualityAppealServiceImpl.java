package com.yanxin.zhijian.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.zhijian.entity.QualityAppeal;
import com.yanxin.zhijian.mapper.QualityAppealMapper;
import com.yanxin.zhijian.service.IQualityAppealService;
import org.springframework.stereotype.Service;
@Service public class QualityAppealServiceImpl extends ServiceImpl<QualityAppealMapper, QualityAppeal> implements IQualityAppealService {}