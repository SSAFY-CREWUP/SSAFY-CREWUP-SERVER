package com.ssafy.crewup.global.util;

import com.ssafy.crewup.course.dto.common.PointDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class GeometryUtil {

    // List -> WKT (LINESTRING)
    public String convertToWkt(List<PointDto> path) {
        if (path == null || path.isEmpty()) return null;

        return "LINESTRING(" + path.stream()
                .map(p -> p.getLat() + " " + p.getLng())
                .collect(Collectors.joining(",")) + ")";
    }

    // WKT -> List
    public List<PointDto> convertToPath(String wkt) {
        List<PointDto> path = new ArrayList<>();
        if (wkt == null || wkt.isEmpty()) return path;

        Pattern pattern = Pattern.compile("([0-9.]+) ([0-9.]+)");
        Matcher matcher = pattern.matcher(wkt);

        while (matcher.find()) {
            Double lat = Double.parseDouble(matcher.group(1)); // 앞: 위도
            Double lng = Double.parseDouble(matcher.group(2)); // 뒤: 경도
            path.add(new PointDto(lat, lng));
        }
        return path;
    }
}
