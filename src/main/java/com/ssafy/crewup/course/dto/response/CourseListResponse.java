package com.ssafy.crewup.course.dto.response;

import lombok.*;

@Getter
@Setter // MyBatis ResultMap에서 setter 쓸 수도 있으니 열어둠
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseListResponse {
    private Long courseId;
    private String title;
    private String thumbnail;       // 썸네일 이미지 URL
    private Integer distance;       // m 단위
    private Integer expectedTime;   // 분 단위
    private String difficulty;      // EASY, NORMAL, HARD
    private Integer scrapCount;     // 스크랩 수

    private String mainPointWkt;
}
