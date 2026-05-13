-- 燕鑫学院-员工在线培训与技能认证系统
-- 数据库初始化脚本
-- 数据库: yanxin_xueyuan
-- 字符集: utf8mb4

CREATE DATABASE IF NOT EXISTS `yanxin_xueyuan` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `yanxin_xueyuan`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '登录账号',
  `password` varchar(100) NOT NULL COMMENT '登录密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID (1:管理员 2:讲师 3:员工)',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态 (1:正常 0:停用)',
  `is_deleted` tinyint(4) NOT NULL DEFAULT 0 COMMENT '逻辑删除 (0:未删 1:已删)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '用户表';

-- 初始管理员数据: admin / 123456
INSERT INTO `staff` VALUES (1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', '13800138000', NULL, 1, 1, 0, NOW(), NOW());

-- ----------------------------
-- 5. 课程表 (course)
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `course_id` varchar(32) NOT NULL COMMENT '主键',
  `course_name` varchar(100) NOT NULL COMMENT '课程名称',
  `course_type` varchar(50) DEFAULT NULL COMMENT '课程类型',
  `difficulty` varchar(50) DEFAULT NULL COMMENT '难度等级',
  `course_duration` int(11) DEFAULT 0 COMMENT '课程时长/分钟',
  `course_content` text COMMENT '课程内容',
  `course_cover` varchar(255) DEFAULT NULL COMMENT '课程封面',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`course_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '存储课程核心信息';

-- ----------------------------
-- 7. 员工学习表 (staff_learning)
-- ----------------------------
DROP TABLE IF EXISTS `staff_learning`;
CREATE TABLE `staff_learning`  (
  `learn_id` varchar(32) NOT NULL COMMENT '主键',
  `staff_id` varchar(32) NOT NULL COMMENT '员工ID',
  `course_id` varchar(32) NOT NULL COMMENT '课程ID',
  `learn_progress` int(11) DEFAULT 0 COMMENT '学习进度/%',
  `learn_status` varchar(50) DEFAULT NULL COMMENT '学习状态',
  `last_learn_time` datetime DEFAULT NULL COMMENT '最后学习时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`learn_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '存储员工学习进度信息';

-- ----------------------------
-- 8. 试题表 (exam_question)
-- ----------------------------
DROP TABLE IF EXISTS `exam_question`;
CREATE TABLE `exam_question`  (
  `question_id` varchar(32) NOT NULL COMMENT '主键',
  `exam_id` varchar(32) NOT NULL COMMENT '关联考试ID',
  `question_type` varchar(50) DEFAULT NULL COMMENT '题型',
  `question_content` text NOT NULL COMMENT '试题内容',
  `option_a` varchar(255) DEFAULT NULL COMMENT '选项A',
  `option_b` varchar(255) DEFAULT NULL COMMENT '选项B',
  `option_c` varchar(255) DEFAULT NULL COMMENT '选项C',
  `option_d` varchar(255) DEFAULT NULL COMMENT '选项D',
  `correct_answer` varchar(50) NOT NULL COMMENT '正确答案',
  `score` int(11) DEFAULT 0 COMMENT '分值',
  PRIMARY KEY (`question_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '存储考试试题信息';

-- ----------------------------
-- 9. 考试表 (exam)
-- ----------------------------
DROP TABLE IF EXISTS `exam`;
CREATE TABLE `exam`  (
  `exam_id` varchar(32) NOT NULL COMMENT '主键',
  `course_id` varchar(32) NOT NULL COMMENT '关联课程ID',
  `exam_name` varchar(100) NOT NULL COMMENT '考试名称',
  `exam_time` datetime DEFAULT NULL COMMENT '考试时间',
  `exam_duration` int(11) DEFAULT 0 COMMENT '考试时长/分钟',
  `pass_score` int(11) DEFAULT 60 COMMENT '及格线',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`exam_id`) USING BTREE,
  CONSTRAINT `fk_exam_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`) ON DELETE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '存储考试相关信息';

-- ----------------------------
-- 10. 员工考试表 (staff_exam)
-- ----------------------------
DROP TABLE IF EXISTS `staff_exam`;
CREATE TABLE `staff_exam`  (
  `staff_exam_id` varchar(32) NOT NULL COMMENT '主键',
  `staff_id` varchar(32) NOT NULL COMMENT '员工ID',
  `exam_id` varchar(32) NOT NULL COMMENT '考试ID',
  `exam_score` int(11) DEFAULT 0 COMMENT '考试分数',
  `exam_status` varchar(50) DEFAULT NULL COMMENT '考试状态',
  `exam_time` datetime DEFAULT NULL COMMENT '考试时间',
  `makeup_count` int(11) DEFAULT 0 COMMENT '补考次数',
  PRIMARY KEY (`staff_exam_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '存储员工考试成绩信息';

-- ----------------------------
-- 11. 技能认证表 (skill_certification)
-- ----------------------------
DROP TABLE IF EXISTS `skill_certification`;
CREATE TABLE `skill_certification`  (
  `cert_id` varchar(32) NOT NULL COMMENT '主键',
  `staff_id` varchar(32) NOT NULL COMMENT '员工ID',
  `exam_id` varchar(32) DEFAULT NULL COMMENT '考试ID',
  `cert_name` varchar(100) NOT NULL COMMENT '证书名称',
  `cert_status` varchar(50) DEFAULT NULL COMMENT '认证状态',
  `apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `cert_validity` datetime DEFAULT NULL COMMENT '有效期',
  `cert_url` varchar(255) DEFAULT NULL COMMENT '电子证书地址',
  PRIMARY KEY (`cert_id`) USING BTREE,
  CONSTRAINT `fk_cert_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`exam_id`) ON DELETE SET NULL
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COMMENT = '存储员工技能认证信息';



SET FOREIGN_KEY_CHECKS = 1;
