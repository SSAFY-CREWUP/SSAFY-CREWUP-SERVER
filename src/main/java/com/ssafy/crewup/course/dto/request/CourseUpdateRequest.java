package com.ssafy.crewup.course.dto.request;

import com.ssafy.crewup.enums.Difficulty;
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
    private Difficulty difficulty; // EASY, NORMAL, HARD
}
