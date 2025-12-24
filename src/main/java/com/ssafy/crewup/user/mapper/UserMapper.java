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

    // 기본 정보만 조회 (회원가입용)
    @Select("SELECT user_id AS id, email, password, nickname, profile_image AS profileImage, " +
            "total_distance AS totalDistance, gender, birth_date AS birthDate, " +
            "average_pace AS averagePace, activity_region AS activityRegion, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM users WHERE user_id = #{id}")
    User findById(@Param("id") Long id);

    // 회원가입 (기본 정보만)
    @Insert("INSERT INTO users(email, password, nickname, profile_image, total_distance) " +
            "VALUES(#{email}, #{password}, #{nickname}, #{profileImage}, #{totalDistance})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "user_id")
    int insert(User user);

    // 기본 정보 수정
    @Update("UPDATE users SET email=#{email}, password=#{password}, nickname=#{nickname}, " +
            "profile_image=#{profileImage}, total_distance=#{totalDistance} " +
            "WHERE user_id=#{id}")
    int update(User user);

    // 추가 정보 수정 (신규)
    @Update("UPDATE users SET gender=#{gender}, birth_date=#{birthDate}, " +
            "average_pace=#{averagePace}, activity_region=#{activityRegion} " +
            "WHERE user_id=#{id}")
    int updateAdditionalInfo(User user);

    @Delete("DELETE FROM users WHERE user_id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(@Param("email") String email);

    @Select("SELECT user_id AS id, email, password, nickname, profile_image AS profileImage, " +
            "total_distance AS totalDistance, gender, birth_date AS birthDate, " +
            "average_pace AS averagePace, activity_region AS activityRegion, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    // 여러 사용자를 한 번에 조회
    @Select("<script>" +
            "SELECT user_id AS id, email, nickname, profile_image AS profileImage, " +
            "total_distance AS totalDistance, gender, birth_date AS birthDate, " +
            "average_pace AS averagePace, activity_region AS activityRegion, " +
            "created_at AS createdAt, updated_at AS updatedAt " +
            "FROM users " +
            "WHERE user_id IN " +
            "<foreach item='id' collection='userIds' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<User> findByIds(@Param("userIds") List<Long> userIds);
}