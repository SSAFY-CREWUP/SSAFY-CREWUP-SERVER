package com.ssafy.crewup.course;

import com.ssafy.crewup.global.common.code.BaseTime;
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
public class CourseReview extends BaseTime {
    private Long id;         // course_review.review_id
    private Long courseId;   // course.course_id
    private Long writerId;   // users.user_id
    private String content;  // TEXT
    private Integer rating;  // 1~5
    private String image;    // TEXT
    private Integer difficultyScore; // 1~5
}
