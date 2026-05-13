package com.yanxin.keyingmen.controller;

import com.yanxin.keyingmen.common.Result;
import com.yanxin.keyingmen.entity.vo.DashboardVO;
import com.yanxin.keyingmen.service.IMemberService;
import com.yanxin.keyingmen.service.IOrdersService;
import com.yanxin.keyingmen.service.IMemberCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanxin.keyingmen.entity.MemberCoupon;
import com.yanxin.keyingmen.entity.Orders;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final IMemberService memberService;
    private final IOrdersService ordersService;
    private final IMemberCouponService memberCouponService;

    @GetMapping("/stat")
    public Result<DashboardVO> getDashboardStat() {
        DashboardVO vo = new DashboardVO();

        // 1. Total Members
        vo.setTotalMembers(memberService.count());

        // 2. Total Orders
        vo.setTotalOrders(ordersService.count());

        // 3. Issued Coupons (All received coupons)
        vo.setIssuedCoupons(memberCouponService.count());

        // 4. Total Income
        QueryWrapper<Orders> incomeQw = new QueryWrapper<>();
        incomeQw.eq("pay_status", 1).select("IFNULL(SUM(pay_amount), 0) as pay_amount");
        Orders sumOrder = ordersService.getOne(incomeQw);
        vo.setTotalIncome(sumOrder != null && sumOrder.getPayAmount() != null ? sumOrder.getPayAmount() : BigDecimal.ZERO);

        // 5. Order Trend (Last 7 days)
        List<String> dates = new ArrayList<>();
        List<Long> orderCounts = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            
            QueryWrapper<Orders> trendQw = new QueryWrapper<>();
            trendQw.ge("create_time", date.atStartOfDay())
                   .lt("create_time", date.plusDays(1).atStartOfDay());
            orderCounts.add(ordersService.count(trendQw));
        }
        vo.setTrendDates(dates);
        vo.setTrendOrders(orderCounts);

        // 6. Conversion Rate
        vo.setCouponReceived(memberCouponService.count());
        QueryWrapper<MemberCoupon> usedQw = new QueryWrapper<>();
        usedQw.eq("status", 1);
        vo.setCouponUsed(memberCouponService.count(usedQw));
        
        QueryWrapper<MemberCoupon> expiredQw = new QueryWrapper<>();
        expiredQw.eq("status", 2);
        vo.setCouponExpired(memberCouponService.count(expiredQw));

        return Result.success(vo);
    }
}
