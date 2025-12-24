package com.ssafy.crewup.schedule;

import com.ssafy.crewup.enums.ScheduleType;
import com.ssafy.crewup.global.common.code.BaseTime;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule extends BaseTime {
    private Long id;            // schedule.schedule_id
    private Long crewId;        // crew.crew_id
    private Long courseId;      // course.course_id
    private String title;
    private LocalDateTime runDate;
    private String location;
    private Integer maxPeople;
    private String content;
    private ScheduleType scheduleType;
}
