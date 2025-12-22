package com.ssafy.crewup.crew.controller;

import com.ssafy.crewup.crew.dto.request.CrewCreateRequest;
import com.ssafy.crewup.crew.dto.response.CrewCreateResponse;
import com.ssafy.crewup.crew.service.CrewService;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.code.SuccessCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.global.service.S3Service;
import jakarta.servlet.http.HttpSession;
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
    public ResponseEntity<ApiResponseBody<CrewCreateResponse>> createCrewWithImage(
        @RequestPart("request") CrewCreateRequest request,
        @RequestPart(value = "crewImage", required = false) MultipartFile crewImage,
        HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        CrewCreateRequest effective = request;
        if (crewImage != null && !crewImage.isEmpty()) {
            String url = s3Service.uploadFile(crewImage, "crews");
            effective = new CrewCreateRequest(
                request.name(),
                request.region(),
                request.description(),
                request.activityTime(),
                request.ageGroup(),
                request.genderLimit(),
                url,
                request.keywords()
            );
        }

        Long crewId = crewService.createCrew(effective, userId);
        return ResponseEntity.ok(
            ApiResponseBody.onSuccess(SuccessCode.CREATED, new CrewCreateResponse(crewId))
        );
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseBody<CrewCreateResponse>> createCrew(
        @RequestBody CrewCreateRequest request,
        HttpSession session
    ) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        Long crewId = crewService.createCrew(request, userId);
        return ResponseEntity.ok(
            ApiResponseBody.onSuccess(SuccessCode.CREATED, new CrewCreateResponse(crewId))
        );
    }
}
