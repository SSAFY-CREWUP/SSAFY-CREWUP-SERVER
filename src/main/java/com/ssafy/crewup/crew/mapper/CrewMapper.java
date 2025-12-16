package com.ssafy.crewup.crew.mapper;

import com.ssafy.crewup.crew.Crew;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface CrewMapper {
    @Select("SELECT crew_id AS id, name, region, description, crew_image AS crewImage, member_count AS memberCount, created_at AS createdAt, updated_at AS updatedAt FROM crew WHERE crew_id = #{id}")
    Crew findById(@Param("id") Long id);

    @Insert("INSERT INTO crew(name, region, description, crew_image, member_count) VALUES(#{name}, #{region}, #{description}, #{crewImage}, #{memberCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "crew_id")
    int insert(Crew crew);

    @Update("UPDATE crew SET name=#{name}, region=#{region}, description=#{description}, crew_image=#{crewImage}, member_count=#{memberCount} WHERE crew_id=#{id}")
    int update(Crew crew);

    @Delete("DELETE FROM crew WHERE crew_id = #{id}")
    int delete(@Param("id") Long id);
}
