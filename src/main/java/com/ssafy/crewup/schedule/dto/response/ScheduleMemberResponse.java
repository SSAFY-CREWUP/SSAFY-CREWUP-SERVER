package com.ssafy.crewup.schedule.dto.response;

import com.ssafy.crewup.enums.ScheduleMemberStatus;
import com.ssafy.crewup.schedule.ScheduleMember;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ScheduleMemberResponse {
    private Long id;
    private Long userId;
    private ScheduleMemberStatus status;

    public static ScheduleMemberResponse from(ScheduleMember member) {
        return ScheduleMemberResponse.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .status(member.getStatus())
                .build();
    }
}
