CREATE DATABASE IF NOT EXISTS yanxin_zhipaidan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE yanxin_zhipaidan;

DROP TABLE IF EXISTS dispatch_record;
DROP TABLE IF EXISTS staff_schedule_status;
DROP TABLE IF EXISTS service_order;

CREATE TABLE service_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  order_no VARCHAR(64) NOT NULL COMMENT '订单编号(来自其他系统)',
  customer_name VARCHAR(64) NOT NULL COMMENT '客户姓名',
  customer_phone VARCHAR(20) NOT NULL COMMENT '客户电话',
  service_address VARCHAR(255) NOT NULL COMMENT '服务地址',
  longitude DECIMAL(10, 6) COMMENT '地址经度(用于计算距离)',
  latitude DECIMAL(10, 6) COMMENT '地址纬度(用于计算距离)',
  service_type VARCHAR(64) NOT NULL COMMENT '所需服务类型(如:日常保洁,月嫂)',
  expect_start_time DATETIME NOT NULL COMMENT '期望服务开始时间',
  expect_end_time DATETIME NOT NULL COMMENT '期望服务结束时间',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-待派单,1-已派单,2-服务中,3-已完成,4-已取消',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_order_no (order_no),
  INDEX idx_status (status),
  INDEX idx_time (expect_start_time, expect_end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务订单表(待调度池)';

CREATE TABLE staff_schedule_status (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  staff_id BIGINT NOT NULL COMMENT '家政员ID(关联全局员工表)',
  staff_name VARCHAR(64) NOT NULL COMMENT '家政员姓名(冗余方便查询)',
  staff_phone VARCHAR(20) NOT NULL COMMENT '家政员电话',
  skills JSON COMMENT '具备的技能标签(JSON数组: ["日常保洁","做饭"])',
  current_status TINYINT NOT NULL DEFAULT 0 COMMENT '当前状态:0-空闲,1-服务中,2-休息/请假',
  last_longitude DECIMAL(10, 6) COMMENT '最后已知经度',
  last_latitude DECIMAL(10, 6) COMMENT '最后已知纬度',
  location_update_time DATETIME COMMENT '位置更新时间',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_staff_id (staff_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='家政员调度状态与技能表';

CREATE TABLE dispatch_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  order_id BIGINT NOT NULL COMMENT '关联订单ID',
  order_no VARCHAR(64) NOT NULL COMMENT '冗余订单编号',
  staff_id BIGINT NOT NULL COMMENT '派给的家政员ID',
  staff_name VARCHAR(64) NOT NULL COMMENT '冗余家政员姓名',
  plan_start_time DATETIME NOT NULL COMMENT '计划开始时间',
  plan_end_time DATETIME NOT NULL COMMENT '计划结束时间',
  dispatch_type TINYINT NOT NULL DEFAULT 0 COMMENT '派单类型:0-系统推荐人工确认,1-完全自动派单,2-人工强行指派',
  estimated_distance INT COMMENT '预估通勤距离(米)',
  status TINYINT NOT NULL DEFAULT 0 COMMENT '状态:0-待接单(可选),1-已接单/生效中,2-已完工,3-已撤销',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除:0-未删除,1-已删除',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '派单时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  INDEX idx_order_id (order_id),
  INDEX idx_staff_id (staff_id),
  INDEX idx_plan_time (plan_start_time, plan_end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='派单结果/行程记录表';
