package com.ssafy.crewup.notification.service.impl;

import com.ssafy.crewup.crew.mapper.CrewMemberMapper;
import com.ssafy.crewup.enums.NotificationType;
import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.notification.Notification;
import com.ssafy.crewup.notification.dto.response.NotificationResponse;
import com.ssafy.crewup.notification.mapper.NotificationMapper;
import com.ssafy.crewup.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final CrewMemberMapper crewMemberMapper;

    private static final int MAX_NOTIFICATIONS = 20;

    @Override
    public List<NotificationResponse> getNotifications(Long userId, int limit) {
        log.debug("알림 조회 - userId: {}, limit: {}", userId, limit);

        List<Notification> notifications = notificationMapper.findByUserId(userId, limit);

        return notifications.stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public int getUnreadCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        log.debug("알림 읽음 처리 - notificationId: {}, userId: {}", notificationId, userId);

        Notification notification = notificationMapper.findById(notificationId);

        if (notification == null) {
            throw new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }

        if (!notification.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        notificationMapper.markAsRead(notificationId, userId);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        log.debug("모든 알림 읽음 처리 - userId: {}", userId);

        int updated = notificationMapper.markAllAsRead(userId);

        log.info("알림 읽음 처리 완료 - userId: {}, count: {}", userId, updated);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        log.debug("알림 삭제 - notificationId: {}, userId: {}", notificationId, userId);

        Notification notification = notificationMapper.findById(notificationId);

        if (notification == null) {
            throw new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }

        if (!notification.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        notificationMapper.delete(notificationId, userId);

        log.info("알림 삭제 완료 - notificationId: {}", notificationId);
    }

    /**
     * 크루 멤버들에게 알림 생성 (비동기 처리)
     */
    @Async("notificationExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createNotificationForCrewMembers(Long crewId, String crewName, Long excludeUserId,
                                                 NotificationType type, String content, String url) {
        try {
            log.info("알림 생성 시작 - crewId: {}, crewName: {}, type: {}", crewId, crewName, type);

            // 1. 크루 멤버 ID 조회
            List<Long> memberIds = crewMemberMapper.findMemberIdsByCrewId(crewId);

            // 2. 본인 제외
            if (excludeUserId != null) {
                memberIds = memberIds.stream()
                        .filter(id -> !id.equals(excludeUserId))
                        .collect(Collectors.toList());
            }

            if (memberIds.isEmpty()) {
                log.info("알림 대상자 없음 - crewId: {}", crewId);
                return;
            }

            // 3. 알림 객체 생성
            List<Notification> notifications = memberIds.stream()
                    .map(memberId -> Notification.builder()
                            .userId(memberId)
                            .crewId(crewId)
                            .crewName(crewName)
                            .content(content)
                            .url(url)
                            .type(type)
                            .isRead(false)
                            .build())
                    .collect(Collectors.toList());

            // 4. Bulk Insert
            notificationMapper.insertBatch(notifications);

            // 5. 각 사용자별 오래된 알림 삭제 (20개 초과 시)
            for (Long memberId : memberIds) {
                deleteOldNotifications(memberId);
            }

            log.info("알림 생성 완료 - crewId: {}, crewName: {}, 대상: {}명, type: {}",
                    crewId, crewName, memberIds.size(), type);

        } catch (Exception e) {
            log.error("알림 생성 실패 - crewId: {}, crewName: {}, type: {}", crewId, crewName, type, e);
        }
    }

    /**
     * ⭐ 특정 사용자에게 개인 알림 생성 (비동기 처리)
     */
    @Async("notificationExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void createNotificationForUser(Long userId, Long crewId, String crewName,
                                          NotificationType type, String content, String url) {
        try {
            log.info("개인 알림 생성 시작 - userId: {}, crewId: {}, type: {}", userId, crewId, type);

            // 알림 생성
            Notification notification = Notification.builder()
                    .userId(userId)
                    .crewId(crewId)
                    .crewName(crewName)
                    .content(content)
                    .url(url)
                    .type(type)
                    .isRead(false)
                    .build();

            notificationMapper.insert(notification);

            // 오래된 알림 삭제
            deleteOldNotifications(userId);

            log.info("개인 알림 생성 완료 - userId: {}, type: {}", userId, type);

        } catch (Exception e) {
            log.error("개인 알림 생성 실패 - userId: {}, type: {}", userId, type, e);
        }
    }

    @Override
    @Transactional
    public void deleteOldNotifications(Long userId) {
        int deleted = notificationMapper.deleteOldNotifications(userId, MAX_NOTIFICATIONS);

        if (deleted > 0) {
            log.debug("오래된 알림 삭제 - userId: {}, deleted: {}", userId, deleted);
        }
    }
}
