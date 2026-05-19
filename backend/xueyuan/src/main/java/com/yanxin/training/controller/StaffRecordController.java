package com.yanxin.training.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.yanxin.training.common.Result;
import com.yanxin.training.entity.Certification;
import com.yanxin.training.entity.StaffExam;
import com.yanxin.training.entity.StaffLearning;
import com.yanxin.training.mapper.CertificationMapper;
import com.yanxin.training.mapper.StaffExamMapper;
import com.yanxin.training.mapper.StaffLearningMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffRecordController {

    private final StaffLearningMapper staffLearningMapper;
    private final StaffExamMapper staffExamMapper;
    private final CertificationMapper certificationMapper;

    @GetMapping("/learning/list")
    public Result<List<StaffLearning>> getLearningRecords() {
        String staffId = StpUtil.getLoginIdAsString();
        return Result.success(staffLearningMapper.getRecordsByStaffId(staffId));
    }

    @GetMapping("/exam/list")
    public Result<List<StaffExam>> getExamRecords() {
        String staffId = StpUtil.getLoginIdAsString();
        return Result.success(staffExamMapper.getRecordsByStaffId(staffId));
    }

    @GetMapping("/cert/list")
    public Result<List<Certification>> getCertRecords() {
        String staffId = StpUtil.getLoginIdAsString();
        return Result.success(certificationMapper.getRecordsByStaffId(staffId));
    }
}
