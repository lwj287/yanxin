package com.yanxin.training.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.training.common.Result;
import com.yanxin.training.entity.Course;
import com.yanxin.training.service.CourseService;
import com.yanxin.training.mapper.CourseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @cn.dev33.satoken.annotation.SaIgnore
    @GetMapping("/page")
    public Result<Page<Course>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String categoryId) {
        
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(title)) {
            wrapper.like(Course::getCourseName, title);
        }
        if (StringUtils.hasText(categoryId)) {
            wrapper.eq(Course::getCourseType, categoryId);
        }
        // 按创建时间倒序
        wrapper.orderByDesc(Course::getCreateTime);

        Page<Course> pageInfo = courseService.page(new Page<>(current, size), wrapper);
        
        CourseMapper mapper = (CourseMapper) courseService.getBaseMapper();
        for (Course c : pageInfo.getRecords()) {
            c.setEnrolled(mapper.getEnrolledCount(c.getCourseId()));
        }
        
        return Result.success(pageInfo);
    }

    @GetMapping("/{id}/enrollment")
    public Result<List<Map<String, Object>>> getCourseEnrollments(@PathVariable String id) {
        CourseMapper mapper = (CourseMapper) courseService.getBaseMapper();
        return Result.success(mapper.getCourseEnrollments(id));
    }

    @GetMapping("/{id}")
    public Result<Course> getById(@PathVariable String id) {
        Course course = courseService.getById(id);
        return Result.success(course);
    }

    @PostMapping
    public Result<Boolean> save(@RequestBody Course course) {
        return Result.success(courseService.save(course));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody Course course) {
        return Result.success(courseService.updateById(course));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable String id) {
        return Result.success(courseService.removeById(id));
    }
}
