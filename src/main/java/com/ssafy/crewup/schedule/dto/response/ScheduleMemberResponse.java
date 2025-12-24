package com.ssafy.crewup.schedule.dto.response;

import com.ssafy.crewup.enums.ScheduleMemberStatus;
import com.ssafy.crewup.schedule.ScheduleMember;
import com.ssafy.crewup.user.dto.response.UserResponse;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ScheduleMemberResponse {
    private Long id;
    private Long userId;
    private ScheduleMemberStatus status;

    //유저 정보 가져오기
    private String nickname;
    private String profileImage;

    public static ScheduleMemberResponse of(ScheduleMember member, UserResponse user) {
        return ScheduleMemberResponse.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .status(member.getStatus())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .build();
    }
}
