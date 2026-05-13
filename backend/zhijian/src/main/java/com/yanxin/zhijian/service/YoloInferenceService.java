package com.yanxin.zhijian.service;

import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class YoloInferenceService {

    private ZooModel<Image, DetectedObjects> model;
    private Predictor<Image, DetectedObjects> predictor;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 初始化模型
    // 手动实现针对 YOLOv8 自定义类别张量解析的 Translator
    private static class CustomYoloV8Translator implements Translator<Image, DetectedObjects> {
        private final float threshold;
        private final List<String> classes;

        public CustomYoloV8Translator(float threshold, List<String> classes) {
            this.threshold = threshold;
            this.classes = classes;
        }

        @Override
        public ai.djl.translate.Batchifier getBatchifier() {
            // 返回 null 禁用默认的 StackBatchifier，因为 DJL ONNXRuntime 后端不支持 stack 操作
            // 我们在 processInput 中直接返回带 batch 维度的张量 [1, 3, 640, 640]
            return null;
        }

        @Override
        public NDList processInput(TranslatorContext ctx, Image input) {
            // Letterbox 预处理缩放至 640x640，使用原生 Java 2D 绘制以确保正确的 padding
            NDManager manager = ctx.getNDManager();
            int width = input.getWidth();
            int height = input.getHeight();
            
            ctx.setAttachment("width", width);
            ctx.setAttachment("height", height);
            
            float scale = Math.min(640f / width, 640f / height);
            ctx.setAttachment("scale", scale);
            
            int newW = Math.round(width * scale);
            int newH = Math.round(height * scale);
            
            float padW = (640f - newW) / 2.0f;
            float padH = (640f - newH) / 2.0f;
            ctx.setAttachment("padW", padW);
            ctx.setAttachment("padH", padH);

            // 获取原始 BufferedImage
            java.awt.image.BufferedImage src = (java.awt.image.BufferedImage) input.getWrappedImage();
            
            // 创建 640x640 画布，填充灰色(114,114,114)背景
            java.awt.image.BufferedImage letterbox = new java.awt.image.BufferedImage(640, 640, java.awt.image.BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = letterbox.createGraphics();
            g2d.setColor(new java.awt.Color(114, 114, 114));
            g2d.fillRect(0, 0, 640, 640);
            
            // 将缩放后的原图居中绘制上去
            g2d.drawImage(src.getScaledInstance(newW, newH, java.awt.Image.SCALE_SMOOTH), (int) padW, (int) padH, null);
            g2d.dispose();

            // 将处理好的 Letterbox 图片转为 NDArray
            Image paddedImage = ImageFactory.getInstance().fromImage(letterbox);
            NDArray array = paddedImage.toNDArray(manager, Image.Flag.COLOR);
            
            // 将 Byte 转换为 Float32
            array = array.toType(DataType.FLOAT32, false);
            
            // 手动执行 HWC -> CHW，因为 DJL 的 ONNXRuntime 后端不支持 transpose(2, 0, 1)
            // 同时也在这里手动除以 255.0f 进行归一化
            float[] hwcData = array.toFloatArray();
            float[] chwData = new float[hwcData.length];
            int channels = 3;
            int h = 640;
            int w = 640;
            for (int c = 0; c < channels; c++) {
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        chwData[c * h * w + y * w + x] = hwcData[y * w * channels + x * channels + c] / 255.0f;
                    }
                }
            }
            
            // 创建 [1, 3, 640, 640] 的张量
            NDArray chwArray = manager.create(chwData, new Shape(1, channels, h, w));
            return new NDList(chwArray);
        }

        @Override
        public DetectedObjects processOutput(TranslatorContext ctx, NDList list) {
            // YOLOv8 输出 shape: [1, 4+classes, 8400]
            NDArray output = list.singletonOrThrow(); // [1, 4+classes, 8400]
            // 打印张量形状以供调试
            System.out.println("Output Tensor Shape: " + output.getShape());
            
            int width = (int) ctx.getAttachment("width");
            int height = (int) ctx.getAttachment("height");

            List<String> retClassNames = new ArrayList<>();
            List<Double> retProbs = new ArrayList<>();
            List<BoundingBox> retBoxes = new ArrayList<>();

            float[] data = output.toFloatArray();
            // 注意：此时矩阵形状为 [1, cols, numAnchors]
            int cols = (int) output.getShape().get(1); // 4 + classes
            int numAnchors = (int) output.getShape().get(2); // 8400
            int numClasses = cols - 4;

            System.out.println("Parsed numAnchors: " + numAnchors + ", cols: " + cols + ", dynamic numClasses: " + numClasses);

            int countAboveThreshold = 0;

            for (int i = 0; i < numAnchors; i++) {
                // 找最大类别置信度
                float maxConf = 0;
                int classId = -1;
                for (int c = 0; c < numClasses; c++) {
                    // 读取类别置信度，它在第 (4+c) 行，第 i 列
                    float conf = data[(4 + c) * numAnchors + i];
                    if (conf > maxConf) {
                        maxConf = conf;
                        classId = c;
                    }
                }

                if (maxConf >= threshold) {
                    countAboveThreshold++;
                    // cx, cy, w, h 在 0, 1, 2, 3 行的第 i 列
                    float cx = data[0 * numAnchors + i];
                    float cy = data[1 * numAnchors + i];
                    float w  = data[2 * numAnchors + i];
                    float h  = data[3 * numAnchors + i];

                    float padW = (float) ctx.getAttachment("padW");
                    float padH = (float) ctx.getAttachment("padH");
                    float scale = (float) ctx.getAttachment("scale");

                    // 还原到原始图片的绝对坐标，先减去 pad，再除以缩放比例 (逆向 Letterbox)
                    float x1 = (cx - w / 2.0f - padW) / scale;
                    float y1 = (cy - h / 2.0f - padH) / scale;
                    float x2 = (cx + w / 2.0f - padW) / scale;
                    float y2 = (cy + h / 2.0f - padH) / scale;

                    // 转换为 DJL Rectangle 需要的相对比例 0~1，这里除以的应该是真实的图片 width 和 height
                    float rx = x1 / width;
                    float ry = y1 / height;
                    float rw = (x2 - x1) / width;
                    float rh = (y2 - y1) / height;

                    // 在转换为相对比例后，直接在这里进行 0~1 的边界限制，而不是限制绝对坐标
                    // 这样即使逆向还原超出边界，前端渲染时也不会溢出图片
                    // 注意这里的 x,y 不能小于0
                    rx = Math.max(0f, rx);
                    ry = Math.max(0f, ry);
                    
                    // 修正宽高，防止加上 x,y 后超出 1.0
                    rw = Math.max(0f, Math.min(1.0f - rx, rw));
                    rh = Math.max(0f, Math.min(1.0f - ry, rh));

                    String className = classId < classes.size() ? classes.get(classId) : "unknown_" + classId;
                    retClassNames.add(className);
                    retProbs.add((double) maxConf);
                    retBoxes.add(new Rectangle(rx, ry, rw, rh));
                }
            }

            System.out.println("Boxes above threshold before NMS: " + countAboveThreshold);

            // 进行 NMS (非极大值抑制)
            List<String> nmsClassNames = new ArrayList<>();
            List<Double> nmsProbs = new ArrayList<>();
            List<BoundingBox> nmsBoxes = new ArrayList<>();

            float nmsThreshold = 0.45f;
            boolean[] merged = new boolean[retBoxes.size()];

            // 按照置信度降序排序
            List<Integer> indices = new ArrayList<>();
            for (int i = 0; i < retBoxes.size(); i++) indices.add(i);
            indices.sort((i, j) -> Double.compare(retProbs.get(j), retProbs.get(i)));

            for (int index : indices) {
                if (merged[index]) continue;
                
                nmsClassNames.add(retClassNames.get(index));
                nmsProbs.add(retProbs.get(index));
                nmsBoxes.add(retBoxes.get(index));
                merged[index] = true;
                
                Rectangle box1 = (Rectangle) retBoxes.get(index);
                for (int j : indices) {
                    if (merged[j]) continue;
                    if (!retClassNames.get(index).equals(retClassNames.get(j))) continue;
                    
                    Rectangle box2 = (Rectangle) retBoxes.get(j);
                    if (computeIoU(box1, box2) > nmsThreshold) {
                        merged[j] = true;
                    }
                }
            }

            System.out.println("Boxes after NMS: " + nmsBoxes.size());

            return new DetectedObjects(nmsClassNames, nmsProbs, nmsBoxes);
        }

        private float computeIoU(Rectangle box1, Rectangle box2) {
            double x1 = Math.max(box1.getX(), box2.getX());
            double y1 = Math.max(box1.getY(), box2.getY());
            double x2 = Math.min(box1.getX() + box1.getWidth(), box2.getX() + box2.getWidth());
            double y2 = Math.min(box1.getY() + box1.getHeight(), box2.getY() + box2.getHeight());

            double w = Math.max(0, x2 - x1);
            double h = Math.max(0, y2 - y1);
            double interArea = w * h;

            double area1 = box1.getWidth() * box1.getHeight();
            double area2 = box2.getWidth() * box2.getHeight();

            return (float) (interArea / (area1 + area2 - interArea));
        }
    }

    @PostConstruct
    public void initModel() {
        try {
            log.info("开始加载 YOLOv8 ONNX 模型...");
            // 获取 resource 目录下的模型路径
            URL modelUrl = getClass().getResource("/models/best.onnx");
            if (modelUrl == null) {
                log.error("未找到模型文件: /models/best.onnx，尝试使用 yolov8s.onnx");
                modelUrl = getClass().getResource("/models/yolov8s.onnx");
                if (modelUrl == null) {
                    log.error("未找到模型文件: /models/yolov8s.onnx，尝试使用 yolov8n.onnx");
                    modelUrl = getClass().getResource("/models/yolov8n.onnx");
                }
            }

            // 初始化模型
            // 我们手动定义水下数据集到家政业务的类别映射
            // 假设水下常见类别有：holothurian(海参), echinus(海胆), scallop(扇贝), starfish(海星)
            List<String> classes = new ArrayList<>();
            classes.add("顽固污渍"); // 0: holothurian -> 顽固污渍
            classes.add("杂物乱放"); // 1: echinus -> 杂物乱放
            classes.add("摆放不齐"); // 2: scallop -> 摆放不齐
            classes.add("明显水渍"); // 3: starfish -> 明显水渍

            // 使用我们手动编写的原生 Translator 来完美适配 [1, 4+classes, 8400] 张量
            Translator<Image, DetectedObjects> translator = new CustomYoloV8Translator(0.25f, classes);

            // 设置 Criteria (模型加载标准)
            Criteria<Image, DetectedObjects> criteria = Criteria.builder()
                    .setTypes(Image.class, DetectedObjects.class)
                    .optModelUrls(modelUrl.toString())
                    .optEngine("OnnxRuntime") // 使用 ONNX 引擎
                    .optTranslator(translator)
                    .build();

            // 为了支持自定义类别名，我们需要手动构建 Translator 并覆盖
            // 但最简单的方式是在生成结果时进行名称映射
            model = criteria.loadModel();
            predictor = model.newPredictor();
            log.info("YOLOv8 模型加载成功!");

        } catch (IOException | ModelException e) {
            log.error("模型加载失败", e);
        }
    }

    /**
     * 执行推理并返回 JSON 格式的检测结果
     */
    public String predictImage(InputStream imageStream) {
        if (predictor == null) {
            log.error("预测器未初始化！");
            return "[]";
        }

        try {
            // 读取图片
            Image img = ImageFactory.getInstance().fromInputStream(imageStream);
            int originalWidth = img.getWidth();
            int originalHeight = img.getHeight();

            // 推理
            DetectedObjects detection = predictor.predict(img);
            List<Map<String, Object>> resultList = new ArrayList<>();

            // 解析结果
            for (int i = 0; i < detection.getNumberOfObjects(); i++) {
                DetectedObjects.DetectedObject obj = detection.item(i);
                BoundingBox box = obj.getBoundingBox();
                Rectangle rect = box.getBounds();

                // 使用相对坐标 (0.0 ~ 1.0)，方便前端直接通过 CSS 的 % 进行自适应定位渲染
                double xmin = rect.getX();
                double ymin = rect.getY();
                double xmax = rect.getX() + rect.getWidth();
                double ymax = rect.getY() + rect.getHeight();

                Map<String, Object> boxData = new HashMap<>();
                boxData.put("label", mapClassName(obj.getClassName()));
                boxData.put("confidence", obj.getProbability());
                boxData.put("box", new double[]{xmin, ymin, xmax, ymax});

                log.info("Detected {}: confidence={}, box=[{}, {}, {}, {}]", 
                        obj.getClassName(), obj.getProbability(), xmin, ymin, xmax, ymax);

                resultList.add(boxData);
            }

            return objectMapper.writeValueAsString(resultList);

        } catch (IOException | TranslateException e) {
            log.error("图片推理失败", e);
            return "[]";
        }
    }

    /**
     * 将模型原生的类别名（如果未提供synset.txt，DJL默认输出 class_0, class_1）映射为业务含义
     */
    private String mapClassName(String className) {
        // 如果已经是我们自定义的中文名称，直接返回
        if ("顽固污渍".equals(className) || "杂物乱放".equals(className) || 
            "摆放不齐".equals(className) || "明显水渍".equals(className)) {
            return className;
        }

        // DJL 默认会输出 class_0, class_1...
        if ("class_0".equals(className)) return "顽固污渍";
        if ("class_1".equals(className)) return "杂物乱放";
        if ("class_2".equals(className)) return "摆放不齐";
        if ("class_3".equals(className)) return "明显水渍";
        
        // 如果您的模型类别名本身就是 holothurian，也可以直接判断
        if ("holothurian".equalsIgnoreCase(className)) return "顽固污渍";
        if ("echinus".equalsIgnoreCase(className)) return "杂物乱放";
        if ("scallop".equalsIgnoreCase(className)) return "摆放不齐";
        if ("starfish".equalsIgnoreCase(className)) return "明显水渍";
        
        return "异常污点";
    }

    @PreDestroy
    public void close() {
        if (predictor != null) predictor.close();
        if (model != null) model.close();
    }
}
