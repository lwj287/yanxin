package com.yanxin.zhipaidan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.zhipaidan.entity.DispatchRecord;
import com.yanxin.zhipaidan.entity.ServiceOrder;
import com.yanxin.zhipaidan.entity.StaffScheduleStatus;
import com.yanxin.zhipaidan.entity.vo.MatchedStaffVO;
import com.yanxin.zhipaidan.mapper.ServiceOrderMapper;
import com.yanxin.zhipaidan.service.IDispatchRecordService;
import com.yanxin.zhipaidan.service.IServiceOrderService;
import com.yanxin.zhipaidan.service.IStaffScheduleStatusService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceOrderServiceImpl extends ServiceImpl<ServiceOrderMapper, ServiceOrder> implements IServiceOrderService {

    @Autowired
    private IStaffScheduleStatusService staffScheduleStatusService;

    @Autowired
    private IDispatchRecordService dispatchRecordService;

    @Override
    public List<MatchedStaffVO> matchStaffForOrder(Long orderId) {
        // 1. 获取订单信息
        ServiceOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        String requiredSkill = order.getServiceType();
        LocalDateTime reqStart = order.getExpectStartTime();
        LocalDateTime reqEnd = order.getExpectEndTime();

        // 2. 查出所有未离职的家政员
        List<StaffScheduleStatus> allStaff = staffScheduleStatusService.list();
        List<MatchedStaffVO> matchedStaffs = new ArrayList<>();

        for (StaffScheduleStatus staff : allStaff) {
            // 规则一：技能匹配
            if (CollectionUtils.isEmpty(staff.getSkills()) || !staff.getSkills().contains(requiredSkill)) {
                continue; // 技能不符，直接跳过
            }

            // 规则二：状态过滤（如果是正在休息/请假，直接跳过）
            if (staff.getCurrentStatus() == 2) {
                continue;
            }

            // 规则三：时间冲突校验（包含 1 小时通勤空档期）
            LocalDateTime reqStartWithBuffer = reqStart.minusHours(1);
            LocalDateTime reqEndWithBuffer = reqEnd.plusHours(1);

            QueryWrapper<DispatchRecord> conflictQuery = new QueryWrapper<>();
            conflictQuery.eq("staff_id", staff.getStaffId())
                    .ne("status", 3) // 排除已撤销的派单
                    // 判断时间重叠: (已有订单开始 < 新订单结束+1h) AND (已有订单结束 > 新订单开始-1h)
                    .lt("plan_start_time", reqEndWithBuffer)
                    .gt("plan_end_time", reqStartWithBuffer);
            
            long conflictCount = dispatchRecordService.count(conflictQuery);
            if (conflictCount > 0) {
                continue; // 时间有冲突，跳过
            }

            // 满足条件，计算大概距离（球面距离粗算，这里简单用欧式距离代替演示）
            MatchedStaffVO vo = new MatchedStaffVO();
            BeanUtils.copyProperties(staff, vo);
            
            double distance = calculateDistance(order.getLongitude(), order.getLatitude(), 
                                              staff.getLastLongitude(), staff.getLastLatitude());
            vo.setDistance(distance);
            
            // 简单的匹配度打分：距离越近分数越高（这里简单用 10000 - 距离 表示）
            vo.setMatchScore((int) Math.max(0, 10000 - distance));
            
            matchedStaffs.add(vo);
        }

        // 4. 按匹配度(分数)降序，距离升序排序返回
        return matchedStaffs.stream()
                .sorted(Comparator.comparing(MatchedStaffVO::getMatchScore).reversed()
                        .thenComparing(MatchedStaffVO::getDistance))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean dispatchOrder(Long orderId, Long staffId) {
        ServiceOrder order = this.getById(orderId);
        if (order == null || order.getStatus() != 0) {
            throw new RuntimeException("订单不存在或不是待派单状态");
        }
        
        StaffScheduleStatus staff = staffScheduleStatusService.getOne(
                new QueryWrapper<StaffScheduleStatus>().eq("staff_id", staffId));
        if (staff == null) {
            throw new RuntimeException("家政员不存在");
        }

        // 1. 插入派单记录
        DispatchRecord record = new DispatchRecord();
        record.setOrderId(orderId);
        record.setOrderNo(order.getOrderNo());
        record.setStaffId(staffId);
        record.setStaffName(staff.getStaffName());
        record.setPlanStartTime(order.getExpectStartTime());
        record.setPlanEndTime(order.getExpectEndTime());
        record.setDispatchType(0); // 人工确认
        
        double distance = calculateDistance(order.getLongitude(), order.getLatitude(), 
                                          staff.getLastLongitude(), staff.getLastLatitude());
        record.setEstimatedDistance((int) distance);
        record.setStatus(1); // 已接单/生效中
        
        dispatchRecordService.save(record);

        // 2. 更新订单状态
        order.setStatus(1); // 1-已派单
        this.updateById(order);

        return true;
    }

    /**
     * 简易经纬度距离计算（单位：米）。
     * 实际生产中建议使用 GeoHash 或调用地图 API。
     */
    private double calculateDistance(BigDecimal lon1, BigDecimal lat1, BigDecimal lon2, BigDecimal lat2) {
        if (lon1 == null || lat1 == null || lon2 == null || lat2 == null) {
            return 999999.0; // 缺坐标则返回极大值
        }
        
        double radLat1 = Math.toRadians(lat1.doubleValue());
        double radLat2 = Math.toRadians(lat2.doubleValue());
        double a = radLat1 - radLat2;
        double b = Math.toRadians(lon1.doubleValue()) - Math.toRadians(lon2.doubleValue());
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        // 地球半径 6378137 米
        return s * 6378137.0;
    }
}
