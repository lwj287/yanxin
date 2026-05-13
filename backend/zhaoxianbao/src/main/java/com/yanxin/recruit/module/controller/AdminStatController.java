package com.yanxin.recruit.module.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanxin.recruit.common.api.ApiResult;
import com.yanxin.recruit.common.enums.OnboardStatusEnum;
import com.yanxin.recruit.module.entity.InterviewSchedule;
import com.yanxin.recruit.module.entity.OnboardingRecord;
import com.yanxin.recruit.module.entity.ResumeInfo;
import com.yanxin.recruit.module.mapper.InterviewScheduleMapper;
import com.yanxin.recruit.module.mapper.OnboardingRecordMapper;
import com.yanxin.recruit.module.mapper.ResumeInfoMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/stat")
@RequiredArgsConstructor
public class AdminStatController {
    private final ResumeInfoMapper resumeInfoMapper;
    private final InterviewScheduleMapper interviewScheduleMapper;
    private final OnboardingRecordMapper onboardingRecordMapper;

    @GetMapping("/overview")
    public ApiResult<?> overview(@RequestParam(required = false) String startDate,
                                 @RequestParam(required = false) String endDate) {
        LocalDateTime start = parseStart(startDate);
        LocalDateTime end = parseEnd(endDate);

        LambdaQueryWrapper<ResumeInfo> resumeQw = new LambdaQueryWrapper<ResumeInfo>()
                .ge(start != null, ResumeInfo::getCreateTime, start)
                .le(end != null, ResumeInfo::getCreateTime, end);
        LambdaQueryWrapper<InterviewSchedule> interviewQw = new LambdaQueryWrapper<InterviewSchedule>()
                .ge(start != null, InterviewSchedule::getCreateTime, start)
                .le(end != null, InterviewSchedule::getCreateTime, end);
        LambdaQueryWrapper<OnboardingRecord> onboardQw = new LambdaQueryWrapper<OnboardingRecord>()
                .eq(OnboardingRecord::getOnboardStatus, OnboardStatusEnum.ONBOARDED.name())
                .ge(start != null, OnboardingRecord::getCreateTime, start)
                .le(end != null, OnboardingRecord::getCreateTime, end);

        long applyCount = resumeInfoMapper.selectCount(resumeQw);
        long interviewCount = interviewScheduleMapper.selectCount(interviewQw);
        long onboardCount = onboardingRecordMapper.selectCount(onboardQw);
        BigDecimal attendRate = applyCount == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(interviewCount * 100.0 / applyCount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal onboardRate = interviewCount == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(onboardCount * 100.0 / interviewCount).setScale(2, RoundingMode.HALF_UP);

        List<String> labels = buildLabels(start, end);
        List<Integer> trendApply = new ArrayList<>();
        List<Integer> trendInterview = new ArrayList<>();
        List<Integer> trendOnboard = new ArrayList<>();

        Map<LocalDate, Long> applyMap = resumeInfoMapper.selectList(new LambdaQueryWrapper<ResumeInfo>()
                        .ge(start != null, ResumeInfo::getCreateTime, start)
                        .le(end != null, ResumeInfo::getCreateTime, end))
                .stream().collect(Collectors.groupingBy(i -> i.getCreateTime().toLocalDate(), Collectors.counting()));
        Map<LocalDate, Long> interviewMap = interviewScheduleMapper.selectList(new LambdaQueryWrapper<InterviewSchedule>()
                        .ge(start != null, InterviewSchedule::getCreateTime, start)
                        .le(end != null, InterviewSchedule::getCreateTime, end))
                .stream().collect(Collectors.groupingBy(i -> i.getCreateTime().toLocalDate(), Collectors.counting()));
        Map<LocalDate, Long> onboardMap = onboardingRecordMapper.selectList(new LambdaQueryWrapper<OnboardingRecord>()
                        .eq(OnboardingRecord::getOnboardStatus, OnboardStatusEnum.ONBOARDED.name())
                        .ge(start != null, OnboardingRecord::getCreateTime, start)
                        .le(end != null, OnboardingRecord::getCreateTime, end))
                .stream().collect(Collectors.groupingBy(i -> i.getCreateTime().toLocalDate(), Collectors.counting()));

        for (String label : labels) {
            LocalDate d = LocalDate.parse(label);
            trendApply.add(applyMap.getOrDefault(d, 0L).intValue());
            trendInterview.add(interviewMap.getOrDefault(d, 0L).intValue());
            trendOnboard.add(onboardMap.getOrDefault(d, 0L).intValue());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("applyCount", applyCount);
        data.put("interviewCount", interviewCount);
        data.put("onboardCount", onboardCount);
        data.put("attendRate", attendRate);
        data.put("onboardRate", onboardRate);
        data.put("labels", labels);
        data.put("trendApply", trendApply);
        data.put("trendInterview", trendInterview);
        data.put("trendOnboard", trendOnboard);
        data.put("startDate", startDate);
        data.put("endDate", endDate);
        return ApiResult.ok(data);
    }

    @GetMapping("/export")
    public void export(@RequestParam(required = false) String startDate,
                       @RequestParam(required = false) String endDate,
                       HttpServletResponse response) throws IOException {
        Map<String, Object> data = (Map<String, Object>) overview(startDate, endDate).getData();
        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.writeRow(java.util.Arrays.asList("指标", "值"));
        writer.writeRow(java.util.Arrays.asList("统计区间", (startDate == null || startDate.isBlank() ? "不限" : startDate) + " ~ " + (endDate == null || endDate.isBlank() ? "不限" : endDate)));
        writer.writeRow(java.util.Arrays.asList("投递量", data.get("applyCount")));
        writer.writeRow(java.util.Arrays.asList("到面量", data.get("interviewCount")));
        writer.writeRow(java.util.Arrays.asList("入职量", data.get("onboardCount")));
        writer.writeRow(java.util.Arrays.asList("到面率", data.get("attendRate") + "%"));
        writer.writeRow(java.util.Arrays.asList("入职率", data.get("onboardRate") + "%"));
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=recruit-report.xlsx");
        writer.flush(response.getOutputStream(), true);
        writer.close();
    }

    private LocalDateTime parseStart(String startDate) {
        if (startDate == null || startDate.isBlank()) return null;
        return LocalDate.parse(startDate).atStartOfDay();
    }

    private LocalDateTime parseEnd(String endDate) {
        if (endDate == null || endDate.isBlank()) return null;
        return LocalDate.parse(endDate).atTime(23, 59, 59);
    }

    private List<String> buildLabels(LocalDateTime start, LocalDateTime end) {
        LocalDate s;
        LocalDate e;
        if (start == null || end == null) {
            e = LocalDate.now();
            s = e.minusDays(6);
        } else {
            s = start.toLocalDate();
            e = end.toLocalDate();
        }
        List<String> labels = new ArrayList<>();
        LocalDate cur = s;
        while (!cur.isAfter(e)) {
            labels.add(cur.toString());
            cur = cur.plusDays(1);
        }
        return labels;
    }
}
