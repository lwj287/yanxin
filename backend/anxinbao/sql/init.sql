-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: yanxin_anxinbao
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
-- Table structure for table `claim`
--

DROP TABLE IF EXISTS `claim`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `claim` (
  `claim_id` bigint NOT NULL AUTO_INCREMENT COMMENT '理赔ID',
  `staff_id` bigint NOT NULL COMMENT '员工ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `claim_amount` decimal(10,2) NOT NULL COMMENT '理赔金额(元)',
  `material_url` text COMMENT '理赔素材地址',
  `claim_status` tinyint NOT NULL DEFAULT '0' COMMENT '理赔状态(0-待审核 1-处理中 2-已赔付 3-已驳回)',
  `apply_time` datetime NOT NULL COMMENT '申请时间',
  `claim_time` datetime DEFAULT NULL COMMENT '理赔(结案)时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`claim_id`),
  UNIQUE KEY `uk_claim_order` (`order_id`),
  KEY `idx_claim_staff` (`staff_id`),
  KEY `idx_claim_status_deleted` (`claim_status`,`is_deleted`),
  CONSTRAINT `fk_claim_order` FOREIGN KEY (`order_id`) REFERENCES `insurance_order` (`order_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_claim_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='理赔管理表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract`
--

DROP TABLE IF EXISTS `contract`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contract` (
  `contract_id` bigint NOT NULL AUTO_INCREMENT COMMENT '合同ID',
  `staff_id` bigint NOT NULL COMMENT '员工ID',
  `template_id` bigint NOT NULL COMMENT '模板ID',
  `contract_content` longtext COMMENT '合同内容(动态生成后内容)',
  `sign_time` datetime DEFAULT NULL COMMENT '签署时间',
  `contract_status` tinyint NOT NULL DEFAULT '0' COMMENT '合同状态(0-待签署 1-已生效 2-已过期 3-已作废)',
  `expire_time` datetime DEFAULT NULL COMMENT '到期时间',
  `storage_url` varchar(255) DEFAULT NULL COMMENT '合同存储地址',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`contract_id`),
  KEY `idx_contract_staff` (`staff_id`),
  KEY `idx_contract_template` (`template_id`),
  KEY `idx_contract_status_deleted` (`contract_status`,`is_deleted`),
  CONSTRAINT `fk_contract_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_contract_template` FOREIGN KEY (`template_id`) REFERENCES `contract_template` (`template_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='电子合同表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `contract_template`
--

DROP TABLE IF EXISTS `contract_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contract_template` (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `contract_type` tinyint NOT NULL COMMENT '合同类型(1-标准合同 2-短期合同 3-补充协议)',
  `template_content` longtext NOT NULL COMMENT '模板内容',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '模板状态(0-停用 1-启用)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`template_id`),
  KEY `idx_contract_template_type_status` (`contract_type`,`status`),
  KEY `idx_contract_template_status_deleted` (`status`,`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='合同模板表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `insurance_order`
--

DROP TABLE IF EXISTS `insurance_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `insurance_order` (
  `order_id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `staff_id` bigint NOT NULL COMMENT '员工ID',
  `product_id` bigint NOT NULL COMMENT '产品ID',
  `premium` decimal(10,2) NOT NULL COMMENT '保费(实付金额)',
  `insure_time` datetime NOT NULL COMMENT '投保时间',
  `expire_time` datetime NOT NULL COMMENT '到期时间',
  `claim_status` tinyint NOT NULL DEFAULT '0' COMMENT '理赔状态(0-无理赔 1-理赔中 2-已理赔 3-已驳回)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '保障状态(1-保障中 2-已过期 3-已退保)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`order_id`),
  KEY `idx_insurance_order_staff` (`staff_id`),
  KEY `idx_insurance_order_product` (`product_id`),
  KEY `idx_insurance_order_status_deleted` (`status`,`is_deleted`),
  KEY `idx_insurance_order_claim_status_deleted` (`claim_status`,`is_deleted`),
  CONSTRAINT `fk_insurance_order_product` FOREIGN KEY (`product_id`) REFERENCES `insurance_product` (`product_id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_insurance_order_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='保险订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `insurance_product`
--

DROP TABLE IF EXISTS `insurance_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `insurance_product` (
  `product_id` bigint NOT NULL AUTO_INCREMENT COMMENT '产品ID',
  `product_name` varchar(100) NOT NULL COMMENT '产品名称',
  `insurance_type` tinyint NOT NULL COMMENT '保险类型(1-意外险 2-责任险 3-短期临时工险)',
  `coverage_amount` decimal(10,2) NOT NULL COMMENT '保险金额(元)',
  `premium` decimal(10,2) NOT NULL COMMENT '保费(元)',
  `duration_days` int NOT NULL COMMENT '保险期限/天',
  `conditions` text COMMENT '投保条件',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0-下架 1-上架)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`product_id`),
  KEY `idx_insurance_product_type_status` (`insurance_type`,`status`),
  KEY `idx_insurance_product_status_deleted` (`status`,`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='保险产品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `realname_auth`
--

DROP TABLE IF EXISTS `realname_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `realname_auth` (
  `auth_id` bigint NOT NULL AUTO_INCREMENT COMMENT '认证ID',
  `staff_id` bigint NOT NULL COMMENT '员工ID',
  `staff_type` tinyint NOT NULL COMMENT '员工类型(1-全职 2-兼职 3-临时)',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `face_material_url` varchar(255) DEFAULT NULL COMMENT '人脸识别素材地址',
  `auth_status` tinyint NOT NULL DEFAULT '0' COMMENT '认证状态(0-待审核 1-已通过 2-已驳回)',
  `auth_time` datetime DEFAULT NULL COMMENT '认证时间',
  `auth_report` text COMMENT '认证报告',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`auth_id`),
  UNIQUE KEY `uk_realname_auth_id_card` (`id_card`),
  KEY `idx_realname_auth_staff` (`staff_id`),
  KEY `idx_realname_auth_status_deleted` (`auth_status`,`is_deleted`),
  CONSTRAINT `fk_realname_auth_staff` FOREIGN KEY (`staff_id`) REFERENCES `staff` (`staff_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='实名认证表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `staff`
--

DROP TABLE IF EXISTS `staff`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff` (
  `staff_id` bigint NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `role_id` bigint DEFAULT NULL COMMENT '角色ID(1-超级管理员 2-家政员)',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像地址',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0-停用 1-启用)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除 1-已删除)',
  PRIMARY KEY (`staff_id`),
  UNIQUE KEY `uk_staff_phone` (`phone`),
  KEY `idx_staff_role_status` (`role_id`,`status`),
  KEY `idx_staff_status_deleted` (`status`,`is_deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工表';
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
