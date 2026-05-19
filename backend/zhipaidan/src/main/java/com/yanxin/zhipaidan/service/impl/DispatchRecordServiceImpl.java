package com.yanxin.zhipaidan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.zhipaidan.entity.DispatchRecord;
import com.yanxin.zhipaidan.entity.ServiceOrder;
import com.yanxin.zhipaidan.entity.StaffScheduleStatus;
import com.yanxin.zhipaidan.mapper.DispatchRecordMapper;
import com.yanxin.zhipaidan.service.IDispatchRecordService;
import com.yanxin.zhipaidan.service.IServiceOrderService;
import com.yanxin.zhipaidan.service.IStaffScheduleStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DispatchRecordServiceImpl extends ServiceImpl<DispatchRecordMapper, DispatchRecord> implements IDispatchRecordService {

    @Autowired
    private IStaffScheduleStatusService staffScheduleStatusService;
    
    @Autowired
    @Lazy
    private IServiceOrderService serviceOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startService(Long id) {
        // 1. 获取派单记录
        DispatchRecord record = this.getById(id);
        if (record == null) {
            throw new RuntimeException("派单记录不存在");
        }
        
        // 2. 更新派单记录状态为服务中 (2) 
        record.setStatus(2);
        boolean updateRecord = this.updateById(record);
        
        // 3. 更新对应的订单状态为服务中 (2)
        ServiceOrder order = serviceOrderService.getById(record.getOrderId());
        if (order != null) {
            order.setStatus(2);
            serviceOrderService.updateById(order);
        }
        
        // 4. 更新家政员状态为服务中 (1)
        StaffScheduleStatus status = staffScheduleStatusService.lambdaQuery()
                .eq(StaffScheduleStatus::getStaffId, record.getStaffId())
                .one();
                
        if (status != null) {
            status.setCurrentStatus(1);
            staffScheduleStatusService.updateById(status);
        }
        
        return updateRecord;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean finishService(Long id) {
        // 1. 获取派单记录
        DispatchRecord record = this.getById(id);
        if (record == null) {
            throw new RuntimeException("派单记录不存在");
        }
        
        // 2. 更新派单记录状态为已完工 (3)
        record.setStatus(3);
        boolean updateRecord = this.updateById(record);
        
        // 3. 更新对应的订单状态为已完成 (3)
        ServiceOrder order = serviceOrderService.getById(record.getOrderId());
        if (order != null) {
            order.setStatus(3);
            serviceOrderService.updateById(order);
        }
        
        // 4. 更新家政员状态
        // 实际业务中需要判断该员工是否还有其他正在进行中的订单
        StaffScheduleStatus status = staffScheduleStatusService.lambdaQuery()
                .eq(StaffScheduleStatus::getStaffId, record.getStaffId())
                .one();
                
        if (status != null) {
            // 查询是否还有其他正在服务中的订单 (状态为 2)
            long activeCount = this.lambdaQuery()
                    .eq(DispatchRecord::getStaffId, record.getStaffId())
                    .eq(DispatchRecord::getStatus, 2)
                    .count();
            
            if (activeCount > 0) {
                status.setCurrentStatus(1); // 还有服务中的订单，保持服务中状态
            } else {
                status.setCurrentStatus(0); // 没有服务中的订单了，置为空闲
            }
            staffScheduleStatusService.updateById(status);
        }
        
        return updateRecord;
    }
}
