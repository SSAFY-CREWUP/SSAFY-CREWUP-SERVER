package com.ssafy.crewup.course.dto.response;

import com.ssafy.crewup.course.dto.common.PointDto;
import com.ssafy.crewup.course.dto.common.WriterDto;
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

    // DB의 WKT String -> List<PointDto> 로 변환
    private List<PointDto> path;

    private Integer distance;
    private Integer expectedTime;
    private String difficulty;
    private Integer scrapCount;
    private String thumbnail;
    private String aiSummary;
    private List<String> aiKeywords; // DB의 JSON String -> List 변환
    private String pathWkt;          // DB: path (LINESTRING)

    // 작성자 정보
    private WriterDto writer;

    private Boolean isScrapped; // 로그인한 유저 기준
    private String createdAt;
}
