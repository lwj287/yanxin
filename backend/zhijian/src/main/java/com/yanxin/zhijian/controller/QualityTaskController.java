package com.yanxin.zhijian.controller;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanxin.zhijian.common.Result;
import com.yanxin.zhijian.entity.QualityTask;
import com.yanxin.zhijian.service.IQualityTaskService;
import com.yanxin.zhijian.entity.QualityImageRecord;
import com.yanxin.zhijian.mapper.QualityImageRecordMapper;
import com.yanxin.zhijian.service.YoloInferenceService;
import com.yanxin.zhijian.service.AsyncImageProcessService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController @RequestMapping("/zhijian/task")
public class QualityTaskController {
    @Autowired private IQualityTaskService taskService;
    @Autowired private QualityImageRecordMapper imageRecordMapper;
    @Autowired private AsyncImageProcessService asyncImageProcessService;
    
    @GetMapping("/list") 
    public Result<?> list(QualityTask query) { 
        QueryWrapper<QualityTask> wrapper = new QueryWrapper<>();
        if (query.getStaffName() != null && !query.getStaffName().isEmpty()) {
            wrapper.like("staff_name", query.getStaffName());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            wrapper.eq("status", query.getStatus());
        }
        wrapper.orderByDesc("create_time");
        List<QualityTask> tasks = taskService.list(wrapper);
        for (QualityTask task : tasks) {
            QueryWrapper<QualityImageRecord> imgWrapper = new QueryWrapper<>();
            imgWrapper.eq("task_id", task.getId());
            task.setImageRecords(imageRecordMapper.selectList(imgWrapper));
        }
        return Result.success(tasks); 
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id) {
        QualityTask task = taskService.getById(id);
        if (task != null) {
            QueryWrapper<QualityImageRecord> imgWrapper = new QueryWrapper<>();
            imgWrapper.eq("task_id", task.getId());
            task.setImageRecords(imageRecordMapper.selectList(imgWrapper));
            return Result.success(task);
        }
        return Result.error("任务不存在");
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody QualityTask task) {
        if (task.getId() == null) {
            return Result.error("ID不能为空");
        }
        task.setUpdateTime(LocalDateTime.now());
        return Result.success(taskService.updateById(task));
    }

    @PostMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return Result.success(taskService.removeById(id));
    }

    @PostMapping("/delete-image/{id}")
    public Result<?> deleteImage(@PathVariable Long id) {
        return Result.success(imageRecordMapper.deleteById(id));
    }

    /**
     * 客户端上传图片，异步调用 AI 推理
     */
    @PostMapping("/upload-image")
    public Result<?> uploadImage(
            @RequestParam("taskId") Long taskId,
            @RequestParam("file") MultipartFile file) {
        
        try {
            // 1. 真实保存图片文件到本地绝对路径
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") ? 
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String newFilename = java.util.UUID.randomUUID().toString().replace("-", "") + extension;
            
            // 按日期划分文件夹 (yyyyMMdd)，防止单目录下文件过多，且不至于太细碎
            String datePath = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            // 获取项目运行的当前目录，并指向 uploads 文件夹 (需要处理可能的子模块路径问题)
            String projectPath = System.getProperty("user.dir");
            if (projectPath.endsWith("backend")) {
                projectPath = projectPath + java.io.File.separator + "zhijian";
            }
            
            // 构建最终的上传目录路径: projectPath/uploads/yyyyMMdd
            String uploadDirPath = projectPath + java.io.File.separator + "uploads" + java.io.File.separator + datePath;
            java.io.File uploadDir = new java.io.File(uploadDirPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 使用更安全的绝对路径传输方式
            java.io.File dest = new java.io.File(uploadDir.getAbsolutePath() + java.io.File.separator + newFilename);
            file.transferTo(dest);

            // 构造可访问的图片 URL
            String imageUrl = "http://localhost:8085/uploads/" + datePath + "/" + newFilename;
            
            // 2. 先保存初始状态到 quality_image_record 表
            QualityImageRecord record = new QualityImageRecord();
            record.setTaskId(taskId);
            record.setFileName(originalFilename);
            record.setImageUrl(imageUrl);
            record.setFileSize(file.getSize());
            record.setAiStatus("UNCHECKED"); // 标记为未检测/检测中
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());
            
            imageRecordMapper.insert(record);

            // 3. 异步触发 YOLO 模型推理和更新
            asyncImageProcessService.processImageAsync(record.getId(), taskId, dest.getAbsolutePath());

            // 4. 立即返回成功，不阻塞前端
            return Result.success(record);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("图像上传失败：" + e.getMessage());
        }
    }
}