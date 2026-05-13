package com.yanxin.recruit.module.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yanxin.recruit.common.api.ApiResult;
import com.yanxin.recruit.common.enums.InterviewStatusEnum;
import com.yanxin.recruit.common.enums.OnboardStatusEnum;
import com.yanxin.recruit.common.enums.ResumeStatusEnum;
import com.yanxin.recruit.common.exception.BizException;
import com.yanxin.recruit.common.security.SecurityUtil;
import com.yanxin.recruit.module.dto.InterviewFinishReq;
import com.yanxin.recruit.module.entity.*;
import com.yanxin.recruit.module.mapper.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/interview")
@RequiredArgsConstructor
public class AdminInterviewController {
    private final InterviewScheduleMapper interviewScheduleMapper;
    private final InterviewEvaluationMapper interviewEvaluationMapper;
    private final ResumeInfoMapper resumeInfoMapper;
    private final OnboardingRecordMapper onboardingRecordMapper;
    private final SysNoticeMapper sysNoticeMapper;
    private final JobPostMapper jobPostMapper;

    @PostMapping("/schedule")
    public ApiResult<?> schedule(@RequestBody InterviewSchedule schedule) {
        schedule.setInterviewStatus(InterviewStatusEnum.PENDING.name());
        schedule.setNoticeSent(0);
        interviewScheduleMapper.insert(schedule);
        return ApiResult.ok();
    }

    @PostMapping("/{id}/notice")
    public ApiResult<?> sendNotice(@PathVariable Long id) {
        InterviewSchedule schedule = interviewScheduleMapper.selectById(id);
        if (schedule == null) throw new BizException("面试记录不存在");
        ResumeInfo resume = resumeInfoMapper.selectById(schedule.getResumeId());
        if (resume == null) throw new BizException("简历不存在");

        schedule.setNoticeSent(1);
        interviewScheduleMapper.updateById(schedule);

        SysNotice notice = new SysNotice();
        notice.setReceiverType("CANDIDATE");
        notice.setReceiverId(resume.getCandidateId());
        notice.setTitle("面试通知");
        notice.setContent("您的面试安排时间: " + schedule.getInterviewTime() + "，请准时参加。");
        notice.setReadFlag(0);
        sysNoticeMapper.insert(notice);
        return ApiResult.ok();
    }

    @PostMapping("/{id}/start")
    public ApiResult<?> start(@PathVariable Long id) {
        InterviewSchedule schedule = new InterviewSchedule();
        schedule.setId(id);
        schedule.setInterviewStatus(InterviewStatusEnum.IN_PROGRESS.name());
        interviewScheduleMapper.updateById(schedule);
        return ApiResult.ok();
    }

    @PostMapping("/{id}/finish")
    public ApiResult<?> finish(@PathVariable Long id, @RequestBody @Valid InterviewFinishReq req) {
        InterviewSchedule schedule = interviewScheduleMapper.selectById(id);
        if (schedule == null) throw new BizException("面试记录不存在");
        ResumeInfo resume = resumeInfoMapper.selectById(schedule.getResumeId());
        if (resume == null) throw new BizException("简历不存在");

        boolean pass = req.getPass() == 1;
        schedule.setInterviewStatus(pass ? InterviewStatusEnum.PASS.name() : InterviewStatusEnum.REJECT.name());
        interviewScheduleMapper.updateById(schedule);

        InterviewEvaluation evaluation = new InterviewEvaluation();
        evaluation.setInterviewId(id);
        evaluation.setEvaluatorId(SecurityUtil.currentUserId());
        evaluation.setProfessionalScore(req.getProfessionalScore() == null ? 0 : req.getProfessionalScore());
        evaluation.setCommunicationScore(req.getCommunicationScore() == null ? 0 : req.getCommunicationScore());
        evaluation.setAttitudeScore(req.getAttitudeScore() == null ? 0 : req.getAttitudeScore());
        int total = evaluation.getProfessionalScore() + evaluation.getCommunicationScore() + evaluation.getAttitudeScore();
        evaluation.setTotalScore(total);
        evaluation.setEvaluateResult(pass ? 1 : 2);
        evaluation.setComments(req.getComments());
        interviewEvaluationMapper.insert(evaluation);

        ResumeInfo updateResume = new ResumeInfo();
        updateResume.setId(resume.getId());
        
        JobPost job = jobPostMapper.selectById(resume.getJobId());
        String jobName = job != null ? job.getTitle() : "未知岗位";
        SysNotice notice = new SysNotice();
        notice.setReceiverType("CANDIDATE");
        notice.setReceiverId(resume.getCandidateId());
        notice.setReadFlag(0);

        if (pass) {
            updateResume.setResumeStatus(ResumeStatusEnum.INTERVIEW_PASS.code());
            onboardingRecordMapper.insert(buildOnboarding(resume.getId()));
            
            notice.setTitle("面试通过通知");
            notice.setContent("恭喜，您投递的【" + jobName + "】岗位已通过面试！请留意后续入职安排。");
        } else {
            updateResume.setResumeStatus(ResumeStatusEnum.INTERVIEW_REJECT.code());
            
            notice.setTitle("面试未通过");
            notice.setContent("抱歉，您投递的【" + jobName + "】岗位未能通过面试。");
        }
        sysNoticeMapper.insert(notice);
        
        resumeInfoMapper.updateById(updateResume);
        return ApiResult.ok();
    }

    private OnboardingRecord buildOnboarding(Long resumeId) {
        OnboardingRecord r = new OnboardingRecord();
        r.setResumeId(resumeId);
        r.setHrId(SecurityUtil.currentUserId());
        r.setDocsVerified(0);
        r.setOnboardStatus(OnboardStatusEnum.PENDING_DOCS.name());
        return r;
    }

    @GetMapping("/page")
    public ApiResult<?> page(@RequestParam int pageNo, @RequestParam int pageSize, @RequestParam(required = false) String interviewStatus) {
        LambdaQueryWrapper<InterviewSchedule> qw = new LambdaQueryWrapper<InterviewSchedule>()
                .eq(interviewStatus != null && !interviewStatus.isBlank(), InterviewSchedule::getInterviewStatus, interviewStatus)
                .orderByDesc(InterviewSchedule::getCreateTime);
        return ApiResult.ok(interviewScheduleMapper.selectList(qw));
    }
}
