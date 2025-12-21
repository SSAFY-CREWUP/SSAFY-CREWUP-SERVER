package com.ssafy.crewup.crew;

import com.ssafy.crewup.global.common.code.BaseTime;
import com.ssafy.crewup.enums.CrewMemberRole;
import com.ssafy.crewup.enums.CrewMemberStatus;
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
public class CrewMember extends BaseTime {
    private Long id;              // crew_member.id
    private Long crewId;          // FK -> crew.crew_id
    private Long userId;          // FK -> users.user_id
    private CrewMemberRole role;  // ENUM
    private CrewMemberStatus status; // ENUM
    private LocalDateTime appliedAt;
    private LocalDateTime joinedAt;
}
