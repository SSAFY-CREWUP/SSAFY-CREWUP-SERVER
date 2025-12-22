package com.ssafy.crewup.course.service;

import com.ssafy.crewup.course.dto.request.CourseCreateRequest;
import com.ssafy.crewup.course.dto.request.CourseReviewRequest;
import com.ssafy.crewup.course.dto.response.CourseGetResponse;
import com.ssafy.crewup.course.dto.response.CourseListResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CourseService {
    // 1. 코스 등록
    Long createCourse(CourseCreateRequest request, MultipartFile image, Long writerId);

    // 2. 코스 상세 조회
    CourseGetResponse getCourseDetail(Long courseId, Long userId);

    // 3. 코스 목록 검색
    List<CourseListResponse> getCourseList(String keyword, String difficulty);

    // 4. 리뷰 등록
    void createReview(Long courseId, CourseReviewRequest request, Long writerId);

    // 5. 스크랩 토글
    boolean toggleScrap(Long courseId, Long userId);
}
