package com.yanxin.anxinbao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.anxinbao.entity.InsuranceProduct;
import com.yanxin.anxinbao.mapper.InsuranceProductMapper;
import com.yanxin.anxinbao.service.IInsuranceProductService;
import org.springframework.stereotype.Service;

@Service
public class InsuranceProductServiceImpl extends ServiceImpl<InsuranceProductMapper, InsuranceProduct> implements IInsuranceProductService {
}