package com.yanxin.anxinbao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.anxinbao.entity.Claim;
import com.yanxin.anxinbao.mapper.ClaimMapper;
import com.yanxin.anxinbao.service.IClaimService;
import org.springframework.stereotype.Service;

@Service
public class ClaimServiceImpl extends ServiceImpl<ClaimMapper, Claim> implements IClaimService {
}