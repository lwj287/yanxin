package com.yanxin.zhijian.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.zhijian.entity.QualityTemplate;
import com.yanxin.zhijian.mapper.QualityTemplateMapper;
import com.yanxin.zhijian.service.IQualityTemplateService;
import org.springframework.stereotype.Service;
@Service public class QualityTemplateServiceImpl extends ServiceImpl<QualityTemplateMapper, QualityTemplate> implements IQualityTemplateService {}