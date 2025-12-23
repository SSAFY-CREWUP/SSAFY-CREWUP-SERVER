package com.ssafy.crewup.schedule.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssafy.crewup.enums.ScheduleType;
import com.ssafy.crewup.schedule.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleCreateRequest {
    // crewId는 PathVariable로 받으므로 DTO에서 제거
    private Long courseId;

    @NotBlank
    private String title;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime runDate;

    @NotBlank
    private String location;

    @NotNull
    private Integer maxPeople;

    @NotBlank
    private String content;

    @NotNull
    private ScheduleType scheduleType;

    // crewId를 파라미터로 받아서 Entity 생성
    public Schedule toEntity(Long crewId) {
        return Schedule.builder()
                .crewId(crewId)
                .courseId(this.courseId)
                .title(this.title)
                .runDate(this.runDate)
                .location(this.location)
                .maxPeople(this.maxPeople)
                .content(this.content)
                .scheduleType(this.scheduleType)
                .build();
    }

}
