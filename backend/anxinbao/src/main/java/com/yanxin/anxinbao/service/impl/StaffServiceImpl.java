package com.yanxin.anxinbao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.anxinbao.entity.Staff;
import com.yanxin.anxinbao.mapper.StaffMapper;
import com.yanxin.anxinbao.service.IStaffService;
import org.springframework.stereotype.Service;

@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements IStaffService {
}