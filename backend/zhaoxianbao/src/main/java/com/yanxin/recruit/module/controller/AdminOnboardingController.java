package com.yanxin.recruit.module.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanxin.recruit.common.api.ApiResult;
import com.yanxin.recruit.common.enums.OnboardStatusEnum;
import com.yanxin.recruit.common.enums.ResumeStatusEnum;
import com.yanxin.recruit.common.exception.BizException;
import com.yanxin.recruit.module.entity.JobPost;
import com.yanxin.recruit.module.entity.OnboardingRecord;
import com.yanxin.recruit.module.entity.ResumeInfo;
import com.yanxin.recruit.module.entity.SysNotice;
import com.yanxin.recruit.module.mapper.JobPostMapper;
import com.yanxin.recruit.module.mapper.OnboardingRecordMapper;
import com.yanxin.recruit.module.mapper.ResumeInfoMapper;
import com.yanxin.recruit.module.mapper.SysNoticeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/admin/onboarding")
@RequiredArgsConstructor
public class AdminOnboardingController {
    private final OnboardingRecordMapper onboardingRecordMapper;
    private final ResumeInfoMapper resumeInfoMapper;
    private final SysNoticeMapper sysNoticeMapper;
    private final JobPostMapper jobPostMapper;

    @GetMapping("/page")
    public ApiResult<?> page(@RequestParam(required = false) String onboardStatus,
                             @RequestParam(required = false) Long resumeId,
                             @RequestParam(required = false) Integer docsVerified) {
        LambdaQueryWrapper<OnboardingRecord> qw = new LambdaQueryWrapper<OnboardingRecord>()
                .eq(onboardStatus != null && !onboardStatus.isBlank(), OnboardingRecord::getOnboardStatus, onboardStatus)
                .eq(resumeId != null, OnboardingRecord::getResumeId, resumeId)
                .eq(docsVerified != null, OnboardingRecord::getDocsVerified, docsVerified)
                .orderByDesc(OnboardingRecord::getCreateTime);
        return ApiResult.ok(onboardingRecordMapper.selectList(qw));
    }

    @PostMapping
    public ApiResult<?> add(@RequestBody OnboardingRecord record) {
        if (record.getResumeId() == null) throw new BizException("简历ID不能为空");
        ResumeInfo resume = resumeInfoMapper.selectById(record.getResumeId());
        if (resume == null) throw new BizException("对应简历不存在");
        if (record.getOnboardStatus() == null || record.getOnboardStatus().isBlank()) {
            record.setOnboardStatus(OnboardStatusEnum.PENDING_DOCS.name());
        }
        if (record.getDocsVerified() == null) {
            record.setDocsVerified(0);
        }
        onboardingRecordMapper.insert(record);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    public ApiResult<?> edit(@PathVariable Long id, @RequestBody OnboardingRecord record) {
        OnboardingRecord old = onboardingRecordMapper.selectById(id);
        if (old == null) throw new BizException("入职记录不存在");
        record.setId(id);
        onboardingRecordMapper.updateById(record);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResult<?> delete(@PathVariable Long id) {
        OnboardingRecord old = onboardingRecordMapper.selectById(id);
        if (old == null) throw new BizException("入职记录不存在");
        onboardingRecordMapper.deleteById(id);
        return ApiResult.ok();
    }

    @PostMapping("/{id}/verify-docs")
    public ApiResult<?> verifyDocs(@PathVariable Long id) {
        OnboardingRecord record = onboardingRecordMapper.selectById(id);
        if (record == null) throw new BizException("入职记录不存在");
        record.setDocsVerified(1);
        record.setOnboardStatus(OnboardStatusEnum.WAIT_ONBOARD.name());
        onboardingRecordMapper.updateById(record);
        return ApiResult.ok();
    }

    @PostMapping("/{id}/confirm")
    public ApiResult<?> confirm(@PathVariable Long id, @RequestParam(required = false) String contractNo) {
        OnboardingRecord record = onboardingRecordMapper.selectById(id);
        if (record == null) throw new BizException("入职记录不存在");
        record.setOnboardStatus(OnboardStatusEnum.ONBOARDED.name());
        record.setOnboardingDate(LocalDate.now());
        record.setContractNo(contractNo);
        onboardingRecordMapper.updateById(record);

        ResumeInfo resume = resumeInfoMapper.selectById(record.getResumeId());
        if (resume != null) {
            resume.setResumeStatus(ResumeStatusEnum.ONBOARDED.code());
            resumeInfoMapper.updateById(resume);
            
            JobPost job = jobPostMapper.selectById(resume.getJobId());
            String jobName = job != null ? job.getTitle() : "未知岗位";
            SysNotice notice = new SysNotice();
            notice.setReceiverType("CANDIDATE");
            notice.setReceiverId(resume.getCandidateId());
            notice.setTitle("入职确认通知");
            notice.setContent("恭喜！您已成功入职【" + jobName + "】岗位，欢迎加入我们！");
            notice.setReadFlag(0);
            sysNoticeMapper.insert(notice);
        }
        return ApiResult.ok();
    }
}
