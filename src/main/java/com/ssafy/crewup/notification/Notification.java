package com.ssafy.crewup.notification;

import com.ssafy.crewup.global.common.BaseTime;
import com.ssafy.crewup.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseTime {
    private Long id;                 // notification.id
    private Long userId;             // users.user_id
    private String content;
    private String url;
    private Boolean isRead;
    private NotificationType type;   // ENUM
}
