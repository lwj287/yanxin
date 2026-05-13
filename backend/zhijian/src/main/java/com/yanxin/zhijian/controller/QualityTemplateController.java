package com.yanxin.zhijian.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanxin.zhijian.common.Result;
import com.yanxin.zhijian.entity.QualityTemplate;
import com.yanxin.zhijian.service.IQualityTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController @RequestMapping("/zhijian/template")
public class QualityTemplateController {
    @Autowired private IQualityTemplateService templateService;
    
    @GetMapping("/list") 
    public Result<?> list(QualityTemplate query) { 
        try {
            QueryWrapper<QualityTemplate> wrapper = new QueryWrapper<>();
            if (query.getTemplateName() != null && !query.getTemplateName().isEmpty()) {
                wrapper.like("template_name", query.getTemplateName());
            }
            if (query.getServiceType() != null && !query.getServiceType().isEmpty()) {
                wrapper.eq("service_type", query.getServiceType());
            }
            if (query.getStatus() != null) {
                wrapper.eq("status", query.getStatus());
            }
            wrapper.orderByDesc("create_time");
            return Result.success(templateService.list(wrapper)); 
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @PostMapping("/save")
    public Result<?> save(@RequestBody QualityTemplate template) {
        if (template.getCreateTime() == null) {
            template.setCreateTime(LocalDateTime.now());
        }
        template.setUpdateTime(LocalDateTime.now());
        return Result.success(templateService.save(template));
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody QualityTemplate template) {
        if (template.getId() == null) {
            return Result.error("ID不能为空");
        }
        template.setUpdateTime(LocalDateTime.now());
        return Result.success(templateService.updateById(template));
    }

    @PostMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return Result.success(templateService.removeById(id));
    }

    @PostMapping("/status/{id}")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        QualityTemplate template = templateService.getById(id);
        if (template == null) {
            return Result.success("模板不存在");
        }
        template.setStatus(status);
        template.setUpdateTime(LocalDateTime.now());
        return Result.success(templateService.updateById(template));
    }
}