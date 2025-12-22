package com.ssafy.crewup.user.mapper;

import com.ssafy.crewup.user.User;
import jakarta.validation.constraints.NotBlank;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT user_id AS id, email, password, nickname, profile_image AS profileImage, total_distance AS totalDistance, created_at AS createdAt, updated_at AS updatedAt FROM users WHERE user_id = #{id}")
    User findById(@Param("id") Long id);

    @Insert("INSERT INTO users(email, password, nickname, profile_image, total_distance) VALUES(#{email}, #{password}, #{nickname}, #{profileImage}, #{totalDistance})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "user_id")
    int insert(User user);

    @Update("UPDATE users SET email=#{email}, password=#{password}, nickname=#{nickname}, profile_image=#{profileImage}, total_distance=#{totalDistance} WHERE user_id=#{id}")
    int update(User user);

    @Delete("DELETE FROM users WHERE user_id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(@Param("email") String email);

    @Select("SELECT user_id AS id, email, password, nickname, profile_image AS profileImage, total_distance AS totalDistance, created_at AS createdAt, updated_at AS updatedAt FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    // 여러 사용자를 한 번에 조회 (추가!)
    @Select("<script>" +
            "SELECT user_id AS id, email, nickname, profile_image AS profileImage, " +
            "total_distance AS totalDistance, created_at AS createdAt, updated_at AS updatedAt " +
            "FROM users " +
            "WHERE user_id IN " +
            "<foreach item='id' collection='userIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<User> findByIds(@Param("userIds") List<Long> userIds);
}

