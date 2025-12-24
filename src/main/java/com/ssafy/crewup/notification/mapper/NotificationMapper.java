//package com.ssafy.crewup.notification.mapper;
//
//import com.ssafy.crewup.notification.Notification;
//import java.util.List;
//import org.apache.ibatis.annotations.*;
//
//@Mapper
//public interface NotificationMapper {
//    @Select("SELECT id, user_id AS userId, content, url, is_read AS isRead, type, created_at AS createdAt, updated_at AS updatedAt FROM notification WHERE id = #{id}")
//    Notification findById(@Param("id") Long id);
//
//    @Select("SELECT id, user_id AS userId, content, url, is_read AS isRead, type, created_at AS createdAt, updated_at AS updatedAt FROM notification WHERE user_id = #{userId} ORDER BY id DESC")
//    List<Notification> findByUserId(@Param("userId") Long userId);
//
//    @Insert("INSERT INTO notification(user_id, content, url, is_read, type) VALUES(#{userId}, #{content}, #{url}, #{isRead}, #{type})")
//    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
//    int insert(Notification notification);
//
//    @Update("UPDATE notification SET content=#{content}, url=#{url}, is_read=#{isRead}, type=#{type} WHERE id=#{id}")
//    int update(Notification notification);
//
//    @Delete("DELETE FROM notification WHERE id = #{id}")
//    int delete(@Param("id") Long id);
//}
package com.ssafy.crewup.notification.mapper;

import com.ssafy.crewup.notification.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {

    /**
     * 단일 알림 삽입
     */
    @Insert("INSERT INTO notifications(user_id, crew_id, crew_name, content, url, is_read, type, created_at, updated_at) " +
            "VALUES(#{userId}, #{crewId}, #{crewName}, #{content}, #{url}, #{isRead}, #{type}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Notification notification);

    /**
     * 벌크 알림 삽입
     */
    @Insert("<script>" +
            "INSERT INTO notifications(user_id, crew_id, crew_name, content, url, is_read, type, created_at, updated_at) " +
            "VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.userId}, #{item.crewId}, #{item.crewName}, #{item.content}, #{item.url}, #{item.isRead}, #{item.type}, NOW(), NOW())" +
            "</foreach>" +
            "</script>")
    int insertBatch(List<Notification> notifications);

    /**
     * 사용자 알림 조회 (최신순, 제한)
     */
    @Select("SELECT id, user_id AS userId, crew_id AS crewId, crew_name AS crewName, " +
            "content, url, is_read AS isRead, type, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM notifications " +
            "WHERE user_id = #{userId} " +
            "ORDER BY created_at DESC " +
            "LIMIT #{limit}")
    List<Notification> findByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 안읽은 알림 개수
     */
    @Select("SELECT COUNT(*) FROM notifications WHERE user_id = #{userId} AND is_read = false")
    int countUnread(@Param("userId") Long userId);

    /**
     * 알림 읽음 처리
     */
    @Update("UPDATE notifications SET is_read = true, updated_at = NOW() " +
            "WHERE id = #{id} AND user_id = #{userId}")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId);

    /**
     * 모두 읽음 처리
     */
    @Update("UPDATE notifications SET is_read = true, updated_at = NOW() " +
            "WHERE user_id = #{userId} AND is_read = false")
    int markAllAsRead(@Param("userId") Long userId);

    /**
     * 알림 ID로 조회
     */
    @Select("SELECT id, user_id AS userId, crew_id AS crewId, crew_name AS crewName, " +
            "content, url, is_read AS isRead, type, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM notifications WHERE id = #{id}")
    Notification findById(@Param("id") Long id);

    /**
     * 오래된 알림 삭제 (keepCount개 남기고 삭제)
     */
    @Delete("DELETE FROM notifications " +
            "WHERE user_id = #{userId} " +
            "AND id NOT IN (" +
            "  SELECT id FROM (" +
            "    SELECT id FROM notifications " +
            "    WHERE user_id = #{userId} " +
            "    ORDER BY created_at DESC " +
            "    LIMIT #{keepCount}" +
            "  ) AS temp" +
            ")")
    int deleteOldNotifications(@Param("userId") Long userId, @Param("keepCount") int keepCount);

    /**
     * 특정 알림 삭제
     */
    @Delete("DELETE FROM notifications WHERE id = #{id} AND user_id = #{userId}")
    int delete(@Param("id") Long id, @Param("userId") Long userId);
}
