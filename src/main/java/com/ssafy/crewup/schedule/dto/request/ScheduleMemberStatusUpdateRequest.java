package com.ssafy.crewup.schedule.dto.request;

import com.ssafy.crewup.enums.ScheduleMemberStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScheduleMemberStatusUpdateRequest {

    @NotNull
    private Long scheduleMemberId;

    @NotNull
    private ScheduleMemberStatus status;
}
