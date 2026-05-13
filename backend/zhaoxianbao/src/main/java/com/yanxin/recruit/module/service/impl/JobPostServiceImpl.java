package com.yanxin.recruit.module.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.recruit.module.entity.JobPost;
import com.yanxin.recruit.module.mapper.JobPostMapper;
import com.yanxin.recruit.module.service.JobPostService;
import org.springframework.stereotype.Service;

@Service
public class JobPostServiceImpl extends ServiceImpl<JobPostMapper, JobPost> implements JobPostService {
    @Override
    public IPage<JobPost> pageQuery(int pageNo, int pageSize, String keyword, String cityCode, String educationCode,
                                    Integer age, Integer expYears, String skillCode, Integer publishStatus) {
        LambdaQueryWrapper<JobPost> qw = new LambdaQueryWrapper<JobPost>()
                .like(StrUtil.isNotBlank(keyword), JobPost::getTitle, keyword)
                .eq(StrUtil.isNotBlank(cityCode), JobPost::getCityCode, cityCode)
                .eq(StrUtil.isNotBlank(educationCode), JobPost::getEducationCode, educationCode)
                .le(age != null, JobPost::getMinAge, age)
                .le(expYears != null, JobPost::getMinExperienceYears, expYears)
                .like(StrUtil.isNotBlank(skillCode), JobPost::getRequiredSkills, skillCode)
                .eq(publishStatus != null, JobPost::getPublishStatus, publishStatus)
                .orderByDesc(JobPost::getCreateTime);
        return this.page(new Page<>(pageNo, pageSize), qw);
    }
}
