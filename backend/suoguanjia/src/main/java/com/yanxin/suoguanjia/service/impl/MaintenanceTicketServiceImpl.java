package com.yanxin.suoguanjia.service.impl;

import com.yanxin.suoguanjia.entity.MaintenanceTicket;
import com.yanxin.suoguanjia.mapper.MaintenanceTicketMapper;
import com.yanxin.suoguanjia.service.MaintenanceTicketService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 维修工单 服务实现类
 */
@Service
public class MaintenanceTicketServiceImpl extends ServiceImpl<MaintenanceTicketMapper, MaintenanceTicket> implements MaintenanceTicketService {
}
