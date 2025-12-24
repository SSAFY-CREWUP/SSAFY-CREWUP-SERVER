package com.ssafy.crewup.course.dto.request;

import com.ssafy.crewup.course.dto.common.PointDto;
import com.ssafy.crewup.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCreateRequest {
    private String title;
    private String description;

    // [{lat:37.1, lng:127.1}, ...]
    private List<PointDto> path;

    private Integer distance;
    private Integer expectedTime;
    private Difficulty difficulty; // "EASY", "MEDIUM", "HARD"
}
