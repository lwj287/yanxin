package com.yanxin.keyingmen.entity.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardVO {
    private Long totalMembers;
    private Long totalOrders;
    private Long issuedCoupons;
    private BigDecimal totalIncome;

    private List<String> trendDates;
    private List<Long> trendOrders;

    private Long couponReceived;
    private Long couponUsed;
    private Long couponExpired;
}
