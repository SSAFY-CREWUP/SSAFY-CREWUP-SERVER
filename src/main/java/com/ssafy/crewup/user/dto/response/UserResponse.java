package com.ssafy.crewup.user.dto.response;

import com.ssafy.crewup.enums.Gender;
import com.ssafy.crewup.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String nickname;
    private String profileImage;

    // 추가 정보
    private Gender gender;
    private LocalDate birthDate;
    private String averagePace;
    private String activityRegion;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .averagePace(user.getAveragePace())
                .activityRegion(user.getActivityRegion())
                .build();
    }
}

