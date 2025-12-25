package com.ssafy.crewup.course.service.impl;

import com.ssafy.crewup.course.Course;
import com.ssafy.crewup.course.CourseReview;
import com.ssafy.crewup.course.dto.common.PointDto;
import com.ssafy.crewup.course.dto.request.CourseCreateRequest;
import com.ssafy.crewup.course.dto.request.CourseReviewRequest;
import com.ssafy.crewup.course.dto.request.CourseSearchCondition;
import com.ssafy.crewup.course.dto.request.CourseUpdateRequest;
import com.ssafy.crewup.course.dto.response.CourseGetResponse;
import com.ssafy.crewup.course.dto.response.CourseListResponse;
import com.ssafy.crewup.course.dto.response.CourseReviewResponse;
import com.ssafy.crewup.course.mapper.CourseMapper;
import com.ssafy.crewup.course.mapper.CourseReviewMapper;
import com.ssafy.crewup.course.mapper.CourseScrapMapper;
import com.ssafy.crewup.course.service.CourseService;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.global.event.ReviewCreatedEvent;
import com.ssafy.crewup.global.service.S3Service;
import com.ssafy.crewup.global.util.GeometryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final CourseReviewMapper courseReviewMapper;
    private final CourseScrapMapper  courseScrapMapper;
    private final S3Service s3Service;
    private final GeometryUtil geometryUtil;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Long createCourse(CourseCreateRequest request, MultipartFile image, Long writerId) {
        String imageUrl = uploadImage(image, "static/course");

        // 1. Path 변환
        String pathWkt = geometryUtil.convertToWkt(request.getPath());

        // 2. MainPoint 변환
        String mainPointWkt = null;
        if (request.getPath() != null && !request.getPath().isEmpty()) {
            PointDto startPoint = request.getPath().get(0);
            mainPointWkt = "POINT(" + startPoint.getLat() + " " + startPoint.getLng() + ")";
        }

        Course course = Course.builder()
                .writerId(writerId)
                .title(request.getTitle())
                .description(request.getDescription())
                .pathWkt(pathWkt)
                .mainPointWkt(mainPointWkt)
                .distance(request.getDistance())
                .expectedTime(request.getExpectedTime())
                .difficulty(request.getDifficulty())
                .thumbnail(imageUrl)
                .scrapCount(0)
                .aiKeywords("{\n" +
                        "  \"positive\": [\"경치 좋음\", \"평지임\", \"편의점 많음\"],\n" +
                        "  \"negative\": [\"사람 많음\", \"벌레 있음\"]\n" +
                        "}")
                .aiSummary("ai 임시 요약입니다.")
                .build();

        courseMapper.insertCourse(course);
        return course.getId();
    }

    // [코스] 상세 조회
    @Override
    @Transactional // 조회수 증가 로직이 들어있어서 Transactional
    public CourseGetResponse getCourseDetail(Long courseId, Long userId) {
        // 조회수 증가 (+1)
        courseMapper.increaseViewCount(courseId);
        CourseGetResponse response = courseMapper.selectCourseDetail(courseId, userId);
        if (response == null) throw new CustomException(ErrorCode.NOT_FOUND);

        // WKT String -> List<PointDto> 변환
        response.setPath(geometryUtil.convertToPath(response.getPathWkt()));

        // 사용자가 로그인했다면 스크랩 여부 확인
        if (userId != null) {
            response.setIsScrapped(courseScrapMapper.existsScrap(userId, courseId));
        }

        return response;
    }

    // [코스] 목록 조회
    @Override
    public List<CourseListResponse> getCourseList(CourseSearchCondition condition) {
        // 1. 반경 기본값 설정 (null이면 3km)
        if (condition.getRadius() == null) {
            condition.setRadius(3000);
        }

        // 2. Offset 계산해서 DTO에 set
        int calculatedOffset = Math.max(0, condition.getPage()) * condition.getSize();
        condition.setOffset(calculatedOffset);

        // 3. 매퍼 호출
        return courseMapper.selectCourseList(condition);
    }


    // [코스] 내 코스 조회
    @Override
    public List<CourseListResponse> getMyCourses(Long userId, int page, int size) {
        int offset = page * size;
        return courseMapper.selectMyCourses(userId, offset, size);
    }

    // [코스] 수정
    @Override
    @Transactional
    public void updateCourse(Long courseId, CourseUpdateRequest request, MultipartFile image, Long userId) {
        // 1. 기존 코스 조회 (작성자 확인)
        Course course = courseMapper.selectCourseById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (!course.getWriterId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        String pathWkt = geometryUtil.convertToWkt(request.getPath());

        String mainPointWkt = null;
        if (request.getPath() != null && !request.getPath().isEmpty()) {
            PointDto startPoint = request.getPath().get(0);
            mainPointWkt = "POINT(" + startPoint.getLat() + " " + startPoint.getLng() + ")";
        }

        // 2. 이미지 처리
        String newImageUrl = null;
        if (image != null && !image.isEmpty()) {
            newImageUrl = s3Service.uploadFile(image, "courses");
        }

        // 4. 업데이트 실행 (파라미터 전달)
        courseMapper.updateCourse(courseId, request, newImageUrl, pathWkt, mainPointWkt);
    }

    // [코스] 삭제
    @Override
    @Transactional
    public void deleteCourse(Long courseId, Long userId) {
        // 1. 작성자 확인
        Course course = courseMapper.selectCourseById(courseId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (!course.getWriterId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        // 2. 연관 데이터 삭제
        courseReviewMapper.deleteReviewsByCourseId(courseId); // 리뷰 전체 삭제
        courseScrapMapper.deleteScrapsByCourseId(courseId);   // 스크랩 전체 삭제

        // 3. 코스 삭제
        courseMapper.deleteCourse(courseId);
    }

    // [스크랩] 코스 스크랩
    @Transactional
    public boolean toggleScrap(Long courseId, Long userId) {
        boolean exists = courseScrapMapper.existsScrap(userId, courseId);

        if (exists) {
            // 이미 스크랩 했으면 -> 취소
            courseScrapMapper.deleteScrap(userId, courseId);
            courseScrapMapper.updateScrapCount(courseId, -1); // 카운트 감소
            return false; // 결과: 스크랩 안 된 상태
        } else {
            // 스크랩 안 했으면 -> 추가
            courseScrapMapper.insertScrap(userId, courseId);
            courseScrapMapper.updateScrapCount(courseId, 1); // 카운트 증가
            return true; // 결과: 스크랩 된 상태
        }
    }


    // [스크랩] 목록 조회
    @Override
    public List<CourseListResponse> getMyScrapCourses(Long userId, int page, int size) {
        int offset = page * size;
        return courseScrapMapper.selectMyScrapCourses(userId, offset, size);
    }



    // [리뷰] 등록
    @Transactional
    public void createReview(Long courseId, MultipartFile image, CourseReviewRequest request, Long writerId) {
        String imageUrl = uploadImage(image, "static/course/review/" +courseId);

        CourseReview review = CourseReview.builder()
                .courseId(courseId)
                .writerId(writerId)
                .content(request.getContent())
                .rating(request.getRating())
                .image(imageUrl)
                .difficultyScore(request.getDifficultyScore())
                .build();
        courseReviewMapper.insertReview(review);
        eventPublisher.publishEvent(new ReviewCreatedEvent(courseId));
    }


    // [리뷰] 목록 조회
    @Override
    public List<CourseReviewResponse> getReviewList(Long courseId, int page, int size, Long userId) {
        int offset = page * size;
        return courseReviewMapper.selectReviewList(courseId, offset, size, userId);
    }

    // [리뷰] 삭제
    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        int deleted = courseReviewMapper.deleteReview(reviewId, userId);
        if (deleted == 0) {
            throw new CustomException(ErrorCode.FORBIDDEN); // 혹은 NOT_FOUND
        }
    }

    // 이미지 업로드 헬퍼 메서드
    private String uploadImage(MultipartFile image, String dir) {
        if (image == null || image.isEmpty()) return null;
        return s3Service.uploadFile(image, dir);
    }
}
