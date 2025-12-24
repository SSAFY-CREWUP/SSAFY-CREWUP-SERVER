package com.ssafy.crewup.schedule.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScheduleJoinRequest {
    @NotNull
    private Long scheduleId;
}
