package com.ssafy.crewup.course.mapper;

import com.ssafy.crewup.course.CourseReview;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CourseReviewMapper {
    @Select("SELECT review_id AS id, course_id AS courseId, writer_id AS writerId, content, rating, image, created_at AS createdAt, updated_at AS updatedAt FROM course_review WHERE review_id = #{id}")
    CourseReview findById(@Param("id") Long id);

    @Insert("INSERT INTO course_review(course_id, writer_id, content, rating, image) VALUES(#{courseId}, #{writerId}, #{content}, #{rating}, #{image})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "review_id")
    int insert(CourseReview review);

    @Update("UPDATE course_review SET content=#{content}, rating=#{rating}, image=#{image} WHERE review_id=#{id}")
    int update(CourseReview review);

    @Delete("DELETE FROM course_review WHERE review_id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT review_id AS id, course_id AS courseId, writer_id AS writerId, content, rating, image, created_at AS createdAt, updated_at AS updatedAt FROM course_review WHERE course_id = #{courseId} ORDER BY review_id DESC")
    List<CourseReview> findByCourseId(@Param("courseId") Long courseId);
}
