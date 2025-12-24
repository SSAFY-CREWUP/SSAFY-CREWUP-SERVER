package com.ssafy.crewup.crew.dto.request;

import com.ssafy.crewup.enums.CrewMemberStatus;
import jakarta.validation.constraints.NotNull;

public record CrewMemberStatusUpdateRequest(
        @NotNull(message = "상태는 필수입니다.")
        CrewMemberStatus status
) {}