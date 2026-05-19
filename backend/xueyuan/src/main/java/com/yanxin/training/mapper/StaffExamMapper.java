package com.yanxin.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxin.training.entity.StaffExam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StaffExamMapper extends BaseMapper<StaffExam> {

    @Select("""
        SELECT
            se.*,
            e.exam_name AS examName,
            e.exam_duration AS duration
        FROM staff_exam se
        LEFT JOIN exam e ON se.exam_id = e.exam_id
        WHERE se.staff_id = #{staffId}
        ORDER BY se.exam_time DESC
    """)
    List<StaffExam> getRecordsByStaffId(@Param("staffId") String staffId);
}
