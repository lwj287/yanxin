package com.yanxin.keyingmen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.keyingmen.entity.CouponTemplate;
import com.yanxin.keyingmen.mapper.CouponTemplateMapper;
import com.yanxin.keyingmen.service.ICouponTemplateService;
import org.springframework.stereotype.Service;

@Service
public class CouponTemplateServiceImpl extends ServiceImpl<CouponTemplateMapper, CouponTemplate> implements ICouponTemplateService {
}
