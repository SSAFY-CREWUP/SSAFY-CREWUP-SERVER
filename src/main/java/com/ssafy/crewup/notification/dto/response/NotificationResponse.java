package com.ssafy.crewup.notification.dto.response;

import com.ssafy.crewup.enums.NotificationType;
import com.ssafy.crewup.global.util.TimeUtils;
import com.ssafy.crewup.notification.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long crewId;
    private String crewName;              // 크루명 추가 ⭐
    private String content;
    private String url;
    private Boolean isRead;
    private NotificationType type;
    private String relativeTime;          // "방금 전", "5분 전" 등
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .crewId(notification.getCrewId())
                .crewName(notification.getCrewName())    // ⭐
                .content(notification.getContent())
                .url(notification.getUrl())
                .isRead(notification.getIsRead())
                .type(notification.getType())
                .relativeTime(TimeUtils.getRelativeTimeString(notification.getCreatedAt()))
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
