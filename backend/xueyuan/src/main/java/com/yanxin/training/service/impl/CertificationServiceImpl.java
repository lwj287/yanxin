package com.yanxin.training.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.training.entity.Certification;
import com.yanxin.training.mapper.CertificationMapper;
import com.yanxin.training.service.CertificationService;
import org.springframework.stereotype.Service;

@Service
public class CertificationServiceImpl extends ServiceImpl<CertificationMapper, Certification> implements CertificationService {
}