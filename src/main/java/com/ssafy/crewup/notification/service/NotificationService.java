package com.ssafy.crewup.notification.service;

import com.ssafy.crewup.enums.NotificationType;
import com.ssafy.crewup.notification.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> getNotifications(Long userId, int limit);

    int getUnreadCount(Long userId);

    void markAsRead(Long notificationId, Long userId);

    void markAllAsRead(Long userId);

    void deleteNotification(Long notificationId, Long userId);

    void createNotificationForCrewMembers(Long crewId, String crewName, Long excludeUserId,
                                          NotificationType type, String content, String url);
    void deleteOldNotifications(Long userId);
    // ⭐ 개인 알림 메서드 추가
    void createNotificationForUser(Long userId, Long crewId, String crewName,
                                   NotificationType type, String content, String url);
}

