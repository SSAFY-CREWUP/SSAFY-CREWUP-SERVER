package com.ssafy.crewup.course.dto.request;

import com.ssafy.crewup.course.dto.common.PointDto;
import com.ssafy.crewup.enums.Difficulty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseUpdateRequest {
    private String title;
    private String description;
    private List<PointDto> path;
    private Integer distance;
    private Integer expectedTime;
    private Difficulty difficulty; // "EASY", "MEDIUM", "HARD"
}
