package com.yanxin.training.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.training.common.Result;
import com.yanxin.training.entity.Exam;
import com.yanxin.training.service.ExamService;
import com.yanxin.training.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;
    private final CourseService courseService;

    @GetMapping("/page")
    public Result<Page<Exam>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status) {
        
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(title)) {
            wrapper.like(Exam::getExamName, title);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Exam::getCourseId, status);
        }
        // 按创建时间倒序
        wrapper.orderByDesc(Exam::getCreateTime);

        Page<Exam> pageInfo = examService.page(new Page<>(current, size), wrapper);
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}/records")
    public Result<List<Map<String, Object>>> getExamRecords(@PathVariable String id) {
        com.yanxin.training.mapper.ExamMapper mapper = (com.yanxin.training.mapper.ExamMapper) examService.getBaseMapper();
        return Result.success(mapper.getExamRecords(id));
    }

    @GetMapping("/{id}")
    public Result<Exam> getById(@PathVariable String id) {
        return Result.success(examService.getById(id));
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Exam exam) {
        return Result.success(examService.save(exam));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Exam exam) {
        return Result.success(examService.updateById(exam));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable String id) {
        return Result.success(examService.removeById(id));
    }
}