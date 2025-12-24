package com.ssafy.crewup.notification.scheduler;

import com.ssafy.crewup.crew.Crew;
import com.ssafy.crewup.crew.mapper.CrewMapper;
import com.ssafy.crewup.enums.NotificationType;
import com.ssafy.crewup.notification.event.NotificationEvent;
import com.ssafy.crewup.schedule.Schedule;
import com.ssafy.crewup.schedule.mapper.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final ScheduleMapper scheduleMapper;
    private final CrewMapper crewMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 일정 알림 스케줄러
     * - 매시간 정각에 실행
     * - 3시간, 6시간, 12시간 후 시작하는 일정 체크
     */
    @Scheduled(cron = "0 0 * * * *")  // 매시간 정각 (초 분 시 일 월 요일)
    public void sendScheduleReminders() {
        log.info("일정 알림 스케줄러 시작");

        LocalDateTime now = LocalDateTime.now();

        // 3시간, 6시간, 12시간 후 일정 알림 (모두 SCHEDULE 타입 사용)
        sendScheduleReminderForHour(now, 3);
        sendScheduleReminderForHour(now, 6);
        sendScheduleReminderForHour(now, 12);

        log.info("일정 알림 스케줄러 종료");
    }

    /**
     * 특정 시간 후 시작하는 일정에 대한 알림 발송
     */
    private void sendScheduleReminderForHour(LocalDateTime now, int hours) {
        // 정확히 N시간 후 시작하는 일정 조회 (±5분 오차 허용)
        LocalDateTime targetTime = now.plusHours(hours);
        LocalDateTime startRange = targetTime.minusMinutes(5);
        LocalDateTime endRange = targetTime.plusMinutes(5);

        List<Schedule> schedules = scheduleMapper.findByRunDateBetween(startRange, endRange);

        log.info("{}시간 후 일정 조회 - 대상: {}건", hours, schedules.size());

        for (Schedule schedule : schedules) {
            try {
                // 크루 정보 조회
                Crew crew = crewMapper.findById(schedule.getCrewId());

                if (crew == null) {
                    log.warn("크루를 찾을 수 없음 - crewId: {}", schedule.getCrewId());
                    continue;
                }

                // 일정명과 크루명 포함
                String content = String.format("[%s] '%s' 일정이 %d시간 후 시작됩니다.",
                        crew.getName(), schedule.getTitle(), hours);
                String url = String.format("/schedule/%d", schedule.getId());

                NotificationEvent event = NotificationEvent.builder()
                        .crewId(schedule.getCrewId())
                        .crewName(crew.getName())
                        .excludeUserId(null)  // 모든 멤버에게 알림
                        .type(NotificationType.SCHEDULE)  // ⭐ SCHEDULE 타입 사용
                        .content(content)
                        .url(url)
                        .build();

                eventPublisher.publishEvent(event);

                log.debug("일정 알림 이벤트 발행 - scheduleId: {}, hours: {}",
                        schedule.getId(), hours);

            } catch (Exception e) {
                log.error("일정 알림 발송 실패 - scheduleId: {}", schedule.getId(), e);
            }
        }
    }

    // 투표 관련 스케줄러는 투표 기능 구현 시 추가
    // 투표 기능이 없으므로 주석 처리 또는 삭제
}
