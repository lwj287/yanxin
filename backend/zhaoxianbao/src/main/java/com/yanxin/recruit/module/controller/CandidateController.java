package com.yanxin.recruit.module.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanxin.recruit.common.api.ApiResult;
import com.yanxin.recruit.common.enums.ResumeStatusEnum;
import com.yanxin.recruit.common.exception.BizException;
import com.yanxin.recruit.common.security.SecurityUtil;
import com.yanxin.recruit.module.entity.*;
import com.yanxin.recruit.module.mapper.*;
import com.yanxin.recruit.module.service.JobPostService;
import com.yanxin.recruit.module.service.ResumeInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/candidate")
@RequiredArgsConstructor
public class CandidateController {
    private final JobPostService jobPostService;
    private final ResumeInfoService resumeInfoService;
    private final InterviewScheduleMapper interviewScheduleMapper;
    private final OnboardingRecordMapper onboardingRecordMapper;
    private final SysNoticeMapper sysNoticeMapper;
    private final CandidateUserMapper candidateUserMapper;
    private final CandidateResumeProfileMapper candidateResumeProfileMapper;
    private final JobPostMapper jobPostMapper;

    @GetMapping("/job/page")
    public ApiResult<?> listJobs(@RequestParam int pageNo, @RequestParam int pageSize,
                                 @RequestParam(required = false) String keyword,
                                 @RequestParam(required = false) String cityCode,
                                 @RequestParam(required = false) String educationCode,
                                 @RequestParam(required = false) Integer age,
                                 @RequestParam(required = false) Integer expYears,
                                 @RequestParam(required = false) String skillCode) {
        return ApiResult.ok(jobPostService.pageQuery(pageNo, pageSize, keyword, cityCode, educationCode, age, expYears, skillCode, 1));
    }

    @GetMapping("/job/{id}")
    public ApiResult<?> getJob(@PathVariable Long id) {
        JobPost job = jobPostService.getById(id);
        if (job == null || job.getPublishStatus() != 1) {
            throw new BizException("岗位不存在或已下架");
        }
        return ApiResult.ok(job);
    }

    @PostMapping("/resume/submit")
    public ApiResult<?> submitResume(@RequestBody ResumeInfo resume) {
        Long candidateId = SecurityUtil.currentUserId();
        if (resume.getJobId() == null) {
            throw new BizException("岗位ID不能为空");
        }
        CandidateResumeProfile profile = candidateResumeProfileMapper.selectOne(new LambdaQueryWrapper<CandidateResumeProfile>()
                .eq(CandidateResumeProfile::getCandidateId, candidateId));

        ResumeInfo snapshot = new ResumeInfo();
        snapshot.setCandidateId(candidateId);
        snapshot.setJobId(resume.getJobId());
        if (profile != null) {
            snapshot.setRealName(profile.getRealName());
            snapshot.setPhone(profile.getPhone());
            snapshot.setAge(profile.getAge());
            snapshot.setEducationCode(profile.getEducationText());
            snapshot.setExperienceYears(profile.getExperienceYears());
            snapshot.setSkills(profile.getSkillsText());
            snapshot.setExpectedCityCode(profile.getExpectedCityText());
            snapshot.setSelfIntroduction(profile.getSelfIntroduction());
            snapshot.setPhotoUrl(profile.getPhotoUrl());
            snapshot.setIdCardFrontUrl(profile.getIdCardFrontUrl());
            snapshot.setIdCardBackUrl(profile.getIdCardBackUrl());
        }

        if (resume.getRealName() != null && !resume.getRealName().isBlank()) snapshot.setRealName(resume.getRealName());
        if (resume.getPhone() != null && !resume.getPhone().isBlank()) snapshot.setPhone(resume.getPhone());
        if (resume.getAge() != null) snapshot.setAge(resume.getAge());
        if (resume.getEducationCode() != null && !resume.getEducationCode().isBlank()) snapshot.setEducationCode(resume.getEducationCode());
        if (resume.getExperienceYears() != null) snapshot.setExperienceYears(resume.getExperienceYears());
        if (resume.getSkills() != null && !resume.getSkills().isBlank()) snapshot.setSkills(resume.getSkills());
        if (resume.getExpectedCityCode() != null && !resume.getExpectedCityCode().isBlank()) snapshot.setExpectedCityCode(resume.getExpectedCityCode());
        if (resume.getSelfIntroduction() != null && !resume.getSelfIntroduction().isBlank()) snapshot.setSelfIntroduction(resume.getSelfIntroduction());
        if (resume.getPhotoUrl() != null && !resume.getPhotoUrl().isBlank()) snapshot.setPhotoUrl(resume.getPhotoUrl());
        if (resume.getIdCardFrontUrl() != null && !resume.getIdCardFrontUrl().isBlank()) snapshot.setIdCardFrontUrl(resume.getIdCardFrontUrl());
        if (resume.getIdCardBackUrl() != null && !resume.getIdCardBackUrl().isBlank()) snapshot.setIdCardBackUrl(resume.getIdCardBackUrl());

        if (snapshot.getRealName() == null || snapshot.getRealName().isBlank()) {
            throw new BizException("请先完善在线简历后再投递");
        }
        if (snapshot.getPhone() == null || snapshot.getPhone().isBlank()) {
            throw new BizException("请先完善在线简历手机号后再投递");
        }

        snapshot.setResumeStatus(ResumeStatusEnum.SUBMITTED.code());
        snapshot.setScreeningResult(0);
        resumeInfoService.save(snapshot);
        CandidateUser candidateUser = new CandidateUser();
        candidateUser.setId(candidateId);
        candidateUser.setResumeDeliveryStatus(1);
        candidateUserMapper.updateById(candidateUser);

        JobPost job = jobPostService.getById(resume.getJobId());
        String jobName = job != null ? job.getTitle() : "未知岗位";
        SysNotice notice = new SysNotice();
        notice.setReceiverType("CANDIDATE");
        notice.setReceiverId(candidateId);
        notice.setTitle("简历投递成功");
        notice.setContent("您已成功投递【" + jobName + "】岗位，请耐心等待筛选结果。");
        notice.setReadFlag(0);
        sysNoticeMapper.insert(notice);

        resumeInfoService.smartScreen(snapshot.getId());
        return ApiResult.ok();
    }

    @GetMapping("/resume/my")
    public ApiResult<?> myResume() {
        List<ResumeInfo> list = resumeInfoService.list(new LambdaQueryWrapper<ResumeInfo>()
                .eq(ResumeInfo::getCandidateId, SecurityUtil.currentUserId())
                .orderByDesc(ResumeInfo::getCreateTime));
        if (list.isEmpty()) {
            return ApiResult.ok(java.util.Collections.emptyList());
        }
        List<Long> jobIds = list.stream()
                .map(ResumeInfo::getJobId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        Map<Long, JobPost> jobMap = jobPostMapper.selectList(new LambdaQueryWrapper<JobPost>()
                        .in(JobPost::getId, jobIds))
                .stream()
                .collect(Collectors.toMap(JobPost::getId, j -> j));

        List<Map<String, Object>> result = new ArrayList<>();
        for (ResumeInfo r : list) {
            Map<String, Object> row = new HashMap<>();
            row.put("resumeId", r.getId());
            row.put("jobId", r.getJobId());
            row.put("resumeStatus", r.getResumeStatus());
            row.put("screeningResult", r.getScreeningResult());
            row.put("screeningReason", r.getScreeningReason());
            row.put("createTime", r.getCreateTime());
            JobPost job = jobMap.get(r.getJobId());
            if (job != null) {
                row.put("jobTitle", job.getTitle());
                row.put("cityCode", job.getCityCode());
                row.put("jobTypeCode", job.getJobTypeCode());
                row.put("salaryMin", job.getSalaryMin());
                row.put("salaryMax", job.getSalaryMax());
            }
            result.add(row);
        }
        return ApiResult.ok(result);
    }

    @GetMapping("/profile/my")
    public ApiResult<?> myResumeProfile() {
        return ApiResult.ok(candidateResumeProfileMapper.selectOne(new LambdaQueryWrapper<CandidateResumeProfile>()
                .eq(CandidateResumeProfile::getCandidateId, SecurityUtil.currentUserId())));
    }

    @PostMapping("/profile/my")
    public ApiResult<?> saveResumeProfile(@RequestBody CandidateResumeProfile profile) {
        Long candidateId = SecurityUtil.currentUserId();
        if (profile.getRealName() == null || profile.getRealName().isBlank()) {
            throw new BizException("姓名不能为空");
        }
        if (profile.getPhone() == null || profile.getPhone().isBlank()) {
            throw new BizException("手机号不能为空");
        }
        CandidateResumeProfile old = candidateResumeProfileMapper.selectOne(new LambdaQueryWrapper<CandidateResumeProfile>()
                .eq(CandidateResumeProfile::getCandidateId, candidateId));
        profile.setCandidateId(candidateId);
        if (old == null) {
            profile.setId(null);
            candidateResumeProfileMapper.insert(profile);
            return ApiResult.ok(profile.getId());
        }
        profile.setId(old.getId());
        candidateResumeProfileMapper.updateById(profile);
        return ApiResult.ok(profile.getId());
    }

    @DeleteMapping("/profile/my")
    public ApiResult<?> deleteResumeProfile() {
        Long candidateId = SecurityUtil.currentUserId();
        CandidateResumeProfile old = candidateResumeProfileMapper.selectOne(new LambdaQueryWrapper<CandidateResumeProfile>()
                .eq(CandidateResumeProfile::getCandidateId, candidateId));
        if (old != null) {
            candidateResumeProfileMapper.deleteById(old.getId());
        }
        return ApiResult.ok();
    }

    @GetMapping("/interview/notice")
    public ApiResult<?> interviewNotice() {
        List<ResumeInfo> resumes = resumeInfoService.list(new LambdaQueryWrapper<ResumeInfo>()
                .eq(ResumeInfo::getCandidateId, SecurityUtil.currentUserId()));
        if (resumes.isEmpty()) return ApiResult.ok(java.util.Collections.emptyList());
        List<Long> resumeIds = resumes.stream().map(ResumeInfo::getId).toList();
        return ApiResult.ok(interviewScheduleMapper.selectList(new LambdaQueryWrapper<InterviewSchedule>()
                .in(InterviewSchedule::getResumeId, resumeIds)
                .orderByDesc(InterviewSchedule::getInterviewTime)));
    }

    @GetMapping("/onboarding/progress")
    public ApiResult<?> onboardingProgress() {
        List<ResumeInfo> resumes = resumeInfoService.list(new LambdaQueryWrapper<ResumeInfo>()
                .eq(ResumeInfo::getCandidateId, SecurityUtil.currentUserId()));
        if (resumes.isEmpty()) return ApiResult.ok(java.util.Collections.emptyList());
        List<Long> resumeIds = resumes.stream().map(ResumeInfo::getId).toList();
        return ApiResult.ok(onboardingRecordMapper.selectList(new LambdaQueryWrapper<OnboardingRecord>()
                .in(OnboardingRecord::getResumeId, resumeIds)
                .orderByDesc(OnboardingRecord::getCreateTime)));
    }

    @GetMapping("/notice")
    public ApiResult<?> notice() {
        return ApiResult.ok(sysNoticeMapper.selectList(new LambdaQueryWrapper<SysNotice>()
                .eq(SysNotice::getReceiverType, "CANDIDATE")
                .eq(SysNotice::getReceiverId, SecurityUtil.currentUserId())
                .orderByDesc(SysNotice::getCreateTime)));
    }

    @GetMapping("/notice/unread-count")
    public ApiResult<Long> unreadNoticeCount() {
        return ApiResult.ok(sysNoticeMapper.selectCount(new LambdaQueryWrapper<SysNotice>()
                .eq(SysNotice::getReceiverType, "CANDIDATE")
                .eq(SysNotice::getReceiverId, SecurityUtil.currentUserId())
                .eq(SysNotice::getReadFlag, 0)));
    }

    @PostMapping("/notice/mark-read")
    public ApiResult<?> markNoticeRead() {
        SysNotice update = new SysNotice();
        update.setReadFlag(1);
        sysNoticeMapper.update(update, new LambdaQueryWrapper<SysNotice>()
                .eq(SysNotice::getReceiverType, "CANDIDATE")
                .eq(SysNotice::getReceiverId, SecurityUtil.currentUserId())
                .eq(SysNotice::getReadFlag, 0));
        return ApiResult.ok();
    }
}
