package com.ssafy.crewup.course.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseSearchCondition {
    private String keyword;
    private String difficulty;

    private Double lat;
    private Double lng;
    private Integer radius;

    private String sort;
    private int page;
    private int size;

    private int offset;
}
