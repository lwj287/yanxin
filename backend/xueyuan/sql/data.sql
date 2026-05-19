-- MySQL dump
-- Business data for Yanxin Academy

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. staff
TRUNCATE TABLE `staff`;
INSERT INTO `staff` (`id`, `username`, `password`, `real_name`, `phone`, `role_id`, `status`, `is_deleted`, `create_time`) VALUES
(1, 'admin', '123456', '超级管理员', '13800138000', 1, 1, 0, NOW()),
(2, 'teacher1', '123456', '张讲师', '13800138001', 2, 1, 0, NOW()),
(3, 'teacher2', '123456', '王讲师', '13800138002', 2, 1, 0, NOW()),
(4, 'staff1', '123456', '李阿姨', '13800138003', 3, 1, 0, NOW()),
(5, 'staff2', '123456', '赵阿姨', '13800138004', 3, 1, 0, NOW()),
(6, 'staff3', '123456', '陈阿姨', '13800138005', 3, 1, 0, NOW()),
(7, 'staff4', '123456', '刘阿姨', '13800138006', 3, 1, 0, NOW()),
(8, 'staff5', '123456', '周阿姨', '13800138007', 3, 1, 0, NOW()),
(9, 'staff6', '123456', '吴阿姨', '13800138008', 3, 1, 0, NOW()),
(10, 'staff7', '123456', '郑阿姨', '13800138009', 3, 1, 0, NOW()),
(11, 'staff8', '123456', '孙阿姨', '13800138010', 3, 1, 0, NOW()),
(12, 'staff9', '123456', '朱阿姨', '13800138011', 3, 1, 0, NOW());

-- 2. course
TRUNCATE TABLE `course`;
INSERT INTO `course` (`course_id`, `course_name`, `course_type`, `difficulty`, `course_duration`, `course_content`, `video_url`, `create_time`) VALUES
('c_001', '金牌月嫂高级护理精讲', '母婴护理', '高级', 120, '本课程涵盖新生儿日常护理、产妇产后恢复及营养月子餐制作等高级技能。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW()),
('c_002', '专业保洁实操与收纳技巧', '保洁服务', '中级', 90, '详细讲解不同材质家具的清洁方法、全屋收纳整理技巧及保洁工具的正确使用。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW()),
('c_003', '养老护理基础实操', '养老护理', '初级', 80, '老年人日常起居照料、心理陪伴及基础急救知识。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW()),
('c_004', '高级中餐烹饪与营养搭配', '烹饪技能', '高级', 150, '家常菜升级、月子餐特色菜、老年人营养餐配比及烹饪技巧。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW()),
('c_005', '婴幼儿早教与智力开发', '母婴护理', '中级', 110, '0-3岁婴幼儿心理学基础、游戏互动技巧、蒙氏早教方法。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW()),
('c_006', '家庭急救与意外处理', '职业素养', '初级', 60, '烫伤、摔伤、异物卡喉(海姆立克急救法)等家庭常见意外的紧急处理。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW()),
('c_007', '高级收纳整理师认证课', '保洁服务', '高级', 200, '日式收纳法、衣橱规划、空间动线设计及高端客户服务礼仪。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW()),
('c_008', '产后心理疏导与干预', '母婴护理', '高级', 90, '识别产后抑郁倾向、沟通技巧、产妇心理支持与抚慰。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW()),
('c_009', '涉外家政英语基础', '职业素养', '中级', 120, '常用家政英语口语、西式餐饮习惯、外籍家庭服务注意事项。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW()),
('c_010', '阿尔茨海默症老人照护', '养老护理', '高级', 140, '认知症老人生活照料、防走失措施、情绪安抚技巧。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW()),
('c_011', '孕产妇中医食疗保健', '烹饪技能', '中级', 100, '中医体质辨识、孕期安胎食谱、产后催乳/回奶汤饮制作。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW()),
('c_012', '智能家居设备使用指南', '职业素养', '初级', 45, '常见智能家电（扫地机器人、智能烤箱、洗碗机）的操作与维护。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW()),
('c_013', '月嫂入户服务流程与礼仪', '母婴护理', '初级', 60, '入户第一天该做什么、与客户沟通的禁忌、家政人员基本礼仪。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW()),
('c_014', '高端衣物洗护与熨烫', '保洁服务', '中级', 85, '真丝、羊绒等特殊面料的清洗、保养与专业熨烫技巧。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW()),
('c_015', '老年人慢性病日常护理', '养老护理', '中级', 110, '高血压、糖尿病老人的饮食控制、用药提醒及日常生命体征监测。', '/api/uploads/2026-05-19/fdd3a8d8a07b4a80997c00b4bb48be08.mp4', NOW());

-- 3. exam
TRUNCATE TABLE `exam`;
INSERT INTO `exam` (`exam_id`, `course_id`, `exam_name`, `exam_time`, `exam_duration`, `pass_score`, `create_time`) VALUES
('e_001', 'c_001', '金牌月嫂结业考试', '2026-06-01 10:00:00', 60, 80, NOW()),
('e_002', 'c_002', '专业保洁技能考核', '2026-06-15 14:00:00', 45, 60, NOW()),
('e_003', 'c_003', '养老护理基础测试', '2026-06-20 14:00:00', 45, 60, NOW()),
('e_004', 'c_007', '高级收纳整理师认证考试', '2026-07-01 09:00:00', 90, 85, NOW()),
('e_005', 'c_010', '认知症照护专项考核', '2026-07-10 10:00:00', 60, 75, NOW());

-- 4. exam_question
TRUNCATE TABLE `exam_question`;
INSERT INTO `exam_question` (`question_id`, `exam_id`, `question_type`, `question_content`, `option_a`, `option_b`, `option_c`, `option_d`, `correct_answer`, `score`) VALUES
('q_001', 'e_001', '单选题', '新生儿洗澡的适宜水温是多少？', '30-32℃', '37-40℃', '42-45℃', '45℃以上', 'B', 50),
('q_002', 'e_001', '单选题', '产妇月子期间饮食应遵循什么原则？', '大补特补', '少食多餐，营养均衡', '只吃素食', '多吃生冷', 'B', 50),
('q_003', 'e_002', '单选题', '清洁皮质沙发时，应避免使用以下哪种物品？', '软布', '专用皮具清洁剂', '钢丝球', '清水', 'C', 100),
('q_004', 'e_003', '单选题', '老年人进食时发生呛咳，首选的急救方法是？', '拍打背部', '海姆立克急救法', '喝水顺下去', '让其平躺', 'B', 100),
('q_005', 'e_004', '单选题', '日式收纳的核心原则之一是？', '全部扔掉', '直立收纳', '随便堆放', '只买贵的', 'B', 50),
('q_006', 'e_004', '多选题', '规划衣橱动线时需要考虑哪些因素？', '客户身高', '衣物使用频率', '季节变化', '衣柜颜色', 'ABC', 50);

-- 5. staff_learning
TRUNCATE TABLE `staff_learning`;
INSERT INTO `staff_learning` (`learn_id`, `staff_id`, `course_id`, `learn_progress`, `learn_status`, `last_learn_time`, `complete_time`) VALUES
('l_001', '4', 'c_001', 100, '已完成', NOW(), NOW()),
('l_002', '4', 'c_002', 50, '学习中', NOW(), NULL),
('l_003', '5', 'c_001', 20, '学习中', NOW(), NULL),
('l_004', '6', 'c_007', 100, '已完成', NOW(), NOW()),
('l_005', '6', 'c_014', 100, '已完成', NOW(), NOW()),
('l_006', '7', 'c_003', 80, '学习中', NOW(), NULL),
('l_007', '8', 'c_010', 100, '已完成', NOW(), NOW()),
('l_008', '9', 'c_005', 60, '学习中', NOW(), NULL),
('l_009', '10', 'c_001', 100, '已完成', NOW(), NOW()),
('l_010', '10', 'c_008', 90, '学习中', NOW(), NULL),
('l_011', '11', 'c_011', 100, '已完成', NOW(), NOW());

-- 6. staff_exam
TRUNCATE TABLE `staff_exam`;
INSERT INTO `staff_exam` (`staff_exam_id`, `staff_id`, `exam_id`, `exam_score`, `exam_status`, `exam_time`, `makeup_count`) VALUES
('se_001', '4', 'e_001', 95, '已及格', NOW(), 0),
('se_002', '6', 'e_004', 88, '已及格', NOW(), 0),
('se_003', '8', 'e_005', 76, '已及格', NOW(), 0),
('se_004', '10', 'e_001', 82, '已及格', NOW(), 1);

-- 7. skill_certification
TRUNCATE TABLE `skill_certification`;
INSERT INTO `skill_certification` (`cert_id`, `staff_id`, `exam_id`, `cert_name`, `cert_status`, `apply_time`, `audit_time`, `cert_validity`, `cert_url`) VALUES
('cert_001', '4', 'e_001', '高级母婴护理师证', '已通过', NOW(), NOW(), '2029-01-01 00:00:00', 'https://dummyimage.com/600x400/409eff/ffffff&text=Certificate'),
('cert_002', '6', 'e_004', '高级收纳整理师证', '已通过', NOW(), NOW(), '2029-05-01 00:00:00', 'https://dummyimage.com/600x400/409eff/ffffff&text=Certificate'),
('cert_003', '8', 'e_005', '认知症照护专项证书', '待审核', NOW(), NULL, NULL, NULL),
('cert_004', '10', 'e_001', '高级母婴护理师证', '已通过', NOW(), NOW(), '2029-06-01 00:00:00', 'https://dummyimage.com/600x400/409eff/ffffff&text=Certificate');

SET FOREIGN_KEY_CHECKS = 1;