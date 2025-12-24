package com.ssafy.crewup.notification.event;

import com.ssafy.crewup.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class NotificationEvent {
    private Long crewId;
    private String crewName;
    private Long excludeUserId;
    private NotificationType type;
    private String content;
    private String url;
}
