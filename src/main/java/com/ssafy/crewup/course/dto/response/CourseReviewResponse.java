package com.ssafy.crewup.course.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseReviewResponse {
    private Long reviewId;
    private Long writerId;
    private String writerNickname;
    private String writerProfileImage;
    private String content;
    private Integer rating;
    private LocalDateTime createdAt;
    private String image;

    private Boolean isMyReview;
}
