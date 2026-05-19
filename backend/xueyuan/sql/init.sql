-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: yanxin_xueyuan
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `course_id` varchar(32) NOT NULL COMMENT '主键',
  `course_name` varchar(100) NOT NULL COMMENT '课程名称',
  `course_type` varchar(50) DEFAULT NULL COMMENT '课程类型',
  `difficulty` varchar(50) DEFAULT NULL COMMENT '难度等级',
  `course_duration` int DEFAULT '0' COMMENT '课程时长/分钟',
  `course_content` text COMMENT '课程内容',
  `video_url` varchar(255) DEFAULT NULL COMMENT '课程视频',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`course_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储课程核心信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exam`
--

DROP TABLE IF EXISTS `exam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam` (
  `exam_id` varchar(32) NOT NULL COMMENT '主键',
  `course_id` varchar(32) NOT NULL COMMENT '关联课程ID',
  `exam_name` varchar(100) NOT NULL COMMENT '考试名称',
  `exam_time` datetime DEFAULT NULL COMMENT '考试时间',
  `exam_duration` int DEFAULT '0' COMMENT '考试时长/分钟',
  `pass_score` int DEFAULT '60' COMMENT '及格线',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`exam_id`) USING BTREE,
  KEY `fk_exam_course` (`course_id`),
  CONSTRAINT `fk_exam_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`course_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储考试相关信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `exam_question`
--

DROP TABLE IF EXISTS `exam_question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_question` (
  `question_id` varchar(32) NOT NULL COMMENT '主键',
  `exam_id` varchar(32) NOT NULL COMMENT '关联考试ID',
  `question_type` varchar(50) DEFAULT NULL COMMENT '题型',
  `question_content` text NOT NULL COMMENT '试题内容',
  `option_a` varchar(255) DEFAULT NULL COMMENT '选项A',
  `option_b` varchar(255) DEFAULT NULL COMMENT '选项B',
  `option_c` varchar(255) DEFAULT NULL COMMENT '选项C',
  `option_d` varchar(255) DEFAULT NULL COMMENT '选项D',
  `correct_answer` varchar(50) NOT NULL COMMENT '正确答案',
  `score` int DEFAULT '0' COMMENT '分值',
  PRIMARY KEY (`question_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储考试试题信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `skill_certification`
--

DROP TABLE IF EXISTS `skill_certification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `skill_certification` (
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
  KEY `fk_cert_exam` (`exam_id`),
  CONSTRAINT `fk_cert_exam` FOREIGN KEY (`exam_id`) REFERENCES `exam` (`exam_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储员工技能认证信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `staff`
--

DROP TABLE IF EXISTS `staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '登录账号',
  `password` varchar(100) NOT NULL COMMENT '登录密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `role_id` bigint NOT NULL COMMENT '角色ID (1:管理员 2:讲师 3:员工)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 (1:正常 0:停用)',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除 (0:未删 1:已删)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `staff_exam`
--

DROP TABLE IF EXISTS `staff_exam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff_exam` (
  `staff_exam_id` varchar(32) NOT NULL COMMENT '主键',
  `staff_id` varchar(32) NOT NULL COMMENT '员工ID',
  `exam_id` varchar(32) NOT NULL COMMENT '考试ID',
  `exam_score` int DEFAULT '0' COMMENT '考试分数',
  `exam_status` varchar(50) DEFAULT NULL COMMENT '考试状态',
  `exam_time` datetime DEFAULT NULL COMMENT '考试时间',
  `makeup_count` int DEFAULT '0' COMMENT '补考次数',
  PRIMARY KEY (`staff_exam_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储员工考试成绩信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `staff_learning`
--

DROP TABLE IF EXISTS `staff_learning`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff_learning` (
  `learn_id` varchar(32) NOT NULL COMMENT '主键',
  `staff_id` varchar(32) NOT NULL COMMENT '员工ID',
  `course_id` varchar(32) NOT NULL COMMENT '课程ID',
  `learn_progress` int DEFAULT '0' COMMENT '学习进度/%',
  `learn_status` varchar(50) DEFAULT NULL COMMENT '学习状态',
  `last_learn_time` datetime DEFAULT NULL COMMENT '最后学习时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`learn_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储员工学习进度信息';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-14 17:28:52
