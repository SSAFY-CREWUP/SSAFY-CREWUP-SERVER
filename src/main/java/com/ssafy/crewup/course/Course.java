package com.ssafy.crewup.course;

import com.ssafy.crewup.global.common.BaseTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends BaseTime {
    private Long id;                 // course.course_id
    private Long writerId;           // users.user_id
    private String title;
    private String description;      // TEXT
    private String pathWkt;          // LINESTRING as WKT (SRID 4326)
    private String mainPointWkt;     // POINT as WKT (SRID 4326)
    private Integer distance;
    private Integer scrapCount;
    private String thumbnail;        // TEXT
    private String aiSummary;
    private String aiKeywordsJson;   // JSON stored as String
}
