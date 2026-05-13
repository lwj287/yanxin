-- ==========================================
-- 燕鑫客盈门系统初始化脚本 (MySQL)
-- ==========================================

CREATE DATABASE IF NOT EXISTS `yanxin_keyingmen` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `yanxin_keyingmen`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 会员表 (member)
-- ----------------------------
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member` (
  `member_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会员ID，主键',
  `member_name` varchar(64) NOT NULL COMMENT '会员姓名',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `member_level` tinyint(4) NOT NULL DEFAULT '0' COMMENT '会员等级：0-普通会员，1-高级会员',
  `account_balance` int(11) NOT NULL DEFAULT '0' COMMENT '账户余额（积分余额）',
  `member_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '会员状态：1-正常，0-禁用',
  `register_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `inviter_id` bigint(20) DEFAULT NULL COMMENT '邀请人会员ID',
  `invite_count` int(11) NOT NULL DEFAULT '0' COMMENT '邀请人数',
  -- 通用业务字段
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_inviter_id` (`inviter_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员表';

-- ----------------------------
-- 2. 优惠券模板表 (coupon_template)
-- ----------------------------
DROP TABLE IF EXISTS `coupon_template`;
CREATE TABLE `coupon_template` (
  `coupon_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '优惠券模板ID，主键',
  `coupon_name` varchar(128) NOT NULL COMMENT '优惠券名称',
  `face_value` decimal(10,2) NOT NULL COMMENT '面额',
  `condition_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '使用条件：满X元可用，0为无门槛',
  `valid_start_time` datetime NOT NULL COMMENT '生效时间',
  `valid_end_time` datetime NOT NULL COMMENT '失效时间',
  `issue_count` int(11) NOT NULL DEFAULT '0' COMMENT '发放数量',
  `used_count` int(11) NOT NULL DEFAULT '0' COMMENT '使用数量',
  -- 通用业务字段
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板表';

-- ----------------------------
-- 3. 营销活动模板表 (activity_template)
-- ----------------------------
DROP TABLE IF EXISTS `activity_template`;
CREATE TABLE `activity_template` (
  `activity_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '活动ID，主键',
  `coupon_id` bigint(20) NOT NULL COMMENT '关联优惠券模板ID',
  `activity_name` varchar(128) NOT NULL COMMENT '活动名称',
  `activity_type` tinyint(4) NOT NULL COMMENT '活动类型：1-赠送，2-抽奖',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `activity_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '活动状态：0-未开始，1-进行中，2-已结束',
  `issue_count` int(11) NOT NULL DEFAULT '0' COMMENT '优惠券发放数量',
  `redeem_count` int(11) NOT NULL DEFAULT '0' COMMENT '优惠券兑换数量',
  -- 通用业务字段
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`activity_id`),
  KEY `idx_coupon_id` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动模板表';

-- ----------------------------
-- 4. 会员活动参与表 (member_activity)
-- ----------------------------
DROP TABLE IF EXISTS `member_activity`;
CREATE TABLE `member_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` bigint(20) NOT NULL COMMENT '会员ID',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `participate_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '参与状态：0-失败，1-成功',
  `participate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '参与时间',
  `obtained_coupon_id` bigint(20) DEFAULT NULL COMMENT '获取优惠券(记录)ID',
  -- 通用业务字段
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_activity_id` (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员活动参与表';

-- ----------------------------
-- 5. 会员优惠券表 (member_coupon)
-- ----------------------------
DROP TABLE IF EXISTS `member_coupon`;
CREATE TABLE `member_coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `member_id` bigint(20) NOT NULL COMMENT '会员ID',
  `coupon_id` bigint(20) NOT NULL COMMENT '优惠券模板ID',
  `order_id` bigint(20) DEFAULT NULL COMMENT '使用订单ID，未使用则为空',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态：0-未使用，1-已使用，2-已过期',
  `receive_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  -- 通用业务字段
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_coupon_id` (`coupon_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员优惠券表';

-- ----------------------------
-- 6. 订单表 (orders)
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `order_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID，主键',
  `member_id` bigint(20) NOT NULL COMMENT '会员ID',
  `coupon_id` bigint(20) DEFAULT NULL COMMENT '使用的会员优惠券记录ID',
  `service_item` varchar(255) NOT NULL COMMENT '服务项目名称',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `point_discount_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '积分抵扣金额',
  `coupon_discount_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '优惠券抵扣金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实际支付金额',
  `pay_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '支付状态：0-待支付，1-已支付，2-已取消',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `pay_method` tinyint(4) DEFAULT NULL COMMENT '支付方式：1-微信，2-支付宝，3-余额',
  -- 通用业务字段
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ----------------------------
-- 7. 积分记录表 (point_record)
-- ----------------------------
DROP TABLE IF EXISTS `point_record`;
CREATE TABLE `point_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID，主键',
  `member_id` bigint(20) NOT NULL COMMENT '会员ID',
  `order_id` bigint(20) NOT NULL COMMENT '关联订单ID',
  `points_change` int(11) NOT NULL COMMENT '变动积分（正数增加，负数扣减）',
  `change_rate` tinyint(4) NOT NULL DEFAULT '1' COMMENT '获取倍率（1-普通会员，2-高级会员）',
  -- 通用业务字段
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_member_id` (`member_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

SET FOREIGN_KEY_CHECKS = 1;
