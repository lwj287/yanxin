package com.yanxin.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxin.training.entity.StaffLearning;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StaffLearningMapper extends BaseMapper<StaffLearning> {

    @Select("""
        SELECT
            sl.*,
            c.course_name AS courseName,
            c.video_url AS coverUrl
        FROM staff_learning sl
        LEFT JOIN course c ON sl.course_id = c.course_id
        WHERE sl.staff_id = #{staffId}
        ORDER BY sl.last_learn_time DESC
    """)
    List<StaffLearning> getRecordsByStaffId(@Param("staffId") String staffId);
}
