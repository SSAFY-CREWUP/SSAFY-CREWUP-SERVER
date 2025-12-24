package com.ssafy.crewup.crew.service;

import java.util.List;

import com.ssafy.crewup.crew.dto.request.CrewCreateRequest;
import com.ssafy.crewup.crew.dto.response.CrewDetailResponse;
import com.ssafy.crewup.crew.dto.request.CrewSearchRequest;
import com.ssafy.crewup.crew.dto.response.CrewListResponse;
import com.ssafy.crewup.crew.dto.response.CrewMemberListResponse;

public interface CrewService {

	Long createCrew(CrewCreateRequest request, String imageUrl, Long userId);

	CrewDetailResponse getCrewDetail(Long crewId);

	void joinCrew(Long crewId, Long userId);

	List<CrewListResponse> searchCrews(CrewSearchRequest request);

	List<CrewListResponse> getMyCrews(Long userId);
  
  List<CrewMemberListResponse> getCrewMemberList(Long crewId);
}
