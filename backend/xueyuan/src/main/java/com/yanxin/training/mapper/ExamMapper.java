package com.yanxin.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanxin.training.entity.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ExamMapper extends BaseMapper<Exam> {

    @Select("SELECT u.id, u.avatar, u.real_name AS realName, u.phone, er.exam_score AS score, " +
            "(er.exam_score >= e.pass_score) AS isPassed, " +
            "DATE_FORMAT(er.exam_time, '%Y-%m-%d %H:%i:%s') AS submitTime " +
            "FROM staff_exam er " +
            "JOIN staff u ON er.staff_id = CAST(u.id AS CHAR) " +
            "JOIN exam e ON er.exam_id = e.exam_id " +
            "WHERE er.exam_id = #{examId} " +
            "ORDER BY er.exam_score DESC, er.exam_time ASC")
    List<Map<String, Object>> getExamRecords(String examId);
}