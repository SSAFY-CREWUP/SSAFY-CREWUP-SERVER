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
    private String image;
    private LocalDateTime createdAt;

    // 내 리뷰인지 여부 (프론트에서 삭제 버튼 보여줄 때 필요)
    private Boolean isMyReview;
}
