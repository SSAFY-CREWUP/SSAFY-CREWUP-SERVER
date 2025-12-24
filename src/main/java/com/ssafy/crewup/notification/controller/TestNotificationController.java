package com.ssafy.crewup.notification.controller;

import com.ssafy.crewup.crew.Crew;
import com.ssafy.crewup.crew.mapper.CrewMapper;
import com.ssafy.crewup.enums.NotificationType;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.notification.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/test/notifications")
@RequiredArgsConstructor
public class TestNotificationController {

    private final ApplicationEventPublisher eventPublisher;
    private final CrewMapper crewMapper;

    @PostMapping("/send")
    public ResponseEntity<String> sendTestNotification(
            @RequestParam Long crewId,
            @RequestParam(defaultValue = "테스트 알림입니다") String message,
            @RequestParam(defaultValue = "NOTICE") NotificationType type) {

        log.info("테스트 알림 발송 요청 - crewId: {}, message: {}, type: {}", crewId, message, type);

        // 크루 조회
        Crew crew = crewMapper.findById(crewId);

        // ⭐ 이 로그가 나오는지 확인
        log.info("크루 조회 결과 - crew: {}", crew);

        if (crew == null) {
            log.error("크루를 찾을 수 없음 - crewId: {}", crewId);
            throw new CustomException(ErrorCode.CREW_NOT_FOUND);
        }

        log.info("크루 정보 - crewId: {}, crewName: {}", crew.getId(), crew.getName());

        // 알림 이벤트 발행
        String content = String.format("[%s] %s", crew.getName(), message);

        NotificationEvent event = NotificationEvent.builder()
                .crewId(crewId)
                .crewName(crew.getName())
                .excludeUserId(null)
                .type(type)
                .content(content)
                .url("/crew/" + crewId)
                .build();

        log.info("알림 이벤트 발행 직전 - content: {}", content);

        eventPublisher.publishEvent(event);

        log.info("알림 이벤트 발행 완료");

        return ResponseEntity.ok("✅ 알림 발송 완료: " + content);
    }
}
