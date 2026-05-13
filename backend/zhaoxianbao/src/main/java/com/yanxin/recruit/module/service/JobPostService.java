package com.yanxin.recruit.module.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanxin.recruit.module.entity.JobPost;

public interface JobPostService extends IService<JobPost> {
    IPage<JobPost> pageQuery(int pageNo, int pageSize, String keyword, String cityCode, String educationCode,
                             Integer age, Integer expYears, String skillCode, Integer publishStatus);
}
