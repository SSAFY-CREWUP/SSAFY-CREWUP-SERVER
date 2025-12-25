package com.ssafy.crewup.course.dto.response;

import com.ssafy.crewup.enums.Difficulty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseListResponse {
    private Long courseId;
    private String title;
    private String thumbnail;       // 썸네일 이미지 URL
    private Integer distance;       // m 단위
    private Integer expectedTime;   // 분 단위
    private Difficulty difficulty;      // EASY, NORMAL, HARD
    private Integer scrapCount;     // 스크랩 수
    private Double currentDistance;  // current_distance

    private Boolean isScrapped; // 로그인한 유저 기준
    private Integer viewCount;
    private String mainPointWkt;
}
