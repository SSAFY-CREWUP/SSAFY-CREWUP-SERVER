package com.ssafy.crewup.course.dto.request;

import com.ssafy.crewup.enums.Difficulty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CourseSearchCondition {
    private Long userId;
    private String keyword;
    private Difficulty difficulty;

    private Double lat;
    private Double lng;
    private Integer radius;

    private String sort;
    private int page;
    private int size;

    private int offset;
}
