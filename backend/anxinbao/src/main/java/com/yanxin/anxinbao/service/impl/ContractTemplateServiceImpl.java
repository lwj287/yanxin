package com.yanxin.anxinbao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.anxinbao.entity.ContractTemplate;
import com.yanxin.anxinbao.mapper.ContractTemplateMapper;
import com.yanxin.anxinbao.service.IContractTemplateService;
import org.springframework.stereotype.Service;

@Service
public class ContractTemplateServiceImpl extends ServiceImpl<ContractTemplateMapper, ContractTemplate> implements IContractTemplateService {
}
