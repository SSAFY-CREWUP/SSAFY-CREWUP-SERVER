package com.ssafy.crewup.notification.event;

import com.ssafy.crewup.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 개인 알림 이벤트
 */
@Getter
@AllArgsConstructor
@Builder
public class PersonalNotificationEvent {
    private Long userId;          // 알림 받을 사용자 ID
    private Long crewId;
    private String crewName;
    private NotificationType type;
    private String content;
    private String url;
}
