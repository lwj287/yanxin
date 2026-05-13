USE yanxin_zhipaidan;
SET NAMES utf8mb4;

-- 清空旧数据以便重复执行
TRUNCATE TABLE dispatch_record;
TRUNCATE TABLE service_order;
TRUNCATE TABLE staff_schedule_status;

-- 插入测试家政员状态数据 (分布在北京各个区域)
INSERT INTO staff_schedule_status (staff_id, staff_name, staff_phone, skills, current_status, last_longitude, last_latitude, location_update_time) VALUES 
(1001, '张阿姨', '13800138001', '["日常保洁", "深度保洁", "做饭"]', 0, 116.397428, 39.909230, NOW()), -- 天安门附近
(1002, '李月嫂', '13800138002', '["月嫂", "育儿嫂"]', 0, 116.481289, 39.993072, NOW()), -- 望京附近
(1003, '王师傅', '13800138003', '["家电维修", "管道疏通"]', 0, 116.321356, 39.894356, NOW()), -- 北京西站附近
(1004, '赵大姐', '13800138004', '["日常保洁", "收纳整理"]', 0, 116.454987, 39.933924, NOW()), -- 三里屯附近
(1005, '刘哥', '13800138005', '["搬家服务", "重物搬运"]', 0, 116.313082, 40.038439, NOW()), -- 中关村软件园附近
(1006, '陈阿姨', '13800138006', '["养老陪护", "做饭"]', 1, 116.411648, 39.914271, NOW()), -- 王府井附近 (服务中)
(1007, '孙大姐', '13800138007', '["日常保洁", "擦玻璃"]', 0, 116.373210, 39.910798, NOW()), -- 西单附近
(1008, '周师傅', '13800138008', '["家电维修", "电路维修"]', 0, 116.395645, 39.983976, NOW()), -- 鸟巢附近
(1009, '吴阿姨', '13800138009', '["育儿嫂", "做饭"]', 1, 116.326756, 40.003304, NOW()), -- 清华大学附近 (服务中)
(1010, '郑大姐', '13800138010', '["日常保洁", "开荒保洁"]', 0, 116.378950, 39.865181, NOW()); -- 北京南站附近

-- 插入测试订单数据 (包含待派单和已派单)
INSERT INTO service_order (order_no, customer_name, customer_phone, service_address, longitude, latitude, service_type, expect_start_time, expect_end_time, status) VALUES 
-- 待派单 (status = 0)
('ORD202605130001', '赵先生', '13900139001', '北京市朝阳区望京SOHO T1', 116.481050, 39.996794, '日常保洁', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 3 HOUR, 0),
('ORD202605130002', '钱女士', '13900139002', '北京市海淀区中关村创业大街', 116.316167, 39.982361, '育儿嫂', DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY) + INTERVAL 8 HOUR, 0),
('ORD202605130004', '林女士', '13900139004', '北京市东城区银泰in88', 116.411648, 39.914271, '收纳整理', DATE_ADD(NOW(), INTERVAL 4 HOUR), DATE_ADD(NOW(), INTERVAL 7 HOUR), 0),
('ORD202605130005', '郭先生', '13900139005', '北京市海淀区五棵松华熙LIVE', 116.281816, 39.910798, '家电维修', DATE_ADD(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 3 HOUR), 0),
('ORD202605130006', '何女士', '13900139006', '北京市朝阳区大悦城', 116.518698, 39.924248, '日常保洁', DATE_ADD(NOW(), INTERVAL 5 HOUR), DATE_ADD(NOW(), INTERVAL 7 HOUR), 0),

-- 已派单 (status = 1)
('ORD202605130003', '孙先生', '13900139003', '北京市丰台区丽泽SOHO', 116.324546, 39.871030, '家电维修', DATE_ADD(NOW(), INTERVAL 3 HOUR), DATE_ADD(NOW(), INTERVAL 5 HOUR), 1),
('ORD202605130007', '高先生', '13900139007', '北京市东城区北京站', 116.427287, 39.903668, '搬家服务', DATE_ADD(NOW(), INTERVAL -1 HOUR), DATE_ADD(NOW(), INTERVAL 2 HOUR), 1),
('ORD202605130008', '刘女士', '13900139008', '北京市朝阳区三里屯太古里', 116.454987, 39.933924, '日常保洁', DATE_ADD(NOW(), INTERVAL 10 HOUR), DATE_ADD(NOW(), INTERVAL 12 HOUR), 1);

-- 插入测试派单记录
INSERT INTO dispatch_record (order_id, order_no, staff_id, staff_name, plan_start_time, plan_end_time, dispatch_type, estimated_distance, status) VALUES 
-- 关联 ORD202605130003
(6, 'ORD202605130003', 1003, '王师傅', DATE_ADD(NOW(), INTERVAL 3 HOUR), DATE_ADD(NOW(), INTERVAL 5 HOUR), 0, 3500, 1),
-- 关联 ORD202605130007
(7, 'ORD202605130007', 1005, '刘哥', DATE_ADD(NOW(), INTERVAL -1 HOUR), DATE_ADD(NOW(), INTERVAL 2 HOUR), 0, 12000, 1),
-- 关联 ORD202605130008
(8, 'ORD202605130008', 1004, '赵大姐', DATE_ADD(NOW(), INTERVAL 10 HOUR), DATE_ADD(NOW(), INTERVAL 12 HOUR), 0, 2000, 1);
