package com.ssafy.crewup.user.dto.response;

import com.ssafy.crewup.user.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserGetResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImage;
    private Integer totalDistance;
    private LocalDateTime createdAt;

    // Entity -> DTO 변환
    public static UserGetResponse from(User user) {
        return UserGetResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .totalDistance(user.getTotalDistance())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now())
                .build();
    }

}
