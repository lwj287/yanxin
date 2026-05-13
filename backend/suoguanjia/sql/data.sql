USE `yanxin_suoguanjia`;

-- 清空旧数据 (方便重复执行)
TRUNCATE TABLE `device_info`;
TRUNCATE TABLE `device_user_rel`;
TRUNCATE TABLE `device_log`;
TRUNCATE TABLE `maintenance_ticket`;

-- 1. 插入设备基础信息 (device_type: 1-智能门锁, 2-工具箱, 3-清洁设备; status: 0-离线, 1-在线, 2-故障)
INSERT INTO `device_info` (`id`, `device_sn`, `device_name`, `device_type`, `status`, `battery_level`, `location`) VALUES
(1, 'LOCK-A-001', 'A区南门智能锁', 1, 1, 85, 'A区南门'),
(2, 'LOCK-B-002', 'B区东门智能锁', 1, 0, 15, 'B区东门'),
(3, 'BOX-C-001', '保洁工具箱-C区', 2, 1, 90, 'C区物业站'),
(4, 'CLEAN-D-001', '智能扫地机器人-D区', 3, 2, 40, 'D区大堂'),
(5, 'LOCK-A-002', 'A区北门智能锁', 1, 1, 72, 'A区北门'),
(6, 'LOCK-C-001', 'C区正门智能锁', 1, 1, 95, 'C区正门'),
(7, 'BOX-A-001', '维修工具箱-A区', 2, 1, 100, 'A区地下室配电房'),
(8, 'CLEAN-A-001', '智能洗地机-A区', 3, 1, 60, 'A区地下车库'),
(9, 'LOCK-D-001', 'D区VIP通道智能锁', 1, 2, 8, 'D区VIP通道'),
(10, 'BOX-B-001', '园林工具箱-B区', 2, 0, 0, 'B区绿化带旁');

-- 2. 插入设备人员授权关联信息 (auth_type: 1-永久授权, 2-临时授权)
INSERT INTO `device_user_rel` (`id`, `device_id`, `user_id`, `user_name`, `auth_type`, `valid_start_time`, `valid_end_time`) VALUES
(1, 1, 101, '张阿姨(保洁)', 1, '2026-01-01 00:00:00', '2099-12-31 23:59:59'),
(2, 1, 102, '李师傅(维修)', 2, '2026-05-13 14:00:00', '2026-05-13 18:00:00'),
(3, 3, 101, '张阿姨(保洁)', 1, '2026-01-01 00:00:00', '2099-12-31 23:59:59'),
(4, 2, 103, '王大爷(安保)', 1, '2026-01-01 00:00:00', '2099-12-31 23:59:59'),
(5, 5, 101, '张阿姨(保洁)', 1, '2026-01-01 00:00:00', '2099-12-31 23:59:59'),
(6, 6, 104, '赵经理(物业)', 1, '2026-01-01 00:00:00', '2099-12-31 23:59:59'),
(7, 7, 102, '李师傅(维修)', 1, '2026-01-01 00:00:00', '2099-12-31 23:59:59'),
(8, 9, 104, '赵经理(物业)', 1, '2026-01-01 00:00:00', '2099-12-31 23:59:59'),
(9, 10, 105, '孙师傅(绿化)', 1, '2026-01-01 00:00:00', '2099-12-31 23:59:59'),
(10, 1, 106, '临时工小陈', 2, '2026-05-13 08:00:00', '2026-05-13 12:00:00');

-- 3. 插入设备操作与状态日志 (log_type: 1-状态上报, 2-远程指令, 3-设备报警)
INSERT INTO `device_log` (`id`, `device_id`, `log_type`, `action`, `operator_id`, `content`) VALUES
(1, 1, 1, 'battery_report', NULL, '{"battery": 85, "signal": "strong"}'),
(2, 1, 2, 'unlock', 101, '{"method": "mini_app", "success": true}'),
(3, 2, 1, 'offline_alert', NULL, '{"last_battery": 15, "reason": "power_loss"}'),
(4, 3, 2, 'open_box', 101, '{"method": "scan_qrcode", "success": true}'),
(5, 4, 3, 'fault_alert', NULL, '{"error_code": "E002", "msg": "主刷头卡死"}'),
(6, 5, 1, 'battery_report', NULL, '{"battery": 72, "signal": "medium"}'),
(7, 6, 2, 'unlock', 104, '{"method": "remote_web", "success": true}'),
(8, 7, 2, 'open_box', 102, '{"method": "scan_qrcode", "success": true}'),
(9, 8, 2, 'start_clean', 101, '{"method": "mini_app", "mode": "deep_clean", "success": true}'),
(10, 9, 3, 'fault_alert', NULL, '{"error_code": "L005", "msg": "锁舌无法弹出"}'),
(11, 10, 1, 'offline_alert', NULL, '{"last_battery": 0, "reason": "power_loss"}'),
(12, 1, 2, 'unlock', 106, '{"method": "mini_app", "success": true}'),
(13, 8, 1, 'status_report', NULL, '{"battery": 60, "status": "cleaning", "water_level": "low"}');

-- 4. 插入维修工单 (status: 0-待处理, 1-维修中, 2-已解决)
INSERT INTO `maintenance_ticket` (`id`, `ticket_sn`, `device_id`, `reporter_id`, `fault_desc`, `status`, `resolve_desc`) VALUES
(1, 'TKT202605130001', 4, NULL, '设备自上报故障：主刷头卡死', 0, NULL),
(2, 'TKT202605130002', 2, 103, '东门智能锁没电离线，需要更换电池', 1, '已指派李师傅前往更换'),
(3, 'TKT202605100001', 3, 101, '工具箱C区门锁铰链损坏，无法锁闭', 2, '已更换新铰链，测试正常'),
(4, 'TKT202605130003', 9, NULL, '设备自上报故障：锁舌无法弹出，低电量警告(8%)', 0, NULL),
(5, 'TKT202605120001', 10, 105, '绿化工具箱电池彻底耗尽，无法开启', 1, '已携带移动电源前往应急开启并更换电池'),
(6, 'TKT202605110002', 8, 101, '洗地机清水箱漏水', 2, '已更换清水箱密封圈');
