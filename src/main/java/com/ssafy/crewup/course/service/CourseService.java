package com.ssafy.crewup.course.service;

import com.ssafy.crewup.course.dto.request.CourseCreateRequest;
import com.ssafy.crewup.course.dto.request.CourseReviewRequest;
import com.ssafy.crewup.course.dto.request.CourseSearchCondition;
import com.ssafy.crewup.course.dto.request.CourseUpdateRequest;
import com.ssafy.crewup.course.dto.response.CourseGetResponse;
import com.ssafy.crewup.course.dto.response.CourseListResponse;
import com.ssafy.crewup.course.dto.response.CourseReviewResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {
    Long createCourse(CourseCreateRequest request, MultipartFile image, Long writerId);

    CourseGetResponse getCourseDetail(Long courseId, Long userId);

    List<CourseListResponse> getCourseList(CourseSearchCondition condition);

    void createReview(Long courseId, MultipartFile image,CourseReviewRequest request, Long writerId);

    boolean toggleScrap(Long courseId, Long userId);

    List<CourseReviewResponse> getReviewList(Long courseId, int page, int size, Long userId);

    void deleteReview(Long reviewId, Long userId);

    List<CourseListResponse> getMyScrapCourses(Long userId, int page, int size);

    List<CourseListResponse> getMyCourses(Long userId, int page, int size);

    void updateCourse(Long courseId, CourseUpdateRequest request, MultipartFile image, Long userId);

    void deleteCourse(Long courseId, Long userId);
}
