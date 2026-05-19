CREATE DATABASE IF NOT EXISTS yanxin_zhaoxianbao DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE yanxin_zhaoxianbao;
SET NAMES utf8mb4;

DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
  password VARCHAR(200) NOT NULL COMMENT '登录密码',
  real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
  gender TINYINT COMMENT '性别(1男 2女)',
  phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
  avatar_url VARCHAR(255) COMMENT '头像地址',
  role VARCHAR(20) NOT NULL COMMENT '角色(ADMIN/HR)',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用 0禁用)',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否 1是)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='系统用户表';

DROP TABLE IF EXISTS candidate_user;
CREATE TABLE candidate_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
  password VARCHAR(200) NOT NULL COMMENT '登录密码',
  real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
  gender TINYINT COMMENT '性别(1男 2女)',
  phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
  avatar_url VARCHAR(255) COMMENT '头像地址',
  resume_delivery_status TINYINT NOT NULL DEFAULT 0 COMMENT '简历投递情况(0未投递 1已投递)',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用 0禁用)',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否 1是)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='求职者用户表';

DROP TABLE IF EXISTS job_post;
CREATE TABLE job_post (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  title VARCHAR(100) NOT NULL COMMENT '岗位名称',
  job_type_code VARCHAR(50) NOT NULL COMMENT '岗位类型编码',
  city_code VARCHAR(20) NOT NULL COMMENT '城市编码',
  area_code VARCHAR(20) COMMENT '区域编码',
  salary_min DECIMAL(10,2) NOT NULL COMMENT '最低薪资',
  salary_max DECIMAL(10,2) NOT NULL COMMENT '最高薪资',
  education_code VARCHAR(50) COMMENT '学历编码',
  min_age INT COMMENT '最小年龄',
  max_age INT COMMENT '最大年龄',
  min_experience_years INT DEFAULT 0 COMMENT '最少工作年限',
  required_skills VARCHAR(500) COMMENT '技能要求(逗号分隔编码)',
  description TEXT COMMENT '岗位描述',
  publish_status TINYINT NOT NULL DEFAULT 0 COMMENT '发布状态(0下架 1上架)',
  recruiter_id BIGINT COMMENT '发布人ID',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用 0禁用)',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否 1是)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_job_title_area (title, city_code, area_code)
) ENGINE=InnoDB COMMENT='岗位信息表';

DROP TABLE IF EXISTS candidate_resume_profile;
CREATE TABLE candidate_resume_profile (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  candidate_id BIGINT NOT NULL COMMENT '求职者ID',
  real_name VARCHAR(50) NOT NULL COMMENT '姓名',
  phone VARCHAR(20) NOT NULL COMMENT '手机号',
  age INT COMMENT '年龄',
  education_text VARCHAR(100) COMMENT '学历（中文）',
  experience_years INT DEFAULT 0 COMMENT '工作年限',
  skills_text VARCHAR(1000) COMMENT '技能（中文，逗号分隔）',
  expected_city_text VARCHAR(100) COMMENT '期望城市（中文）',
  self_introduction TEXT COMMENT '个人介绍',
  photo_url VARCHAR(255) COMMENT '照片地址',
  id_card_front_url VARCHAR(255) COMMENT '身份证正面地址',
  id_card_back_url VARCHAR(255) COMMENT '身份证反面地址',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用 0禁用)',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否 1是)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_candidate_resume_profile_candidate (candidate_id)
) ENGINE=InnoDB COMMENT='求职者在线简历档案表';

DROP TABLE IF EXISTS resume_delivery_record;
CREATE TABLE resume_delivery_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  candidate_id BIGINT NOT NULL COMMENT '求职者ID',
  job_id BIGINT NOT NULL COMMENT '岗位ID',
  real_name VARCHAR(50) NOT NULL COMMENT '姓名',
  phone VARCHAR(20) NOT NULL COMMENT '手机号',
  age INT COMMENT '年龄',
  education_code VARCHAR(50) COMMENT '学历编码',
  experience_years INT DEFAULT 0 COMMENT '工作年限',
  skills VARCHAR(500) COMMENT '技能(逗号分隔编码)',
  expected_city_code VARCHAR(20) COMMENT '期望城市编码',
  self_introduction TEXT COMMENT '自我介绍',
  photo_url VARCHAR(255) COMMENT '照片地址',
  id_card_front_url VARCHAR(255) COMMENT '身份证正面地址',
  id_card_back_url VARCHAR(255) COMMENT '身份证反面地址',
  screening_score INT DEFAULT 0 COMMENT '筛选得分',
  screening_result TINYINT DEFAULT 0 COMMENT '筛选结果(0待筛选 1合格 2不合格)',
  screening_reason VARCHAR(255) COMMENT '筛选原因',
  resume_status TINYINT NOT NULL DEFAULT 1 COMMENT '简历状态(1已投递 2合格 3不合格 4已入职)',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用 0禁用)',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否 1是)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='简历投递记录表';

DROP TABLE IF EXISTS interview_schedule;
CREATE TABLE interview_schedule (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  resume_id BIGINT NOT NULL COMMENT '简历ID',
  interviewer_id BIGINT NOT NULL COMMENT '面试官ID',
  interview_time DATETIME NOT NULL COMMENT '面试时间',
  interview_type TINYINT NOT NULL DEFAULT 1 COMMENT '面试类型(1线下 2线上)',
  interview_location VARCHAR(200) COMMENT '面试地点',
  interview_link VARCHAR(255) COMMENT '线上链接',
  notice_sent TINYINT NOT NULL DEFAULT 0 COMMENT '通知状态(0未发送 1已发送)',
  interview_status VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT '面试状态',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用 0禁用)',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否 1是)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='面试安排表';

DROP TABLE IF EXISTS interview_evaluation;
CREATE TABLE interview_evaluation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  interview_id BIGINT NOT NULL COMMENT '面试安排ID',
  evaluator_id BIGINT NOT NULL COMMENT '评价人ID',
  professional_score INT NOT NULL COMMENT '专业能力分',
  communication_score INT NOT NULL COMMENT '沟通能力分',
  attitude_score INT NOT NULL COMMENT '职业态度分',
  total_score INT NOT NULL COMMENT '总分',
  evaluate_result TINYINT NOT NULL COMMENT '评价结果(1合格 2不合格)',
  comments VARCHAR(500) COMMENT '评价意见',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用 0禁用)',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否 1是)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='面试评价表';

DROP TABLE IF EXISTS onboarding_record;
CREATE TABLE onboarding_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  resume_id BIGINT NOT NULL COMMENT '简历ID',
  hr_id BIGINT NOT NULL COMMENT 'HR用户ID',
  onboard_status VARCHAR(30) NOT NULL DEFAULT 'PENDING_DOCS' COMMENT '入职状态',
  docs_verified TINYINT NOT NULL DEFAULT 0 COMMENT '材料是否审核(0否 1是)',
  onboarding_date DATE COMMENT '入职日期',
  contract_no VARCHAR(100) COMMENT '合同编号',
  remark VARCHAR(255) COMMENT '备注',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用 0禁用)',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否 1是)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='入职登记表';

DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  dict_type VARCHAR(50) NOT NULL COMMENT '字典类型',
  dict_code VARCHAR(50) NOT NULL COMMENT '字典编码',
  dict_name VARCHAR(100) NOT NULL COMMENT '字典名称',
  sort_no INT DEFAULT 0 COMMENT '排序号',
  ext_value VARCHAR(255) COMMENT '扩展值',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用 0禁用)',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否 1是)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_type_code (dict_type, dict_code)
) ENGINE=InnoDB COMMENT='系统字典表';

DROP TABLE IF EXISTS sys_operation_log;
CREATE TABLE sys_operation_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT COMMENT '操作人ID',
  username VARCHAR(50) COMMENT '操作人用户名',
  module_name VARCHAR(100) COMMENT '模块名称',
  operation_type VARCHAR(50) COMMENT '操作类型',
  request_uri VARCHAR(255) COMMENT '请求地址',
  request_method VARCHAR(20) COMMENT '请求方法',
  request_param TEXT COMMENT '请求参数',
  response_data TEXT COMMENT '响应数据',
  ip VARCHAR(50) COMMENT 'IP地址',
  cost_ms BIGINT COMMENT '耗时(毫秒)',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1成功 0失败)',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否 1是)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='系统操作日志表';

DROP TABLE IF EXISTS sys_notice;
CREATE TABLE sys_notice (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  receiver_type VARCHAR(20) NOT NULL COMMENT '接收人类型(HR/CANDIDATE)',
  receiver_id BIGINT NOT NULL COMMENT '接收人ID',
  title VARCHAR(100) NOT NULL COMMENT '通知标题',
  content VARCHAR(500) NOT NULL COMMENT '通知内容',
  read_flag TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读(0未读 1已读)',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1有效 0无效)',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否 1是)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB COMMENT='系统通知表';
