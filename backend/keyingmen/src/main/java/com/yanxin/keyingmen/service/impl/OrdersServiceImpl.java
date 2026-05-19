package com.yanxin.keyingmen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.keyingmen.entity.Member;
import com.yanxin.keyingmen.entity.Orders;
import com.yanxin.keyingmen.entity.PointRecord;
import com.yanxin.keyingmen.mapper.OrdersMapper;
import com.yanxin.keyingmen.service.IMemberService;
import com.yanxin.keyingmen.service.IOrdersService;
import com.yanxin.keyingmen.service.IPointRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

    @Autowired
    private IMemberService memberService;

    @Autowired
    private IPointRecordService pointRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Orders entity) {
        boolean result = super.save(entity);
        if (result && entity.getPayStatus() != null && entity.getPayStatus() == 1) {
            handlePointsReward(entity);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(Orders entity) {
        // 如果是要更新订单状态为已支付 (payStatus = 1)
        if (entity.getPayStatus() != null && entity.getPayStatus() == 1) {
            Orders oldOrder = this.getById(entity.getOrderId());
            // 如果原本不是已支付，现在变成已支付，则触发积分回馈
            if (oldOrder != null && (oldOrder.getPayStatus() == null || oldOrder.getPayStatus() != 1)) {
                handlePointsReward(entity);
            }
        }
        return super.updateById(entity);
    }

    private void handlePointsReward(Orders entity) {
        Long memberId = entity.getMemberId();
        if (memberId == null) {
            Orders dbOrder = this.getById(entity.getOrderId());
            if (dbOrder != null) {
                memberId = dbOrder.getMemberId();
            }
        }
        
        if (memberId != null) {
            Member member = memberService.getById(memberId);
            if (member != null) {
                // 根据规则：一单回馈一积分
                int pointsEarned = 1;
                // 根据规则：高级会员回馈系数翻倍 (memberLevel: 0-普通, 1-高级及以上)
                int changeRate = (member.getMemberLevel() != null && member.getMemberLevel() >= 1) ? 2 : 1;
                int finalPoints = pointsEarned * changeRate;

                // 1. 插入积分变动记录
                PointRecord pointRecord = new PointRecord();
                pointRecord.setMemberId(memberId);
                pointRecord.setOrderId(entity.getOrderId());
                pointRecord.setPointsChange(finalPoints);
                pointRecord.setChangeRate(changeRate);
                pointRecordService.save(pointRecord);

                // 2. 更新会员积分余额
                int currentBalance = member.getAccountBalance() != null ? member.getAccountBalance() : 0;
                member.setAccountBalance(currentBalance + finalPoints);
                memberService.updateById(member);
            }
        }
    }
}
