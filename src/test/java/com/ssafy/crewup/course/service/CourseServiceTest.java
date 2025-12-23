//package com.ssafy.crewup.course.service;
//
//import com.ssafy.crewup.course.Course;
//import com.ssafy.crewup.course.dto.common.PointDto;
//import com.ssafy.crewup.course.dto.request.CourseCreateRequest;
//import com.ssafy.crewup.course.dto.response.CourseGetResponse;
//import com.ssafy.crewup.course.mapper.CourseMapper;
//import com.ssafy.crewup.course.service.impl.CourseServiceImpl;
//import com.ssafy.crewup.global.common.exception.CustomException;
//import com.ssafy.crewup.global.service.S3Service;
//import com.ssafy.crewup.global.util.GeometryUtil;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mock.web.MockMultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//class CourseServiceTest {
//
//    @InjectMocks
//    private CourseServiceImpl courseService; // 테스트할 진짜 녀석
//
//    @Mock
//    private CourseMapper courseMapper;       // 가짜 1
//    @Mock
//    private S3Service S3Service;           // 가짜 2 (실제 S3 안 감)
//    @Mock
//    private GeometryUtil geometryUtil;       // 가짜 3
//
//    @Test
//    @DisplayName("코스 등록 성공 테스트")
//    void createCourseSuccess() throws IOException {
//        // given
//        CourseCreateRequest request = new CourseCreateRequest(
//                "서비스 테스트 코스", "설명", List.of(new PointDto(37.1, 127.1)),
//                3000, 20, "EASY"
//        );
//        MockMultipartFile image = new MockMultipartFile("image", "test.jpg", "image/jpeg", "dummy".getBytes());
//
//        // upload 호출되면 그냥 URL 문자열 리턴
//        given(S3Service.uploadFile(any(), anyString())).willReturn("https://s3.url/test.jpg");
//        // 변환 요청오면 문자열 return
//        given(geometryUtil.convertToWkt(any())).willReturn("LINESTRING(...)");
//
//        // when (진짜 로직 실행)
//        Long writerId = 1L;
//        courseService.createCourse(request, image, writerId);
//
//        // then (검증)
//        // mapper.insertCourse()가 딱 1번 호출되었는지 확인
//        verify(courseMapper, times(1)).insertCourse(any(Course.class));
//    }
//
//    @Test
//    @DisplayName("코스 상세 조회 - 성공")
//    void getCourseDetailSuccess() {
//        // given
//        Long courseId = 1L;
//        CourseGetResponse mockResponse = new CourseGetResponse();
//        mockResponse.setPathWkt("LINESTRING(...)"); // WKT 설정
//
//        // Mapper는 조회하면 이 객체 리턴
//        given(courseMapper.selectCourseDetail(courseId)).willReturn(mockResponse);
//
//        // when
//        CourseGetResponse result = courseService.getCourseDetail(courseId, 1L);
//
//        // then
//        assertThat(result).isNotNull();
//        // GeometryUtil이 호출되었는지 확인
//        verify(geometryUtil, times(1)).convertToPath(anyString());
//    }
//
//    @Test
//    @DisplayName("코스 상세 조회 - 실패 (코스 없음)")
//    void getCourseDetailNotFound() {
//        // given
//        Long courseId = 999L;
//        // Mapper는 조회하면 null 반환
//        given(courseMapper.selectCourseDetail(courseId)).willReturn(null);
//
//        // when & then
//        // 예외가 발생하는지 확인
//        assertThatThrownBy(() -> courseService.getCourseDetail(courseId, 1L))
//                .isInstanceOf(CustomException.class);
//    }
//}
