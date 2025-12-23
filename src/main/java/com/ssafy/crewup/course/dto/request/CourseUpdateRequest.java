package com.ssafy.crewup.course.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseUpdateRequest {
    private String title;
    private String description;
    private Integer distance;
    private Integer expectedTime;
    private String difficulty; // EASY, NORMAL, HARD
}
