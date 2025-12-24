package com.ssafy.crewup.notification.event;

import com.ssafy.crewup.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    /**
     * 크루 전체 알림 이벤트 처리
     */
    @Async("notificationExecutor")
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("알림 이벤트 수신 - crewId: {}, crewName: {}, type: {}",
                event.getCrewId(), event.getCrewName(), event.getType());

        notificationService.createNotificationForCrewMembers(
                event.getCrewId(),
                event.getCrewName(),
                event.getExcludeUserId(),
                event.getType(),
                event.getContent(),
                event.getUrl()
        );
    }

    /**
     * ⭐ 개인 알림 이벤트 처리
     */
    @Async("notificationExecutor")
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePersonalNotificationEvent(PersonalNotificationEvent event) {
        log.info("개인 알림 이벤트 수신 - userId: {}, crewId: {}, type: {}",
                event.getUserId(), event.getCrewId(), event.getType());

        notificationService.createNotificationForUser(
                event.getUserId(),
                event.getCrewId(),
                event.getCrewName(),
                event.getType(),
                event.getContent(),
                event.getUrl()
        );
    }
}
