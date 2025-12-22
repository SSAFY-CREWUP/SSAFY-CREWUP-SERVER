package com.ssafy.crewup.schedule.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ssafy.crewup.enums.ScheduleType;
import com.ssafy.crewup.schedule.Schedule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleCreateRequest {
    private Long id;            // schedule.schedule_id
    private Long crewId;        // crew.crew_id
    private Long courseId;// course.course_id
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

    public Schedule toEntity() {
        return Schedule.builder()
                .crewId(this.crewId)
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
