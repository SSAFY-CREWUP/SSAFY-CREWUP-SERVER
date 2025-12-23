package com.ssafy.crewup.schedule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ScheduleCreatorCheckResponse {

    private Boolean isCreator;  // 생성자 여부

    public static ScheduleCreatorCheckResponse of(Boolean isCreator) {
        return ScheduleCreatorCheckResponse.builder()
                .isCreator(isCreator)
                .build();
    }
}