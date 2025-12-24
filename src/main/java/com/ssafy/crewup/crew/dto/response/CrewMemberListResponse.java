package com.ssafy.crewup.crew.dto.response;

import com.ssafy.crewup.crew.CrewMember;
import com.ssafy.crewup.enums.CrewMemberRole;
import com.ssafy.crewup.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CrewMemberListResponse {
    private Long memberId;              // crew_member.id
    private Long userId;                // user.id
    private String profileImage;        // 프로필 이미지
    private String nickname;            // 멤버 이름
    private CrewMemberRole role;        // 권한 (LEADER, MANAGER, MEMBER)
    private Integer totalDistance;      // 총 거리
    private String averagePace;         // 평균 페이스
    private LocalDateTime joinedAt;     // 가입일

    /**
     * CrewMember와 User 정보를 합쳐서 응답 DTO 생성
     */
    public static CrewMemberListResponse of(CrewMember crewMember, User user) {
        return CrewMemberListResponse.builder()
                .memberId(crewMember.getId())
                .userId(user.getId())
                .profileImage(user.getProfileImage())
                .nickname(user.getNickname())
                .role(crewMember.getRole())
                .totalDistance(user.getTotalDistance())
                .averagePace(user.getAveragePace())
                .joinedAt(crewMember.getJoinedAt())
                .build();
    }
}
