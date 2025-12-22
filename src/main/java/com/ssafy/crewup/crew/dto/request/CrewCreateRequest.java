package com.ssafy.crewup.crew.dto.request;

import java.util.List;

public record CrewCreateRequest(
    String name,
    String region,
    String description,
    String activityTime,
    String ageGroup,
    String genderLimit,
    String crewImage,
    List<String> keywords
) {}
