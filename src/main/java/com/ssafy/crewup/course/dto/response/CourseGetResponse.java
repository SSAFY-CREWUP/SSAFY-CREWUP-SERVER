package com.ssafy.crewup.course.dto.response;

import com.ssafy.crewup.course.dto.common.PointDto;
import com.ssafy.crewup.course.dto.common.WriterDto;
import com.ssafy.crewup.enums.Difficulty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseGetResponse {
    private Long courseId;
    private String title;
    private String description;
    private Double avgPaceSeconds;
    private Integer myPaceSeconds;

    // DB의 WKT String -> List<PointDto> 로 변환
    private List<PointDto> path;

    private Integer distance;
    private Integer expectedTime;
    private Difficulty difficulty;
    private Integer scrapCount;
    private String thumbnail;
    private String aiSummary;
    private String aiKeywords;
    private String pathWkt;          // DB: path (LINESTRING)

    // 작성자 정보
    private WriterDto writer;

    private Boolean isScrapped; // 로그인한 유저 기준
    private String createdAt;

    private Double avgDifficultyScore; // 사용자들의 평균 체감 난이도

    public String getAvgPace() {
        if (avgPaceSeconds == null || avgPaceSeconds == 0) {
            return null;
        }

        int totalSeconds = (int) Math.round(avgPaceSeconds);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        // 예: 7'18"
        return String.format("%d'%02d\"", minutes, seconds);
    }

    public String getMyPace() {
        if (myPaceSeconds == null || myPaceSeconds == 0) {
            return null; // 페이스 설정 안 한 유저
        }

        int minutes = myPaceSeconds / 60;
        int seconds = myPaceSeconds % 60;
        return String.format("%d'%02d\"", minutes, seconds);
    }

}
