USE yanxin_zhipaidan;

-- 插入测试家政员状态数据
INSERT INTO staff_schedule_status (staff_id, staff_name, staff_phone, skills, current_status, last_longitude, last_latitude, location_update_time) VALUES 
(1001, '张阿姨', '13800138001', '["日常保洁", "深度保洁"]', 0, 116.397428, 39.90923, NOW()),
(1002, '李月嫂', '13800138002', '["月嫂", "育儿嫂"]', 0, 116.481289, 39.993072, NOW()),
(1003, '王师傅', '13800138003', '["家电维修", "管道疏通"]', 0, 116.321356, 39.894356, NOW());

-- 插入测试订单数据
INSERT INTO service_order (order_no, customer_name, customer_phone, service_address, longitude, latitude, service_type, expect_start_time, expect_end_time, status) VALUES 
('ORD202605130001', '赵先生', '13900139001', '北京市朝阳区望京SOHO', 116.48105, 39.996794, '日常保洁', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 2 HOUR, 0),
('ORD202605130002', '钱女士', '13900139002', '北京市海淀区中关村', 116.316167, 39.982361, '育儿嫂', DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY) + INTERVAL 8 HOUR, 0),
('ORD202605130003', '孙先生', '13900139003', '北京市丰台区丽泽桥', 116.324546, 39.87103, '家电维修', DATE_ADD(NOW(), INTERVAL 3 HOUR), DATE_ADD(NOW(), INTERVAL 5 HOUR), 1);

-- 插入测试派单记录
-- 假设订单 ORD202605130003 已经派发给王师傅
INSERT INTO dispatch_record (order_id, order_no, staff_id, staff_name, plan_start_time, plan_end_time, dispatch_type, estimated_distance, status) VALUES 
(3, 'ORD202605130003', 1003, '王师傅', DATE_ADD(NOW(), INTERVAL 3 HOUR), DATE_ADD(NOW(), INTERVAL 5 HOUR), 0, 3500, 1);
