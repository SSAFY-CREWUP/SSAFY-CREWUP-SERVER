package com.ssafy.crewup.course.mapper;

import com.ssafy.crewup.course.Course;
import com.ssafy.crewup.course.dto.request.CourseSearchCondition;
import com.ssafy.crewup.course.dto.request.CourseUpdateRequest;
import com.ssafy.crewup.course.dto.response.CourseGetResponse;
import com.ssafy.crewup.course.dto.response.CourseListResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;


@Mapper
public interface CourseMapper {
    // 1. 코스 조회 (단건, Entity 반환)
    Optional<Course> selectCourseById(@Param("courseId") Long courseId);

    // 2. 코스 등록
    void insertCourse(Course course);

    // 3. 코스 상세 조회 (DTO 반환)
    CourseGetResponse selectCourseDetail(
            @Param("courseId") Long courseId,
            @Param("userId") Long userId
    );

    // 4. 코스 목록 검색 (통합 검색)
    List<CourseListResponse> selectCourseList(CourseSearchCondition condition);

    // 5. 코스 조회수 증가
    void increaseViewCount(@Param("courseId") Long courseId);

    // 6. 내 코스 조회
    List<CourseListResponse> selectMyCourses(
            @Param("userId") Long userId,
            @Param("offset") int offset,
            @Param("size") int size
    );

    // 7. 코스 수정
    void updateCourse(
            @Param("courseId") Long courseId,
            @Param("req") CourseUpdateRequest request,
            @Param("imageUrl") String imageUrl
    );

    // 8. 코스 삭제
    void deleteCourse(@Param("courseId") Long courseId);
}
