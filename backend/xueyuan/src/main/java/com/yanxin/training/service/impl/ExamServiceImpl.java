package com.yanxin.training.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanxin.training.entity.Exam;
import com.yanxin.training.mapper.ExamMapper;
import com.yanxin.training.service.ExamService;
import org.springframework.stereotype.Service;

@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {
}