-- ----------------------------
-- 燕鑫慧质检系统 (zhijian) 测试数据
-- ----------------------------

USE `yanxin_zhijian`;

-- 1. 插入质检模板数据
INSERT INTO `quality_template` (`id`, `service_type`, `template_name`, `photo_requirements`, `description`, `status`) VALUES
(1, '日常保洁', '2小时日常保洁基础版', '[{"name":"客厅全景","desc":"需包含地面和主要家具","required":true}, {"name":"厨房台面","desc":"无油污，物品摆放整齐","required":true}, {"name":"卫生间洗手台","desc":"镜面无水渍，台面无杂物","required":true}]', '适用于普通家庭2-3小时日常保洁', 1),
(2, '深度保洁', '新居开荒深度保洁', '[{"name":"门窗滑轨细节","desc":"滑轨无灰尘颗粒","required":true}, {"name":"厨房油烟机内侧","desc":"无明显油渍残留","required":true}, {"name":"卫生间马桶死角","desc":"底座及后侧无污垢","required":true}, {"name":"全屋地面全景","desc":"无装修垃圾和脚印","required":true}]', '适用于新房装修后的全面深度清洁', 1),
(3, '家电清洗', '空调单洗', '[{"name":"清洗前过滤网","desc":"展示清洗前灰尘情况","required":true}, {"name":"清洗后过滤网","desc":"展示清洗后干净程度","required":true}, {"name":"清洗后外壳","desc":"外壳擦拭干净，无水痕","required":true}, {"name":"试机运行温度显示","desc":"证明机器运转正常","required":true}]', '适用于壁挂式/柜式空调清洗', 1);

-- 2. 插入质检任务数据
INSERT INTO `quality_task` (`id`, `order_id`, `service_type`, `staff_id`, `staff_name`, `template_id`, `status`, `ai_score`, `inspector_id`, `inspector_name`, `manual_score`, `final_result`, `remark`, `submit_time`, `audit_time`) VALUES
(1001, 88001, '日常保洁', 201, '张阿姨', 1, 'PASS', NULL, 10, '王主管', 95.00, 'PASS', '照片清晰，清洁达标。', '2026-05-11 10:30:00', '2026-05-11 11:00:00'),
(1002, 88002, '深度保洁', 202, '李师傅', 2, 'REJECT', NULL, 10, '王主管', 60.00, 'REJECT', '厨房油烟机内侧仍有明显油污，需返工。', '2026-05-11 14:00:00', '2026-05-11 14:15:00'),
(1003, 88003, '家电清洗', 203, '赵师傅', 3, 'UPLOADED', NULL, NULL, NULL, NULL, NULL, NULL, '2026-05-11 15:00:00', NULL),
(1004, 88004, '日常保洁', 204, '孙阿姨', 1, 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- 3. 插入质检图片记录数据
INSERT INTO `quality_image_record` (`task_id`, `file_name`, `image_url`, `ai_status`, `ai_reason`, `ai_score`) VALUES
(1001, '1', 'http://localhost:8085/uploads/20260512/2.jpg', NULL, NULL, NULL),
(1002, '1', 'http://localhost:8085/uploads/20260512/2.jpg', NULL, NULL, NULL),
(1003, '1', 'http://localhost:8085/uploads/20260512/2.jpg', NULL, NULL, NULL);

-- 4. 插入质检申诉数据
INSERT INTO `quality_appeal` (`id`, `task_id`, `staff_id`, `reason`, `status`, `handler_id`, `handler_name`, `handle_result`, `create_time`) VALUES
(1, 1002, 202, '油烟机型号老旧，顽固污渍无法完全清除，已提前和客户沟通。', 'PENDING', NULL, NULL, NULL, '2026-05-11 14:30:00');
