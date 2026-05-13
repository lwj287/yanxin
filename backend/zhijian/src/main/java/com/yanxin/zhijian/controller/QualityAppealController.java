package com.yanxin.zhijian.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanxin.zhijian.common.Result;
import com.yanxin.zhijian.entity.QualityAppeal;
import com.yanxin.zhijian.entity.QualityTask;
import com.yanxin.zhijian.service.IQualityAppealService;
import com.yanxin.zhijian.service.IQualityTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController @RequestMapping("/zhijian/appeal")
public class QualityAppealController {
    @Autowired private IQualityAppealService appealService;
    @Autowired private IQualityTaskService taskService;
    
    @GetMapping("/list") 
    public Result<?> list(QualityAppeal query, @RequestParam(required = false) String staffName) { 
        QueryWrapper<QualityAppeal> wrapper = new QueryWrapper<>();
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq("status", query.getStatus());
        }
        wrapper.orderByDesc("create_time");
        List<QualityAppeal> list = appealService.list(wrapper);
        
        // 手动关联查询员工姓名并过滤
        java.util.Iterator<QualityAppeal> iterator = list.iterator();
        while (iterator.hasNext()) {
            QualityAppeal appeal = iterator.next();
            if (appeal.getTaskId() != null) {
                QualityTask task = taskService.getById(appeal.getTaskId());
                if (task != null) {
                    appeal.setStaffName(task.getStaffName());
                }
            }
            // 如果传了员工姓名参数，且不匹配，则移除
            if (staffName != null && !staffName.isEmpty()) {
                if (appeal.getStaffName() == null || !appeal.getStaffName().contains(staffName)) {
                    iterator.remove();
                }
            }
        }
        
        return Result.success(list); 
    }

    /**
     * 更新申诉信息 (编辑)
     */
    @PostMapping("/update")
    public Result<?> update(@RequestBody QualityAppeal appeal) {
        if (appeal.getId() == null) {
            return Result.error("ID不能为空");
        }
        appeal.setUpdateTime(LocalDateTime.now());
        return Result.success(appealService.updateById(appeal));
    }

    /**
     * 逻辑删除申诉
     */
    @PostMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return Result.success(appealService.removeById(id));
    }

    /**
     * 处理申诉
     */
    @PostMapping("/handle")
    public Result<?> handleAppeal(@RequestBody QualityAppeal appealRequest) {
        if (appealRequest.getId() == null || appealRequest.getStatus() == null) {
            return Result.success("参数不完整");
        }
        
        QualityAppeal existAppeal = appealService.getById(appealRequest.getId());
        if (existAppeal == null) {
            return Result.success("申诉单不存在");
        }

        // 记录旧状态，用于判断是否需要回滚质检任务状态
        String oldStatus = existAppeal.getStatus();
        String newStatus = appealRequest.getStatus();

        // 更新申诉状态和处理意见
        existAppeal.setStatus(newStatus); // APPROVED 或 REJECTED
        existAppeal.setHandleResult(appealRequest.getHandleResult()); // 真正的处理意见字段
        existAppeal.setHandlerId(1L); // 模拟当前登录管理员ID
        existAppeal.setHandlerName("系统管理员"); // 模拟当前登录管理员姓名
        existAppeal.setHandleTime(LocalDateTime.now()); // 记录处理时间
        existAppeal.setUpdateTime(LocalDateTime.now());
        
        appealService.updateById(existAppeal);

        // 处理质检任务的联动逻辑
        if (existAppeal.getTaskId() != null) {
            QualityTask task = taskService.getById(existAppeal.getTaskId());
            if (task != null) {
                // 如果是新通过申诉，或者原来是驳回现在改判通过 -> 质检任务改为 PASS
                if ("APPROVED".equals(newStatus)) {
                    task.setStatus("PASS");
                    task.setUpdateTime(LocalDateTime.now());
                    taskService.updateById(task);
                } 
                // 如果原来是通过（撤销了扣款），现在又反悔改成了驳回 -> 质检任务必须改回 REJECTED，继续扣款
                else if ("APPROVED".equals(oldStatus) && "REJECTED".equals(newStatus)) {
                    task.setStatus("REJECTED");
                    task.setUpdateTime(LocalDateTime.now());
                    taskService.updateById(task);
                }
            }
        }

        return Result.success(true);
    }
}