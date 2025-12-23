package com.ssafy.crewup.crew.service;

import java.util.List;

import com.ssafy.crewup.crew.dto.request.CrewCreateRequest;
import com.ssafy.crewup.crew.dto.request.CrewSearchRequest;
import com.ssafy.crewup.crew.dto.response.CrewListResponse;

public interface CrewService {

	Long createCrew(CrewCreateRequest request, String imageUrl, Long userId);

	List<CrewListResponse> searchCrews(CrewSearchRequest request);
}
