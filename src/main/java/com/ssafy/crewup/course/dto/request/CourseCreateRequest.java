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

    // 프론트: [{lat:37.1, lng:127.1}, ...] 이렇게 줌
    private List<PointDto> path;

    private Integer distance;
    private Integer expectedTime;
    private Difficulty difficulty;

    // 썸네일은 별도 MultipartFile로 받아서 URL로 변환 후,
    // 서비스 로직에서 Entity 만들 때 setThumbnail()로 넣어줄 거임.
}
