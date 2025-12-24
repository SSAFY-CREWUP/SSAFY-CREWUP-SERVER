package com.ssafy.crewup.crew.controller;

import java.util.List;

import com.ssafy.crewup.crew.dto.request.CrewCreateRequest;
import com.ssafy.crewup.crew.dto.request.CrewMemberStatusUpdateRequest;
import com.ssafy.crewup.crew.dto.request.CrewSearchRequest;
import com.ssafy.crewup.crew.dto.response.CrewCreateResponse;
import com.ssafy.crewup.crew.dto.response.CrewDetailResponse;
import com.ssafy.crewup.crew.dto.response.CrewListResponse;
import com.ssafy.crewup.crew.dto.response.CrewMemberListResponse;
import com.ssafy.crewup.crew.service.CrewService;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.code.SuccessCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.global.service.S3Service;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/crew")
@RequiredArgsConstructor
public class CrewController {

	private final CrewService crewService;
	private final S3Service s3Service;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponseBody<CrewCreateResponse>> createCrew(
		@Valid @RequestPart("request") CrewCreateRequest request,
		@RequestPart(value = "crewImage", required = false) MultipartFile crewImage,
		HttpSession session
	) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) throw new CustomException(ErrorCode.UNAUTHORIZED);

		// 1. 이미지 업로드 처리 (파일이 없으면 null 전달)
		String uploadedUrl = null;
		if (crewImage != null && !crewImage.isEmpty()) {
			uploadedUrl = s3Service.uploadFile(crewImage, "crews");
		}

		// 2. 서비스 호출
		Long crewId = crewService.createCrew(request, uploadedUrl, userId);

		return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.CREATED, new CrewCreateResponse(crewId)));
	}

	/**
	 * 크루 목록 조회 및 검색 API
	 */
	@GetMapping("/search")
	public ResponseEntity<ApiResponseBody<List<CrewListResponse>>> searchCrews(
		@ModelAttribute CrewSearchRequest request
	) {
		List<CrewListResponse> crews = crewService.searchCrews(request);
		return ResponseEntity.ok(
			ApiResponseBody.onSuccess(SuccessCode.OK, crews)
		);
	}

	@GetMapping("/{crewId}")
	public ResponseEntity<ApiResponseBody<CrewDetailResponse>> getCrewDetail(
		@PathVariable("crewId") Long crewId
	) {
		CrewDetailResponse response = crewService.getCrewDetail(crewId);
		return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.OK, response));
	}

	@PostMapping("/{crewId}/join")
	public ResponseEntity<ApiResponseBody<Void>> joinCrew(
		@PathVariable("crewId") Long crewId,
		HttpSession session
	) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) throw new CustomException(ErrorCode.UNAUTHORIZED);

		crewService.joinCrew(crewId, userId);
		return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.OK));
	}

	@GetMapping("/my")
	public ResponseEntity<ApiResponseBody<List<CrewListResponse>>> getMyCrews(
		HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null)
			throw new CustomException(ErrorCode.UNAUTHORIZED);

		List<CrewListResponse> myCrews = crewService.getMyCrews(userId);
		return ResponseEntity.ok(ApiResponseBody.onSuccess(SuccessCode.OK, myCrews));
	}
    /**
     * 크루 멤버 리스트 조회
     */
    @GetMapping("/{crewId}/members")
    public ResponseEntity<ApiResponseBody<List<CrewMemberListResponse>>> getCrewMemberList(
            @PathVariable Long crewId,
            HttpSession session) {

        // 로그인 체크
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        List<CrewMemberListResponse> members = crewService.getCrewMemberList(crewId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.OK, members)
        );
    }



    /**
     * 크루 멤버 상태 변경 (승인/거절)
     * - LEADER 또는 MANAGER만 가능
     */
    @PutMapping("/{crewId}/members/{memberId}/status")
    public ResponseEntity<ApiResponseBody<Void>> updateMemberStatus(
            @PathVariable Long crewId,
            @PathVariable Long memberId,
            @Valid @RequestBody CrewMemberStatusUpdateRequest request,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        crewService.updateMemberStatus(crewId, memberId, request.status(), userId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.OK)
        );
    }

    /**
     * 크루 가입 대기 중인 멤버 리스트 조회 (WAITING 상태)
     * - LEADER 또는 MANAGER만 조회 가능
     */
    @GetMapping("/{crewId}/members/waiting")
    public ResponseEntity<ApiResponseBody<List<CrewMemberListResponse>>> getWaitingMemberList(
            @PathVariable Long crewId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        List<CrewMemberListResponse> waitingMembers = crewService.getWaitingMemberList(crewId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.OK, waitingMembers)
        );
    }
}
