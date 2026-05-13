package com.yanxin.recruit.module.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.recruit.common.enums.ResumeStatusEnum;
import com.yanxin.recruit.common.exception.BizException;
import com.yanxin.recruit.module.entity.JobPost;
import com.yanxin.recruit.module.entity.ResumeInfo;
import com.yanxin.recruit.module.entity.SysNotice;
import com.yanxin.recruit.module.mapper.JobPostMapper;
import com.yanxin.recruit.module.mapper.ResumeInfoMapper;
import com.yanxin.recruit.module.mapper.SysNoticeMapper;
import com.yanxin.recruit.module.service.ResumeInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeInfoServiceImpl extends ServiceImpl<ResumeInfoMapper, ResumeInfo> implements ResumeInfoService {
    private final JobPostMapper jobPostMapper;
    private final SysNoticeMapper sysNoticeMapper;

    @Override
    public IPage<ResumeInfo> pageQuery(int pageNo, int pageSize, Long jobId, Integer screeningResult, Integer resumeStatus, String keyword) {
        LambdaQueryWrapper<ResumeInfo> qw = new LambdaQueryWrapper<ResumeInfo>()
                .eq(jobId != null, ResumeInfo::getJobId, jobId)
                .eq(screeningResult != null, ResumeInfo::getScreeningResult, screeningResult)
                .eq(resumeStatus != null, ResumeInfo::getResumeStatus, resumeStatus)
                .and(StrUtil.isNotBlank(keyword), i -> i.like(ResumeInfo::getRealName, keyword).or().like(ResumeInfo::getPhone, keyword))
                .orderByDesc(ResumeInfo::getCreateTime);
        return this.page(new Page<>(pageNo, pageSize), qw);
    }

    @Override
    public void smartScreen(Long resumeId) {
        ResumeInfo resume = this.getById(resumeId);
        if (resume == null) {
            throw new BizException("简历不存在");
        }
        JobPost job = jobPostMapper.selectById(resume.getJobId());
        if (job == null) {
            throw new BizException("岗位不存在");
        }
        int score = 0;
        if (resume.getAge() != null && job.getMinAge() != null && resume.getAge() >= job.getMinAge()) score += 20;
        if (resume.getAge() != null && job.getMaxAge() != null && resume.getAge() <= job.getMaxAge()) score += 20;
        if (resume.getExperienceYears() != null && job.getMinExperienceYears() != null && resume.getExperienceYears() >= job.getMinExperienceYears()) score += 30;
        if (StrUtil.isNotBlank(resume.getEducationCode())
                && StrUtil.equals(StrUtil.trim(resume.getEducationCode()), StrUtil.trim(job.getEducationCode()))) {
            score += 10;
        }
        if (StrUtil.isNotBlank(resume.getSkills()) && StrUtil.isNotBlank(job.getRequiredSkills())) {
            List<String> rs = Arrays.stream(resume.getSkills().split("[,，、\\s]+"))
                    .map(StrUtil::trim)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList());
            List<String> js = Arrays.stream(job.getRequiredSkills().split("[,，、\\s]+"))
                    .map(StrUtil::trim)
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList());
            long hit = rs.stream().filter(js::contains).count();
            score += (int) Math.min(20, hit * 10);
        }
        resume.setScreeningScore(score);
        if (score >= 50) {
            resume.setScreeningResult(1);
            resume.setScreeningReason("系统筛选通过");
            resume.setResumeStatus(ResumeStatusEnum.SMART_SCREEN_PASS.code());

            SysNotice notice = new SysNotice();
            notice.setReceiverType("CANDIDATE");
            notice.setReceiverId(resume.getCandidateId());
            notice.setTitle("初筛通过通知");
            notice.setContent("您投递的【" + job.getTitle() + "】岗位已通过系统初筛，请留意后续面试通知。");
            notice.setReadFlag(0);
            sysNoticeMapper.insert(notice);
        } else {
            resume.setScreeningResult(2);
            resume.setScreeningReason("经验或技能匹配不足");
            resume.setResumeStatus(ResumeStatusEnum.SMART_SCREEN_REJECT.code());

            SysNotice notice = new SysNotice();
            notice.setReceiverType("CANDIDATE");
            notice.setReceiverId(resume.getCandidateId());
            notice.setTitle("初筛未通过");
            notice.setContent("抱歉，您投递的【" + job.getTitle() + "】岗位未能通过系统初筛。");
            notice.setReadFlag(0);
            sysNoticeMapper.insert(notice);
        }
        this.updateById(resume);
    }
}
