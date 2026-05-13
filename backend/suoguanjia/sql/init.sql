CREATE DATABASE IF NOT EXISTS `yanxin_suoguanjia` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `yanxin_suoguanjia`;

-- 1. 设备基础信息表
DROP TABLE IF EXISTS `device_info`;
CREATE TABLE `device_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_sn` varchar(64) NOT NULL COMMENT '设备序列号(SN)，唯一标识',
  `device_name` varchar(128) NOT NULL COMMENT '设备名称(如：南门智能锁)',
  `device_type` tinyint(4) NOT NULL COMMENT '设备类型: 1-智能门锁, 2-工具箱, 3-清洁设备',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态: 0-离线, 1-在线, 2-故障',
  `battery_level` int(11) DEFAULT '100' COMMENT '电量百分比 0-100',
  `location` varchar(255) DEFAULT NULL COMMENT '安装位置或归属地',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_sn` (`device_sn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备基础信息表';

-- 2. 设备人员授权关联表 (解决设备与服务人员的绑定)
DROP TABLE IF EXISTS `device_user_rel`;
CREATE TABLE `device_user_rel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_id` bigint(20) NOT NULL COMMENT '设备ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户/家政员ID',
  `user_name` varchar(64) NOT NULL COMMENT '冗余用户姓名',
  `auth_type` tinyint(4) NOT NULL COMMENT '授权类型: 1-永久授权, 2-临时授权(如单次开门)',
  `valid_start_time` datetime DEFAULT NULL COMMENT '授权生效时间',
  `valid_end_time` datetime DEFAULT NULL COMMENT '授权失效时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备人员授权关联表';

-- 3. 设备操作与状态日志表 (统一记录心跳上报和下发指令)
DROP TABLE IF EXISTS `device_log`;
CREATE TABLE `device_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `device_id` bigint(20) NOT NULL COMMENT '设备ID',
  `log_type` tinyint(4) NOT NULL COMMENT '日志类型: 1-状态上报, 2-远程指令, 3-设备报警',
  `action` varchar(64) NOT NULL COMMENT '具体动作: unlock, lock, battery_report, fault_alert',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID(指令下发者,如果是设备上报则为空)',
  `content` json DEFAULT NULL COMMENT '详细内容(JSON格式,如电量、温湿度、指令参数)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备操作与状态日志表';

-- 4. 维修工单表 (解决故障自动派单)
DROP TABLE IF EXISTS `maintenance_ticket`;
CREATE TABLE `maintenance_ticket` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ticket_sn` varchar(64) NOT NULL COMMENT '工单编号',
  `device_id` bigint(20) NOT NULL COMMENT '故障设备ID',
  `reporter_id` bigint(20) DEFAULT NULL COMMENT '报修人ID(可为空,设备自上报)',
  `fault_desc` varchar(500) NOT NULL COMMENT '故障描述',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态: 0-待处理, 1-维修中, 2-已解决',
  `resolve_desc` varchar(500) DEFAULT NULL COMMENT '处理结果描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ticket_sn` (`ticket_sn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备维修工单表';