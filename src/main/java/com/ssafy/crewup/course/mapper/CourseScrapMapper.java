package com.ssafy.crewup.course.mapper;

import com.ssafy.crewup.course.dto.response.CourseListResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseScrapMapper {
    boolean existsScrap(@Param("userId") Long userId, @Param("courseId") Long courseId);

    void insertScrap(@Param("userId") Long userId, @Param("courseId") Long courseId);

    void deleteScrap(@Param("userId") Long userId, @Param("courseId") Long courseId);

    void updateScrapCount(@Param("courseId") Long courseId, @Param("delta") int delta);

    void deleteScrapsByCourseId(Long courseId);

    List<CourseListResponse> selectMyScrapCourses(
            @Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("size") int size
    );
}
