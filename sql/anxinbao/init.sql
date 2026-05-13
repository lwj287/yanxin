-- 燕鑫安心保 (Yanxin Anxinbao) 初始化脚本 (基于 E-R 图优化)
-- 优化项：补齐外键关系、增加常用索引、统一状态枚举注释

CREATE DATABASE IF NOT EXISTS `yanxin_anxinbao` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `yanxin_anxinbao`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 员工表 (Staff)
-- ----------------------------
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff` (
  `staff_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID(1-超级管理员 2-家政员)',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态(0-停用 1-启用)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`staff_id`),
  UNIQUE KEY `uk_staff_phone` (`phone`),
  KEY `idx_staff_role_status` (`role_id`, `status`),
  KEY `idx_staff_status_deleted` (`status`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- ----------------------------
-- 2. 实名认证表 (Realname Auth)
-- ----------------------------
DROP TABLE IF EXISTS `realname_auth`;
CREATE TABLE `realname_auth` (
  `auth_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '认证ID',
  `staff_id` bigint(20) NOT NULL COMMENT '员工ID',
  `staff_type` tinyint(4) NOT NULL COMMENT '员工类型(1-全职 2-兼职 3-临时)',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `face_material_url` varchar(255) DEFAULT NULL COMMENT '人脸识别素材地址',
  `auth_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '认证状态(0-待审核 1-已通过 2-已驳回)',
  `auth_time` datetime DEFAULT NULL COMMENT '认证时间',
  `auth_report` text COMMENT '认证报告',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`auth_id`),
  UNIQUE KEY `uk_realname_auth_id_card` (`id_card`),
  KEY `idx_realname_auth_staff` (`staff_id`),
  KEY `idx_realname_auth_status_deleted` (`auth_status`, `is_deleted`),
  CONSTRAINT `fk_realname_auth_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实名认证表';

-- ----------------------------
-- 3. 合同模板表 (Contract Template)
-- ----------------------------
DROP TABLE IF EXISTS `contract_template`;
CREATE TABLE `contract_template` (
  `template_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `contract_type` tinyint(4) NOT NULL COMMENT '合同类型(1-标准合同 2-短期合同 3-补充协议)',
  `template_content` longtext NOT NULL COMMENT '模板内容',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '模板状态(0-停用 1-启用)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`template_id`),
  KEY `idx_contract_template_type_status` (`contract_type`, `status`),
  KEY `idx_contract_template_status_deleted` (`status`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同模板表';

-- ----------------------------
-- 4. 电子合同表 (Contract)
-- ----------------------------
DROP TABLE IF EXISTS `contract`;
CREATE TABLE `contract` (
  `contract_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '合同ID',
  `staff_id` bigint(20) NOT NULL COMMENT '员工ID',
  `template_id` bigint(20) NOT NULL COMMENT '模板ID',
  `contract_content` longtext COMMENT '合同内容(动态生成后内容)',
  `sign_time` datetime DEFAULT NULL COMMENT '签署时间',
  `contract_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '合同状态(0-待签署 1-已生效 2-已过期 3-已作废)',
  `expire_time` datetime DEFAULT NULL COMMENT '到期时间',
  `storage_url` varchar(255) DEFAULT NULL COMMENT '合同存储地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`contract_id`),
  KEY `idx_contract_staff` (`staff_id`),
  KEY `idx_contract_template` (`template_id`),
  KEY `idx_contract_status_deleted` (`contract_status`, `is_deleted`),
  CONSTRAINT `fk_contract_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_contract_template` FOREIGN KEY (`template_id`) REFERENCES `contract_template` (`template_id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电子合同表';

-- ----------------------------
-- 5. 保险产品表 (Insurance Product)
-- ----------------------------
DROP TABLE IF EXISTS `insurance_product`;
CREATE TABLE `insurance_product` (
  `product_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '产品ID',
  `product_name` varchar(100) NOT NULL COMMENT '产品名称',
  `insurance_type` tinyint(4) NOT NULL COMMENT '保险类型(1-意外险 2-责任险 3-短期临时工险)',
  `coverage_amount` decimal(10,2) NOT NULL COMMENT '保险金额(元)',
  `premium` decimal(10,2) NOT NULL COMMENT '保费(元)',
  `duration_days` int(11) NOT NULL COMMENT '保险期限/天',
  `conditions` text COMMENT '投保条件',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态(0-下架 1-上架)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`product_id`),
  KEY `idx_insurance_product_type_status` (`insurance_type`, `status`),
  KEY `idx_insurance_product_status_deleted` (`status`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保险产品表';

-- ----------------------------
-- 6. 保险订单表 (Insurance Order)
-- ----------------------------
DROP TABLE IF EXISTS `insurance_order`;
CREATE TABLE `insurance_order` (
  `order_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `staff_id` bigint(20) NOT NULL COMMENT '员工ID',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `premium` decimal(10,2) NOT NULL COMMENT '保费(实付金额)',
  `insure_time` datetime NOT NULL COMMENT '投保时间',
  `expire_time` datetime NOT NULL COMMENT '到期时间',
  `claim_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '理赔状态(0-无理赔 1-理赔中 2-已理赔 3-已驳回)',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '保障状态(1-保障中 2-已过期 3-已退保)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`order_id`),
  KEY `idx_insurance_order_staff` (`staff_id`),
  KEY `idx_insurance_order_product` (`product_id`),
  KEY `idx_insurance_order_status_deleted` (`status`, `is_deleted`),
  KEY `idx_insurance_order_claim_status_deleted` (`claim_status`, `is_deleted`),
  CONSTRAINT `fk_insurance_order_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_insurance_order_product` FOREIGN KEY (`product_id`) REFERENCES `insurance_product` (`product_id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保险订单表';

-- ----------------------------
-- 7. 理赔管理表 (Claim)
-- ----------------------------
DROP TABLE IF EXISTS `claim`;
CREATE TABLE `claim` (
  `claim_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '理赔ID',
  `staff_id` bigint(20) NOT NULL COMMENT '员工ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `claim_amount` decimal(10,2) NOT NULL COMMENT '理赔金额(元)',
  `material_url` text COMMENT '理赔素材地址',
  `claim_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '理赔状态(0-待审核 1-处理中 2-已赔付 3-已驳回)',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `claim_time` datetime DEFAULT NULL COMMENT '理赔(结案)时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`claim_id`),
  KEY `idx_claim_staff` (`staff_id`),
  UNIQUE KEY `uk_claim_order` (`order_id`),
  KEY `idx_claim_status_deleted` (`claim_status`, `is_deleted`),
  CONSTRAINT `fk_claim_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_claim_order` FOREIGN KEY (`order_id`) REFERENCES `insurance_order` (`order_id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='理赔管理表';

-- 测试数据请参考同目录下的 data.sql 文件
SET FOREIGN_KEY_CHECKS = 1;
