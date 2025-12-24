package com.ssafy.crewup.notification.controller;

import com.ssafy.crewup.global.common.code.ErrorCode;
import com.ssafy.crewup.global.common.code.SuccessCode;
import com.ssafy.crewup.global.common.dto.ApiResponseBody;
import com.ssafy.crewup.global.common.exception.CustomException;
import com.ssafy.crewup.notification.dto.response.NotificationResponse;
import com.ssafy.crewup.notification.dto.response.UnreadCountResponse;
import com.ssafy.crewup.notification.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    private static final int DEFAULT_LIMIT = 20;  // 기본 조회 개수

    /**
     * 알림 목록 조회 (최근 20개)
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponseBody<List<NotificationResponse>>> getNotifications(
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        List<NotificationResponse> notifications = notificationService.getNotifications(userId, DEFAULT_LIMIT);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.NOTIFICATION_LIST_SUCCESS, notifications)
        );
    }

    /**
     * 안읽은 알림 개수 조회
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponseBody<UnreadCountResponse>> getUnreadCount(
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        int unreadCount = notificationService.getUnreadCount(userId);
        UnreadCountResponse response = UnreadCountResponse.of(unreadCount);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.NOTIFICATION_UNREAD_COUNT_SUCCESS, response)
        );
    }

    /**
     * 알림 읽음 처리
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponseBody<Void>> markAsRead(
            @PathVariable Long notificationId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        notificationService.markAsRead(notificationId, userId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.NOTIFICATION_READ_SUCCESS)
        );
    }

    /**
     * 모두 읽음 처리
     */
    @PutMapping("/read-all")
    public ResponseEntity<ApiResponseBody<Void>> markAllAsRead(
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.NOTIFICATION_READ_ALL_SUCCESS)
        );
    }

    /**
     * 알림 삭제
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponseBody<Void>> deleteNotification(
            @PathVariable Long notificationId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        notificationService.deleteNotification(notificationId, userId);

        return ResponseEntity.ok(
                ApiResponseBody.onSuccess(SuccessCode.NOTIFICATION_DELETE_SUCCESS)
        );
    }
}
