package com.ssafy.crewup.course.mapper;

import com.ssafy.crewup.course.CourseScrap;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CourseScrapMapper {
    @Select("SELECT id, user_id AS userId, course_id AS courseId, created_at AS createdAt, updated_at AS updatedAt FROM course_scrap WHERE id = #{id}")
    CourseScrap findById(@Param("id") Long id);

    @Insert("INSERT INTO course_scrap(user_id, course_id) VALUES(#{userId}, #{courseId})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(CourseScrap scrap);

    @Delete("DELETE FROM course_scrap WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT id, user_id AS userId, course_id AS courseId, created_at AS createdAt, updated_at AS updatedAt FROM course_scrap WHERE user_id = #{userId}")
    List<CourseScrap> findByUserId(@Param("userId") Long userId);

    @Select("SELECT id, user_id AS userId, course_id AS courseId, created_at AS createdAt, updated_at AS updatedAt FROM course_scrap WHERE course_id = #{courseId}")
    List<CourseScrap> findByCourseId(@Param("courseId") Long courseId);
}
