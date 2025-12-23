package com.ssafy.crewup.course.mapper;

import com.ssafy.crewup.course.CourseReview;
import com.ssafy.crewup.course.dto.response.CourseReviewResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CourseReviewMapper {
    void insertReview(CourseReview review);

    List<CourseReviewResponse> selectReviewList(
            @Param("courseId") Long courseId,
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("userId") Long userId
    );
    int deleteReview(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    void deleteReviewsByCourseId(Long courseId);
}
