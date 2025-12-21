package com.ssafy.crewup.schedule;

import com.ssafy.crewup.global.common.code.BaseTime;
import com.ssafy.crewup.enums.ScheduleMemberStatus;
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
public class ScheduleMember extends BaseTime {
    private Long id;              // schedule_member.id
    private Long scheduleId;      // schedule.schedule_id
    private Long userId;          // users.user_id
    private ScheduleMemberStatus status; // ENUM
    private LocalDateTime attendedAt;
}
