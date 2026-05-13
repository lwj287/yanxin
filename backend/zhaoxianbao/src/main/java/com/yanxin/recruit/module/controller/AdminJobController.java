package com.yanxin.recruit.module.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.yanxin.recruit.common.api.ApiResult;
import com.yanxin.recruit.common.exception.BizException;
import com.yanxin.recruit.common.security.SecurityUtil;
import com.yanxin.recruit.module.entity.JobPost;
import com.yanxin.recruit.module.service.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/job")
@RequiredArgsConstructor
public class AdminJobController {
    private final JobPostService jobPostService;

    @PostMapping
    public ApiResult<?> add(@RequestBody JobPost job) {
        checkUniqueByTitleAndArea(job, null);
        job.setPublishStatus(0);
        job.setRecruiterId(SecurityUtil.currentUserId());
        jobPostService.save(job);
        return ApiResult.ok();
    }

    @PutMapping("/{id}")
    public ApiResult<?> edit(@PathVariable Long id, @RequestBody JobPost job) {
        checkUniqueByTitleAndArea(job, id);
        job.setId(id);
        jobPostService.updateById(job);
        return ApiResult.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResult<?> delete(@PathVariable Long id) {
        jobPostService.removeById(id);
        return ApiResult.ok();
    }

    @PutMapping("/{id}/publish")
    public ApiResult<?> publish(@PathVariable Long id, @RequestParam Integer publishStatus) {
        JobPost job = new JobPost();
        job.setId(id);
        job.setPublishStatus(publishStatus);
        jobPostService.updateById(job);
        return ApiResult.ok();
    }

    @GetMapping("/page")
    public ApiResult<IPage<JobPost>> page(@RequestParam int pageNo, @RequestParam int pageSize,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) String cityCode,
                                           @RequestParam(required = false) String educationCode,
                                           @RequestParam(required = false) Integer age,
                                           @RequestParam(required = false) Integer expYears,
                                           @RequestParam(required = false) String skillCode,
                                           @RequestParam(required = false) Integer publishStatus) {
        return ApiResult.ok(jobPostService.pageQuery(pageNo, pageSize, keyword, cityCode, educationCode, age, expYears, skillCode, publishStatus));
    }

    private void checkUniqueByTitleAndArea(JobPost job, Long currentId) {
        if (job.getTitle() == null || job.getTitle().isBlank()) throw new BizException("岗位名称不能为空");
        if (job.getCityCode() == null || job.getCityCode().isBlank()) throw new BizException("城市不能为空");
        if (job.getAreaCode() == null || job.getAreaCode().isBlank()) throw new BizException("区域不能为空");
        String title = job.getTitle().trim();
        LambdaQueryWrapper<JobPost> qw = new LambdaQueryWrapper<JobPost>()
                .eq(JobPost::getTitle, title)
                .eq(JobPost::getCityCode, job.getCityCode())
                .eq(JobPost::getAreaCode, job.getAreaCode());
        if (currentId != null) {
            qw.ne(JobPost::getId, currentId);
        }
        if (jobPostService.count(qw) > 0) {
            throw new BizException("同一地区已存在相同岗位名称，请勿重复创建");
        }
        job.setTitle(title);
    }
}
