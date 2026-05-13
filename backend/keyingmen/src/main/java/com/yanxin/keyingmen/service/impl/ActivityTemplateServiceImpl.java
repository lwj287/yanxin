package com.yanxin.keyingmen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.keyingmen.entity.ActivityTemplate;
import com.yanxin.keyingmen.mapper.ActivityTemplateMapper;
import com.yanxin.keyingmen.service.IActivityTemplateService;
import org.springframework.stereotype.Service;

@Service
public class ActivityTemplateServiceImpl extends ServiceImpl<ActivityTemplateMapper, ActivityTemplate> implements IActivityTemplateService {
}
