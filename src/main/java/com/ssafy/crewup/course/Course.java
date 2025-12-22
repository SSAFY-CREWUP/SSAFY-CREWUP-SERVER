package com.ssafy.crewup.course;

import com.ssafy.crewup.global.common.code.BaseTime;
import lombok.*;

@Getter
@Setter // MyBatis 매핑 위해 Setter 필요
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends BaseTime {
    private Long id;           // DB: course_id
    private Long writerId;           // DB: writer_id
    private String title;
    private String description;

    // DB는 GEOMETRY 타입이지만, 자바 객체에선 WKT 문자열로 들고 있음
    private String pathWkt;          // DB: path (LINESTRING)
    private String mainPointWkt;     // DB: main_point (POINT)

    private Integer distance;
    private Integer expectedTime;    // ALTER로 추가함
    private String difficulty;       // ALTER로 추가함

    private Integer scrapCount;
    private String thumbnail;
    private String aiSummary;
    private String aiKeywords;       // DB: ai_keywords (JSON) - String으로 받음
}
