package com.yanxin.recruit.module.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yanxin.recruit.common.api.ApiResult;
import com.yanxin.recruit.common.enums.ResumeStatusEnum;
import com.yanxin.recruit.common.exception.BizException;
import com.yanxin.recruit.module.entity.JobPost;
import com.yanxin.recruit.module.entity.ResumeInfo;
import com.yanxin.recruit.module.entity.SysNotice;
import com.yanxin.recruit.module.mapper.JobPostMapper;
import com.yanxin.recruit.module.mapper.SysNoticeMapper;
import com.yanxin.recruit.module.service.ResumeInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/resume")
@RequiredArgsConstructor
public class AdminResumeController {
    private final ResumeInfoService resumeInfoService;
    private final JobPostMapper jobPostMapper;
    private final SysNoticeMapper sysNoticeMapper;

    @GetMapping("/page")
    public ApiResult<IPage<ResumeInfo>> page(@RequestParam int pageNo, @RequestParam int pageSize,
                                             @RequestParam(required = false) Long jobId,
                                             @RequestParam(required = false) Integer screeningResult,
                                             @RequestParam(required = false) Integer resumeStatus,
                                             @RequestParam(required = false) String keyword) {
        return ApiResult.ok(resumeInfoService.pageQuery(pageNo, pageSize, jobId, screeningResult, resumeStatus, keyword));
    }

    @GetMapping("/interview-options")
    public ApiResult<?> interviewOptions(@RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<ResumeInfo> qw = new LambdaQueryWrapper<ResumeInfo>()
                .eq(ResumeInfo::getScreeningResult, 1)
                .eq(ResumeInfo::getResumeStatus, ResumeStatusEnum.QUALIFIED.code())
                .orderByDesc(ResumeInfo::getCreateTime)
                .last("limit 300");
        List<ResumeInfo> resumes = resumeInfoService.list(qw);
        if (resumes.isEmpty()) return ApiResult.ok(List.of());

        List<Long> jobIds = resumes.stream().map(ResumeInfo::getJobId).distinct().toList();
        Map<Long, String> jobTitleMap = jobPostMapper.selectBatchIds(jobIds).stream()
                .collect(Collectors.toMap(JobPost::getId, JobPost::getTitle, (a, b) -> a));

        String kw = keyword == null ? "" : keyword.trim();
        List<Map<String, Object>> options = resumes.stream()
                .map(r -> {
                    String title = jobTitleMap.getOrDefault(r.getJobId(), "未知岗位");
                    String label = r.getId() + " - " + r.getRealName() + " / " + title + " / " + r.getPhone();
                    return Map.<String, Object>of(
                            "resumeId", r.getId(),
                            "realName", r.getRealName(),
                            "phone", r.getPhone(),
                            "jobTitle", title,
                            "label", label
                    );
                })
                .filter(item -> kw.isBlank()
                        || String.valueOf(item.get("label")).contains(kw)
                        || String.valueOf(item.get("realName")).contains(kw)
                        || String.valueOf(item.get("phone")).contains(kw))
                .toList();
        return ApiResult.ok(options);
    }

    @PostMapping
    public ApiResult<?> add(@RequestBody ResumeInfo resume) {
        if (resume.getJobId() == null) throw new BizException("岗位ID不能为空");
        if (resume.getRealName() == null || resume.getRealName().isBlank()) throw new BizException("姓名不能为空");
        if (resume.getPhone() == null || resume.getPhone().isBlank()) throw new BizException("手机号不能为空");
        if (resume.getCandidateId() == null) resume.setCandidateId(0L);
        if (resume.getScreeningResult() == null) resume.setScreeningResult(0);
        if (resume.getScreeningScore() == null) resume.setScreeningScore(0);
        if (resume.getResumeStatus() == null) {
            resume.setResumeStatus(ResumeStatusEnum.SUBMITTED.code());
        }
        resumeInfoService.save(resume);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    public ApiResult<?> edit(@PathVariable Long id, @RequestBody ResumeInfo resume) {
        ResumeInfo old = resumeInfoService.getById(id);
        if (old == null) throw new BizException("简历不存在");
        resume.setId(id);
        resumeInfoService.updateById(resume);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResult<?> delete(@PathVariable Long id) {
        ResumeInfo old = resumeInfoService.getById(id);
        if (old == null) throw new BizException("简历不存在");
        resumeInfoService.removeById(id);
        return ApiResult.ok();
    }

    @PostMapping("/{id}/smart-screen")
    public ApiResult<?> smartScreen(@PathVariable Long id) {
        resumeInfoService.smartScreen(id);
        return ApiResult.ok();
    }

    @PutMapping("/{id}/screen")
    public ApiResult<?> manualScreen(@PathVariable Long id, @RequestParam Integer pass, @RequestParam(required = false) String reason) {
        ResumeInfo old = resumeInfoService.getById(id);
        if (old == null) throw new BizException("简历不存在");

        ResumeInfo resume = new ResumeInfo();
        resume.setId(id);
        resume.setScreeningResult(pass == 1 ? 1 : 2);
        resume.setScreeningReason(reason);
        resume.setResumeStatus(pass == 1 ? ResumeStatusEnum.MANUAL_SCREEN_PASS.code() : ResumeStatusEnum.MANUAL_SCREEN_REJECT.code());
        resumeInfoService.updateById(resume);

        if (old.getCandidateId() != null && old.getCandidateId() > 0) {
            JobPost job = jobPostMapper.selectById(old.getJobId());
            String jobName = job != null ? job.getTitle() : "未知岗位";
            SysNotice notice = new SysNotice();
            notice.setReceiverType("CANDIDATE");
            notice.setReceiverId(old.getCandidateId());
            if (pass == 1) {
                notice.setTitle("简历筛选通过");
                notice.setContent("您投递的【" + jobName + "】岗位已通过人工筛选，请留意后续面试通知。");
            } else {
                notice.setTitle("简历筛选未通过");
                notice.setContent("抱歉，您投递的【" + jobName + "】岗位未能通过人工筛选。" + (reason != null && !reason.isBlank() ? "原因：" + reason : ""));
            }
            notice.setReadFlag(0);
            sysNoticeMapper.insert(notice);
        }

        return ApiResult.ok();
    }
}
