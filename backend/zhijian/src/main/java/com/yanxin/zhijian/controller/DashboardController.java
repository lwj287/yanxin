package com.yanxin.zhijian.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanxin.zhijian.common.Result;
import com.yanxin.zhijian.entity.QualityAppeal;
import com.yanxin.zhijian.entity.QualityTask;
import com.yanxin.zhijian.service.IQualityAppealService;
import com.yanxin.zhijian.service.IQualityTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/zhijian/dashboard")
public class DashboardController {

    @Autowired
    private IQualityTaskService taskService;

    @Autowired
    private IQualityAppealService appealService;

    @GetMapping("/stat")
    public Result<?> getDashboardStat() {
        Map<String, Object> stat = new HashMap<>();
        
        try {
            // 1. 总任务数
            long totalTasks = taskService.count();
            stat.put("totalTasks", totalTasks);

            // 2. 待处理任务 (状态为 UPLOADED 或 PENDING)
            QueryWrapper<QualityTask> pendingTaskWrapper = new QueryWrapper<>();
            pendingTaskWrapper.in("status", "UPLOADED", "PENDING");
            long pendingTasks = taskService.count(pendingTaskWrapper);
            stat.put("pendingTasks", pendingTasks);

            // 3. 今日已通过
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            QueryWrapper<QualityTask> todayPassedWrapper = new QueryWrapper<>();
            todayPassedWrapper.eq("status", "PASS")
                    .between("update_time", todayStart, todayEnd);
            long todayPassed = taskService.count(todayPassedWrapper);
            stat.put("todayPassed", todayPassed);

            // 4. 待处理申诉 (状态为 PENDING)
            QueryWrapper<QualityAppeal> pendingAppealWrapper = new QueryWrapper<>();
            pendingAppealWrapper.eq("status", "PENDING");
            long pendingAppeals = appealService.count(pendingAppealWrapper);
            stat.put("pendingAppeals", pendingAppeals);

            return Result.success(stat);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取统计数据失败");
        }
    }

    @GetMapping("/chart")
    public Result<?> getDashboardChart() {
        Map<String, Object> chartData = new HashMap<>();
        
        try {
            // 1. 质检合格率分布 (饼图)
            // 统计各种状态的数量
            QueryWrapper<QualityTask> passWrapper = new QueryWrapper<>();
            passWrapper.eq("status", "PASS");
            long passCount = taskService.count(passWrapper);
            
            QueryWrapper<QualityTask> rejectWrapper = new QueryWrapper<>();
            rejectWrapper.eq("status", "REJECT");
            long rejectCount = taskService.count(rejectWrapper);
            
            QueryWrapper<QualityTask> otherWrapper = new QueryWrapper<>();
            otherWrapper.in("status", "PENDING", "UPLOADED");
            long otherCount = taskService.count(otherWrapper);

            List<Map<String, Object>> statusDist = new ArrayList<>();
            statusDist.add(createChartItem("合格", passCount));
            statusDist.add(createChartItem("不合格", rejectCount));
            statusDist.add(createChartItem("处理中", otherCount));
            chartData.put("statusDist", statusDist);

            // 2. 近7日任务趋势 (折线图)
            List<String> dates = new ArrayList<>();
            List<Long> taskCounts = new ArrayList<>();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
            LocalDate today = LocalDate.now();
            
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                dates.add(date.format(formatter));
                
                LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
                
                QueryWrapper<QualityTask> dailyWrapper = new QueryWrapper<>();
                dailyWrapper.between("create_time", dayStart, dayEnd);
                taskCounts.add(taskService.count(dailyWrapper));
            }
            
            Map<String, Object> trend = new HashMap<>();
            trend.put("dates", dates);
            trend.put("counts", taskCounts);
            chartData.put("trend", trend);

            return Result.success(chartData);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取图表数据失败");
        }
    }

    private Map<String, Object> createChartItem(String name, Object value) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("value", value);
        return item;
    }
}
