package com.yanxin.keyingmen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.keyingmen.entity.MemberActivity;
import com.yanxin.keyingmen.mapper.MemberActivityMapper;
import com.yanxin.keyingmen.service.IMemberActivityService;
import org.springframework.stereotype.Service;

@Service
public class MemberActivityServiceImpl extends ServiceImpl<MemberActivityMapper, MemberActivity> implements IMemberActivityService {
}
