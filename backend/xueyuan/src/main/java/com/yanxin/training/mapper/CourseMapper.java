package com.yanxin.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxin.training.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {

    @Select("SELECT u.id, u.real_name AS realName, u.phone, u.avatar, sr.learn_progress AS progress, DATE_FORMAT(sr.last_learn_time, '%Y-%m-%d %H:%i:%s') AS joinTime " +
            "FROM staff_learning sr " +
            "JOIN staff u ON sr.staff_id = CAST(u.id AS CHAR) " +
            "WHERE sr.course_id = #{courseId} " +
            "ORDER BY sr.last_learn_time DESC")
    List<Map<String, Object>> getCourseEnrollments(String courseId);

    @Select("SELECT COUNT(*) FROM staff_learning WHERE course_id = #{courseId}")
    Integer getEnrolledCount(String courseId);

    @Select("SELECT COALESCE(SUM(learn_progress), 0) FROM staff_learning")
    Integer getTotalStudyDuration();

    @Select("SELECT " +
            "  DATE_FORMAT(last_learn_time, '%Y-%m-%d') as date, " +
            "  COALESCE(SUM(learn_progress), 0) as duration " +
            "FROM staff_learning " +
            "WHERE last_learn_time >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) " +
            "GROUP BY DATE_FORMAT(last_learn_time, '%Y-%m-%d') " +
            "ORDER BY date ASC")
    List<Map<String, Object>> getRecentStudyTrend();

    @Select("SELECT " +
            "  course_type as name, " +
            "  COUNT(course_id) as value " +
            "FROM course " +
            "GROUP BY course_type " +
            "HAVING value > 0")
    List<Map<String, Object>> getCategoryDistribution();
}