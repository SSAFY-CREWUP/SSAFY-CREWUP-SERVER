package com.ssafy.crewup.notification.mapper;

import com.ssafy.crewup.notification.Notification;
import java.util.List;
import org.apache.ibatis.annotations.*;

@Mapper
public interface NotificationMapper {
    @Select("SELECT id, user_id AS userId, content, url, is_read AS isRead, type, created_at AS createdAt, updated_at AS updatedAt FROM notification WHERE id = #{id}")
    Notification findById(@Param("id") Long id);

    @Select("SELECT id, user_id AS userId, content, url, is_read AS isRead, type, created_at AS createdAt, updated_at AS updatedAt FROM notification WHERE user_id = #{userId} ORDER BY id DESC")
    List<Notification> findByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO notification(user_id, content, url, is_read, type) VALUES(#{userId}, #{content}, #{url}, #{isRead}, #{type})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(Notification notification);

    @Update("UPDATE notification SET content=#{content}, url=#{url}, is_read=#{isRead}, type=#{type} WHERE id=#{id}")
    int update(Notification notification);

    @Delete("DELETE FROM notification WHERE id = #{id}")
    int delete(@Param("id") Long id);
}
