package com.ssafy.crewup.crew.service;

import com.ssafy.crewup.crew.dto.request.CrewCreateRequest;

public interface CrewService {
    Long createCrew(CrewCreateRequest request, Long userId);
}
