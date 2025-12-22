package com.ssafy.crewup.enums;

public enum Region {
    // Seoul
    서울_강남구, 서울_강동구, 서울_강북구, 서울_강서구, 서울_관악구,
    서울_광진구, 서울_구로구, 서울_금천구, 서울_노원구, 서울_도봉구,
    서울_동대문구, 서울_동작구, 서울_마포구, 서울_서대문구, 서울_서초구,
    서울_성동구, 서울_성북구, 서울_송파구, 서울_양천구, 서울_영등포구,
    서울_용산구, 서울_은평구, 서울_종로구, 서울_중구, 서울_중랑구,
    // Gyeonggi
    경기_고양시, 경기_과천시, 경기_광명시, 경기_구리시, 경기_군포시,
    경기_김포시, 경기_남양주시, 경기_부천시, 경기_성남시, 경기_수원시,
    경기_시흥시, 경기_안산시, 경기_안양시, 경기_용인시, 경기_의정부시,
    경기_파주시, 경기_평택시, 경기_하남시, 경기_화성시,
    // Incheon
    인천_계양구, 인천_남동구, 인천_부평구, 인천_연수구, 인천_중구, 인천_서구,
    // Others (Major Cities)
    부산_해운대구, 부산_수영구, 부산진구,
    대구_수성구, 대구_중구,
    광주_서구, 광주_동구,
    대전_유성구, 대전_서구,
    울산_남구, 울산_중구,
    세종특별자치시,
    강원_춘천시, 강원_강릉시, 강원_원주시,
    제주_제주시, 제주_서귀포시;

    public static boolean isValidLabel(String label) {
        // Accept labels with space style like "서울 강남구" by converting to enum naming
        String normalized = label.replace(' ', '_');
        for (Region r : values()) {
            if (r.name().equals(normalized)) return true;
        }
        return false;
    }
}
