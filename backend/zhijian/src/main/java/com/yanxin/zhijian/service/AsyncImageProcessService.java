package com.yanxin.zhijian.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanxin.zhijian.entity.QualityImageRecord;
import com.yanxin.zhijian.entity.QualityTask;
import com.yanxin.zhijian.mapper.QualityImageRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AsyncImageProcessService {

    @Autowired
    private YoloInferenceService yoloInferenceService;

    @Autowired
    private QualityImageRecordMapper imageRecordMapper;

    @Autowired
    private IQualityTaskService taskService;

    @Async
    public void processImageAsync(Long recordId, Long taskId, String absoluteFilePath) {
        log.info("开始异步处理图片，recordId: {}, filePath: {}", recordId, absoluteFilePath);
        try {
            File file = new File(absoluteFilePath);
            if (!file.exists()) {
                log.error("文件不存在，无法进行AI检测: {}", absoluteFilePath);
                return;
            }

            // 1. 调用 YOLO 模型推理
            String detectResultJson;
            try (InputStream inputStream = new FileInputStream(file)) {
                detectResultJson = yoloInferenceService.predictImage(inputStream);
            }

            // 2. 解析 JSON 获取 AI 识别原因
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> boxes = mapper.readValue(detectResultJson, new TypeReference<List<Map<String, Object>>>() {});

            int boxCount = boxes.size();
            // 每发现一个缺陷扣 10 分
            BigDecimal score = new BigDecimal(Math.max(0, 100 - (boxCount * 10)));
            // 分数低于 80 分判定为不合格，80 分及以上判定为合格（容忍 1-2 个小缺陷）
            String aiStatus = score.compareTo(new BigDecimal(80)) < 0 ? "FAILED" : "PASSED";
            String aiReason = null;
            if (boxCount > 0) {
                String label = (String) boxes.get(0).get("label");
                if ("顽固污渍".equals(label) || "杂物乱放".equals(label) || "摆放不齐".equals(label) || "明显水渍".equals(label)) {
                    aiReason = label;
                } else {
                    aiReason = "顽固污渍";
                }
            }

            // 3. 更新图片记录表
            QualityImageRecord record = imageRecordMapper.selectById(recordId);
            if (record != null) {
                record.setAiStatus(aiStatus);
                record.setAiReason(aiReason);
                record.setAiDetectResult(detectResultJson);
                record.setAiScore(score);
                record.setUpdateTime(LocalDateTime.now());
                imageRecordMapper.updateById(record);
            }

            // 4. 更新任务表总分
            updateTaskAiScore(taskId);

            log.info("异步处理图片完成，recordId: {}", recordId);
        } catch (Exception e) {
            log.error("异步处理图片异常，recordId: {}", recordId, e);
            // 可以选择将状态更新为 FAILED 并写入错误原因
            QualityImageRecord record = imageRecordMapper.selectById(recordId);
            if (record != null) {
                record.setAiStatus("FAILED");
                record.setAiReason("AI检测出错");
                record.setUpdateTime(LocalDateTime.now());
                imageRecordMapper.updateById(record);
            }
        }
    }

    private void updateTaskAiScore(Long taskId) {
        QueryWrapper<QualityImageRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("task_id", taskId);
        List<QualityImageRecord> records = imageRecordMapper.selectList(wrapper);

        if (records != null && !records.isEmpty()) {
            BigDecimal total = BigDecimal.ZERO;
            int count = 0;
            for (QualityImageRecord r : records) {
                if (r.getAiScore() != null) {
                    total = total.add(r.getAiScore());
                    count++;
                }
            }
            if (count > 0) {
                BigDecimal avg = total.divide(new BigDecimal(count), 2, java.math.RoundingMode.HALF_UP);
                QualityTask task = taskService.getById(taskId);
                if (task != null) {
                    task.setAiScore(avg);
                    task.setUpdateTime(LocalDateTime.now());
                    taskService.updateById(task);
                }
            }
        }
    }
}
