package com.yanxin.training.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanxin.training.entity.Certification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CertificationMapper extends BaseMapper<Certification> {

    @Select("""
        <script>
        SELECT
            c.*,
            u.real_name AS userName,
            e.exam_name AS examName,
            c.cert_name AS certName,
            co.difficulty AS level
        FROM skill_certification c
        LEFT JOIN staff u ON c.staff_id = CAST(u.id AS CHAR)
        LEFT JOIN exam e ON c.exam_id = e.exam_id
        LEFT JOIN course co ON e.course_id = co.course_id
        WHERE 1=1
        <if test="certLevel != null and certLevel != ''">
            AND (c.cert_name LIKE CONCAT('%', #{certLevel}, '%') OR u.real_name LIKE CONCAT('%', #{certLevel}, '%'))
        </if>
        <if test="certStatus != null and certStatus != ''">
            AND c.cert_status = #{certStatus}
        </if>
        ORDER BY c.apply_time DESC
        </script>
    """)
    Page<Certification> selectCertPage(Page<Certification> page, 
                                       @Param("certLevel") String certLevel, 
                                       @Param("certStatus") String certStatus);
}