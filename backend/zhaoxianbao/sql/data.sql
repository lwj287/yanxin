USE yanxin_zhaoxianbao;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE sys_user;
INSERT INTO sys_user (id, username, password, real_name, gender, phone, avatar_url, role, status, deleted, create_time, update_time) VALUES
(1, 'admin', '123456', '系统管理员', 1, '13800000001', 'https://example.com/avatar1.png', 'ADMIN', 1, 0, NOW(), NOW()),
(2, 'hr', '123456', '招聘专员', 2, '13800000002', 'https://example.com/avatar2.png', 'HR', 1, 0, NOW(), NOW()),
(3, 'hr3', '123456', '招聘专员3', 1, '13810000003', 'https://example.com/avatar3.png', 'HR', 1, 0, NOW(), NOW());

TRUNCATE TABLE candidate_user;
INSERT INTO candidate_user (id, username, password, real_name, gender, phone, avatar_url, resume_delivery_status, status, deleted, create_time, update_time) VALUES
(1, 'candidate1', '123456', '郑军', 1, '13967449336', 'https://example.com/avatar4.png', 1, 1, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(2, 'candidate2', '123456', '朱平', 2, '13925392859', 'https://example.com/avatar5.png', 0, 1, 0, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(3, 'candidate3', '123456', '沈强', 1, '13933690613', 'https://example.com/avatar6.png', 1, 1, 0, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(4, 'candidate4', '123456', '吴芳', 1, '13939143641', 'https://example.com/avatar7.png', 0, 1, 0, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),
(5, 'candidate5', '123456', '吴杰', 2, '13967008260', 'https://example.com/avatar8.png', 1, 1, 0, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW());

TRUNCATE TABLE candidate_resume_profile;
INSERT INTO candidate_resume_profile (id, candidate_id, real_name, phone, age, education_text, experience_years, skills_text, expected_city_text, self_introduction, photo_url, status, deleted, create_time, update_time) VALUES
(1, 1, '郑军', '13967449336', 39, '中专', 8, '做饭,保洁', '深圳', '为人踏实肯干', 'https://example.com/photo1.png', 1, 0, NOW(), NOW()),
(2, 2, '朱平', '13925392859', 38, '大专', 7, '育婴,辅食', '广州', '喜欢小孩，有耐心', 'https://example.com/photo2.png', 1, 0, NOW(), NOW()),
(3, 3, '沈强', '13933690613', 29, '大专', 3, '育婴', '深圳', '退伍军人，身体素质好', 'https://example.com/photo3.png', 1, 0, NOW(), NOW()),
(4, 4, '吴芳', '13939143641', 35, '中专', 2, '育婴', '广州', '做事细心', 'https://example.com/photo4.png', 1, 0, NOW(), NOW()),
(5, 5, '吴杰', '13967008260', 40, '高中', 5, '养老护理', '广州', '经验丰富', 'https://example.com/photo5.png', 1, 0, NOW(), NOW());

TRUNCATE TABLE job_post;
INSERT INTO job_post (id, title, job_type_code, city_code, area_code, salary_min, salary_max, education_code, min_age, max_age, min_experience_years, required_skills, description, publish_status, recruiter_id, status, deleted, create_time, update_time) VALUES
(1, '月嫂-1', 'NANNY', 'SZ', 'NS', 7396, 10905, 'COLLEGE', 20, 50, 2, 'ELDERCARE', '负责相关工作任务', 1, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
(2, '住家保姆-2', 'NANNY', 'SZ', 'NS', 5941, 10031, 'HIGH', 20, 50, 0, 'ELDERCARE', '负责相关工作任务', 1, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
(3, '家庭保洁-3', 'CLEANER', 'GZ', 'YX', 5699, 10315, 'HIGH', 20, 50, 2, 'COOKING', '负责相关工作任务', 1, 3, 1, 0, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
(4, '护工-4', 'NANNY', 'GZ', 'YX', 4778, 8086, 'MIDDLE', 20, 50, 0, 'BABYCARE', '负责相关工作任务', 1, 3, 1, 0, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),
(5, '月嫂-5', 'NANNY', 'SZ', 'NS', 6151, 9785, 'MIDDLE', 20, 50, 0, 'BABYCARE', '负责相关工作任务', 1, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW());

TRUNCATE TABLE resume_delivery_record;
INSERT INTO resume_delivery_record (id, candidate_id, job_id, real_name, phone, age, education_code, experience_years, skills, expected_city_code, self_introduction, photo_url, screening_score, screening_result, resume_status, status, deleted, create_time, update_time) VALUES
(1, 1, 1, '郑军', '13967449336', 39, 'MIDDLE', 8, 'COOKING', 'SZ', '自我介绍', 'https://example.com/photo.png', 92, 1, 4, 1, 0, DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW()),
(2, 2, 2, '朱平', '13925392859', 38, 'COLLEGE', 7, 'BABYCARE', 'GZ', '自我介绍', 'https://example.com/photo.png', 87, 0, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW()),
(3, 3, 3, '沈强', '13933690613', 29, 'COLLEGE', 3, 'BABYCARE', 'SZ', '自我介绍', 'https://example.com/photo.png', 86, 1, 4, 1, 0, DATE_SUB(NOW(), INTERVAL 3 HOUR), NOW()),
(4, 4, 4, '吴芳', '13939143641', 35, 'MIDDLE', 2, 'BABYCARE', 'GZ', '自我介绍', 'https://example.com/photo.png', 98, 2, 4, 1, 0, DATE_SUB(NOW(), INTERVAL 4 HOUR), NOW()),
(5, 5, 5, '吴杰', '13967008260', 40, 'HIGH', 5, 'ELDERCARE', 'GZ', '自我介绍', 'https://example.com/photo.png', 91, 0, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 5 HOUR), NOW()),
(6, 1, 2, '郑军', '13967449336', 39, 'COLLEGE', 8, 'ELDERCARE', 'GZ', '自我介绍', 'https://example.com/photo.png', 95, 0, 4, 1, 0, DATE_SUB(NOW(), INTERVAL 6 HOUR), NOW()),
(7, 2, 3, '朱平', '13925392859', 25, 'MIDDLE', 5, 'COOKING', 'SZ', '自我介绍', 'https://example.com/photo.png', 74, 1, 2, 1, 0, DATE_SUB(NOW(), INTERVAL 7 HOUR), NOW()),
(8, 3, 4, '沈强', '13933690613', 44, 'COLLEGE', 5, 'ELDERCARE', 'GZ', '自我介绍', 'https://example.com/photo.png', 62, 2, 1, 1, 0, DATE_SUB(NOW(), INTERVAL 8 HOUR), NOW());

TRUNCATE TABLE interview_schedule;
INSERT INTO interview_schedule (id, resume_id, interviewer_id, interview_time, interview_type, interview_location, interview_status, status, deleted, create_time, update_time) VALUES
(1, 1, 2, DATE_ADD(NOW(), INTERVAL 1 HOUR), 1, '会议室A', 'CANCELLED', 1, 0, NOW(), NOW()),
(2, 2, 2, DATE_ADD(NOW(), INTERVAL 2 HOUR), 1, '会议室A', 'COMPLETED', 1, 0, NOW(), NOW()),
(3, 3, 3, DATE_ADD(NOW(), INTERVAL 3 HOUR), 1, '会议室A', 'COMPLETED', 1, 0, NOW(), NOW()),
(4, 4, 3, DATE_ADD(NOW(), INTERVAL 4 HOUR), 1, '会议室A', 'CANCELLED', 1, 0, NOW(), NOW());

TRUNCATE TABLE interview_evaluation;
INSERT INTO interview_evaluation (id, interview_id, evaluator_id, professional_score, communication_score, attitude_score, total_score, evaluate_result, comments, status, deleted, create_time, update_time) VALUES
(1, 1, 2, 73, 94, 77, 81, 1, '评价意见', 1, 0, NOW(), NOW()),
(2, 2, 2, 60, 98, 92, 83, 1, '评价意见', 1, 0, NOW(), NOW()),
(3, 3, 3, 73, 83, 81, 79, 1, '评价意见', 1, 0, NOW(), NOW()),
(4, 4, 3, 75, 94, 62, 77, 1, '评价意见', 1, 0, NOW(), NOW());

TRUNCATE TABLE onboarding_record;
INSERT INTO onboarding_record (id, resume_id, hr_id, onboard_status, docs_verified, onboarding_date, contract_no, remark, status, deleted, create_time, update_time) VALUES
(1, 1, 2, 'COMPLETED', 1, DATE_ADD(NOW(), INTERVAL 1 DAY), 'HT00001', '备注', 1, 0, NOW(), NOW()),
(2, 2, 2, 'CANCELLED', 0, DATE_ADD(NOW(), INTERVAL 2 DAY), 'HT00002', '备注', 1, 0, NOW(), NOW()),
(3, 3, 3, 'PENDING_DOCS', 1, DATE_ADD(NOW(), INTERVAL 3 DAY), 'HT00003', '备注', 1, 0, NOW(), NOW());

TRUNCATE TABLE sys_dict;
INSERT INTO sys_dict (id, dict_type, dict_code, dict_name, sort_no, status, deleted, create_time, update_time) VALUES
(1, 'JOB_TYPE', 'NANNY', '保姆', 1, 1, 0, NOW(), NOW()),
(2, 'JOB_TYPE', 'CLEANER', '保洁', 2, 1, 0, NOW(), NOW()),
(3, 'EDUCATION', 'MIDDLE', '中专/初中', 1, 1, 0, NOW(), NOW()),
(4, 'EDUCATION', 'HIGH', '高中', 2, 1, 0, NOW(), NOW()),
(5, 'EDUCATION', 'COLLEGE', '大专', 3, 1, 0, NOW(), NOW()),
(6, 'CITY', 'SZ', '深圳', 1, 1, 0, NOW(), NOW()),
(7, 'CITY', 'GZ', '广州', 2, 1, 0, NOW(), NOW());

TRUNCATE TABLE sys_operation_log;
INSERT INTO sys_operation_log (id, user_id, username, module_name, operation_type, request_uri, request_method, status, deleted, create_time, update_time) VALUES
(1, 1, 'admin', '用户管理', '新增用户', '/api/sys/user', 'POST', 1, 0, NOW(), NOW()),
(2, 2, 'hr', '岗位管理', '发布岗位', '/api/job/post', 'POST', 1, 0, NOW(), NOW());

TRUNCATE TABLE sys_notice;
INSERT INTO sys_notice (id, receiver_type, receiver_id, title, content, read_flag, status, deleted, create_time, update_time) VALUES
(1, 'CANDIDATE', 1, '面试通知', '您有一个新的面试安排', 0, 1, 0, NOW(), NOW()),
(2, 'HR', 2, '新简历投递', '您发布的岗位收到了新简历', 0, 1, 0, NOW(), NOW());

SET FOREIGN_KEY_CHECKS = 1;
