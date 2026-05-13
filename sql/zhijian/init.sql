-- ----------------------------
-- 燕鑫慧质检系统 (zhijian) 数据库建表脚本 (纯图像版)
-- ----------------------------

CREATE DATABASE IF NOT EXISTS `yanxin_zhijian` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `yanxin_zhijian`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 质检模板表 (Quality Template)
-- 定义不同服务类型需要拍摄哪些照片。
-- ----------------------------
DROP TABLE IF EXISTS `quality_template`;
CREATE TABLE `quality_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `service_type` varchar(50) NOT NULL COMMENT '服务类型 (如: 日常保洁, 深度保洁, 家电清洗)',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `photo_requirements` json NOT NULL COMMENT '拍照要求 (JSON格式: [{"name":"客厅全景","desc":"需包含地面和沙发","required":true}, ...])',
  `description` varchar(255) DEFAULT NULL COMMENT '模板描述',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 (0:停用, 1:启用)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除 (0:未删除, 1:已删除)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='质检模板表';

-- ----------------------------
-- 2. 质检任务表 (Quality Task)
-- 关联实际的业务订单，记录整体质检的状态和结果。
-- ----------------------------
DROP TABLE IF EXISTS `quality_task`;
CREATE TABLE `quality_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '关联业务订单ID',
  `service_type` varchar(50) NOT NULL COMMENT '服务类型',
  `staff_id` bigint NOT NULL COMMENT '服务人员ID',
  `staff_name` varchar(50) NOT NULL COMMENT '服务人员姓名',
  `template_id` bigint NOT NULL COMMENT '关联的质检模板ID',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '质检状态 (PENDING:待上传, UPLOADED:已上传待审, PASS:合格, REJECT:不合格)',
  `ai_score` decimal(5,2) DEFAULT NULL COMMENT 'AI智能评分 (0-100)',
  `inspector_id` bigint DEFAULT NULL COMMENT '人工审核员ID',
  `inspector_name` varchar(50) DEFAULT NULL COMMENT '人工审核员姓名',
  `manual_score` decimal(5,2) DEFAULT NULL COMMENT '人工评分 (0-100)',
  `final_result` varchar(20) DEFAULT NULL COMMENT '最终结论 (PASS/REJECT)',
  `remark` varchar(500) DEFAULT NULL COMMENT '审核备注/整改意见',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
  `submit_time` datetime DEFAULT NULL COMMENT '服务人员提交图片时间',
  `audit_time` datetime DEFAULT NULL COMMENT '审核完成时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_staff_id` (`staff_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='质检任务表';

-- ----------------------------
-- 3. 质检图片记录表 (Quality Image Record)
-- 存储服务人员根据模板要求上传的具体照片，以及对单张照片的AI分析结果。
-- ----------------------------
DROP TABLE IF EXISTS `quality_image_record`;
CREATE TABLE `quality_image_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID (文件ID)',
  `task_id` bigint NOT NULL COMMENT '关联质检任务ID',
  `file_name` varchar(255) NOT NULL COMMENT '上传文件名',
  `image_url` varchar(500) NOT NULL COMMENT '上传的图片URL',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小(字节)',
  `ai_status` varchar(20) DEFAULT 'UNCHECKED' COMMENT '该图AI识别状态 (UNCHECKED, PASSED, FAILED)',
  `ai_reason` varchar(50) DEFAULT NULL COMMENT 'AI识别不通过原因枚举 (如: STUBBORN_STAIN, CLUTTER)',
  `ai_detect_result` json DEFAULT NULL COMMENT 'AI完整识别结果JSON',
  `ai_score` decimal(5,2) DEFAULT NULL COMMENT 'AI评分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='质检图片及AI识别结果记录表';

-- ----------------------------
-- 4. 质检申诉表 (Quality Appeal)
-- 如果家政人员对质检不合格结果有异议，可以发起申诉。
-- ----------------------------
DROP TABLE IF EXISTS `quality_appeal`;
CREATE TABLE `quality_appeal` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint NOT NULL COMMENT '关联质检任务ID',
  `staff_id` bigint NOT NULL COMMENT '申诉员工ID',
  `reason` varchar(500) NOT NULL COMMENT '申诉理由',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '处理状态 (PENDING:待处理, APPROVED:申诉通过, REJECTED:申诉驳回)',
  `handler_id` bigint DEFAULT NULL COMMENT '处理人ID',
  `handler_name` varchar(50) DEFAULT NULL COMMENT '处理人姓名',
  `handle_result` varchar(500) DEFAULT NULL COMMENT '处理结果说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申诉时间',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='质检申诉表';

-- ----------------------------
-- 5. 服务类型表 (Service Type)
-- 定义系统支持的各项服务类型。
-- ----------------------------
DROP TABLE IF EXISTS `service_type`;
CREATE TABLE `service_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '服务类型名称 (如: 日常保洁, 深度保洁, 家电清洗)',
  `description` varchar(255) DEFAULT NULL COMMENT '服务描述',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 (0:停用, 1:启用)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='服务类型表';

INSERT INTO `service_type` (`name`, `description`, `status`) VALUES
('日常保洁', '提供基础的家庭清洁服务', 1),
('深度保洁', '提供全方位、深度的家庭清洁服务', 1),
('家电清洗', '提供各类家电的专业清洗服务', 1);

SET FOREIGN_KEY_CHECKS = 1;
