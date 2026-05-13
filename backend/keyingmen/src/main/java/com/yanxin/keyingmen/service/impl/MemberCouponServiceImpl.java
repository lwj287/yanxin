package com.yanxin.keyingmen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.keyingmen.entity.MemberCoupon;
import com.yanxin.keyingmen.mapper.MemberCouponMapper;
import com.yanxin.keyingmen.service.IMemberCouponService;
import org.springframework.stereotype.Service;

@Service
public class MemberCouponServiceImpl extends ServiceImpl<MemberCouponMapper, MemberCoupon> implements IMemberCouponService {
}
