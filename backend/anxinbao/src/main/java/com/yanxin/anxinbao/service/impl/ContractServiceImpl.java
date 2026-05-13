package com.yanxin.anxinbao.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.anxinbao.entity.Contract;
import com.yanxin.anxinbao.mapper.ContractMapper;
import com.yanxin.anxinbao.service.IContractService;
import org.springframework.stereotype.Service;

@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract> implements IContractService {
}
