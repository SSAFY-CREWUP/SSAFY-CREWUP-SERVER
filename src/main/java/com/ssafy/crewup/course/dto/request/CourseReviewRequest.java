package com.ssafy.crewup.course.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseReviewRequest {
    private String content;
    private Integer rating;
}
