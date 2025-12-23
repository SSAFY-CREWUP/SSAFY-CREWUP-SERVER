package com.ssafy.crewup.global.config;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@MappedTypes(List.class)
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {

	// Java List -> DB String (INSERT/UPDATE 시 호출)
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
		// 리스트 요소를 쉼표(,)로 결합하여 하나의 문자열로 변환
		String joinedString = parameter.stream()
			.filter(s -> s != null && !s.isEmpty())
			.collect(Collectors.joining(","));
		ps.setString(i, joinedString);
	}

	// DB String -> Java List (SELECT 시 호출 - 컬럼명 기준)
	@Override
	public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return toStringList(rs.getString(columnName));
	}

	// DB String -> Java List (SELECT 시 호출 - 인덱스 기준)
	@Override
	public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return toStringList(rs.getString(columnIndex));
	}

	// DB String -> Java List (Stored Procedure 호출 시)
	@Override
	public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return toStringList(cs.getString(columnIndex));
	}

	private List<String> toStringList(String s) {
		if (s == null || s.isEmpty()) {
			return List.of(); // 빈 리스트 반환
		}
		// 쉼표를 기준으로 잘라서 리스트로 변환
		return Arrays.stream(s.split(","))
			.map(String::trim)
			.collect(Collectors.toList());
	}
}
