package com.yanxin.zhipaidan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanxin.zhipaidan.entity.ServiceOrder;
import com.yanxin.zhipaidan.entity.vo.MatchedStaffVO;

import java.util.List;

public interface IServiceOrderService extends IService<ServiceOrder> {
    
    /**
     * 为订单匹配合适的家政员
     * @param orderId 订单ID
     * @return 匹配的家政员列表
     */
    List<MatchedStaffVO> matchStaffForOrder(Long orderId);
    
    /**
     * 执行派单操作
     * @param orderId 订单ID
     * @param staffId 家政员ID
     * @return 是否成功
     */
    boolean dispatchOrder(Long orderId, Long staffId);
}
