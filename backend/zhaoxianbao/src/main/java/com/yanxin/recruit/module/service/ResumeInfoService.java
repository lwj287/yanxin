package com.yanxin.recruit.module.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yanxin.recruit.module.entity.ResumeInfo;

public interface ResumeInfoService extends IService<ResumeInfo> {
    IPage<ResumeInfo> pageQuery(int pageNo, int pageSize, Long jobId, Integer screeningResult, Integer resumeStatus, String keyword);
    void smartScreen(Long resumeId);
}
