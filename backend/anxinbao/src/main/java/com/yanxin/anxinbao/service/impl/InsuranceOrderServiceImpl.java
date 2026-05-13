package com.yanxin.anxinbao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.anxinbao.entity.InsuranceOrder;
import com.yanxin.anxinbao.mapper.InsuranceOrderMapper;
import com.yanxin.anxinbao.service.IInsuranceOrderService;
import org.springframework.stereotype.Service;

@Service
public class InsuranceOrderServiceImpl extends ServiceImpl<InsuranceOrderMapper, InsuranceOrder> implements IInsuranceOrderService {
}
