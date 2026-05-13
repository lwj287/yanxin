package com.yanxin.training.controller;

import com.yanxin.training.common.Result;
import com.yanxin.training.service.CertificationService;
import com.yanxin.training.service.CourseService;
import com.yanxin.training.service.UserService;
import com.yanxin.training.mapper.CourseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RestController
@RequestMapping("/stat")
@RequiredArgsConstructor
public class StatController {

    private final UserService userService;
    private final CourseService courseService;
    private final CertificationService certificationService;
    private final CourseMapper courseMapper;

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardStat() {
        Map<String, Object> stat = new HashMap<>();
        
        // 平台总员工数 (过滤管理员)
        long userCount = userService.lambdaQuery().eq(com.yanxin.training.entity.User::getRoleId, 3).count();
        stat.put("userCount", userCount);

        // 在线课程数 (全部)
        long courseCount = courseService.count();
        stat.put("courseCount", courseCount);

        // 累计学习进度(总和)
        Integer totalProgress = courseMapper.getTotalStudyDuration();
        stat.put("totalStudyHours", totalProgress != null ? totalProgress : 0);

        // 认证通过人数
        long certCount = certificationService.lambdaQuery().eq(com.yanxin.training.entity.Certification::getCertStatus, "已通过").count();
        stat.put("certCount", certCount);

        // 近七日学习趋势 (进度累加)
        List<Map<String, Object>> trendData = courseMapper.getRecentStudyTrend();
        
        // 补全近7天的空数据
        List<String> dateList = new ArrayList<>();
        List<Integer> durationList = new ArrayList<>();
        
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            dateList.add(d.format(formatter));
            
            String dbDateStr = d.format(dbFormatter);
            Integer duration = 0;
            for (Map<String, Object> row : trendData) {
                if (dbDateStr.equals(row.get("date"))) {
                    duration = ((Number) row.get("duration")).intValue(); // 直接使用进度累加值
                    break;
                }
            }
            durationList.add(duration);
        }
        
        stat.put("trendDates", dateList);
        stat.put("trendDurations", durationList);

        // 热门课程分类占比
        List<Map<String, Object>> categoryData = courseMapper.getCategoryDistribution();
        stat.put("categoryData", categoryData);

        return Result.success(stat);
    }
}