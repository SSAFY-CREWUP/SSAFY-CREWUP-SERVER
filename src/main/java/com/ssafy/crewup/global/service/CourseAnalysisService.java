package com.ssafy.crewup.global.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.crewup.course.mapper.CourseMapper;
import com.ssafy.crewup.course.mapper.CourseReviewMapper;
import com.ssafy.crewup.vote.dto.response.AiSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseAnalysisService {

    private final ChatModel chatModel;
    private final CourseMapper courseMapper;
    private final CourseReviewMapper reviewMapper;

    public void analyzeAndUpdate(Long courseId) {

        long count = reviewMapper.countByCourseId(courseId);
        if (count == 0 || count % 5 != 0) {
            return;
        }

        try {
            List<String> reviews = reviewMapper.selectRecentReviews(courseId, 20);
            if (reviews.isEmpty()) return;

            var converter = new BeanOutputConverter<>(AiSummaryResponse.class);

            // 3. 프롬프트 입력 및 변환
            String message = """
    You are a running course analyst. Analyze the following reviews:
    {reviews}
    
    Extract a summary and keywords.
    
    Constraints for Summary:
    1. Language: Korean (한국어), Polite tone (해요체).
    2. Length: **2 to 4 sentences** (around 100~200 characters).
    3. Content: Natural flow, combining specific pros and cons.
    
    Constraints for Keywords :
    1. **Specificity:** Avoid neutral nouns. Use descriptive phrases or specific terms.
       - Bad: "길", "사람", "편의점"
       - Good: "평탄한 길", "많은 인파", "편의점 부족", "가파른 오르막"
    2. **Transformation:** Convert descriptions into professional keywords.
       - "사람이 많아요" -> "인파" or "혼잡함"
       - "길이 잘 닦였어요" -> "포장 도로" or "평탄한 길"
    3. **Exclusion:** Still exclude generic sentiment adjectives (e.g., "좋음", "최고", "추천", "아쉬움").
    4. **Quantity:** 0 to 3 keywords per category. Empty list [] is allowed.
    5. **Merge:** Group similar concepts (e.g., "벌레", "날파리" -> "벌레").
    {format}
    """;

            PromptTemplate template = new PromptTemplate(message);
            Prompt prompt = template.create(Map.of(
                    "reviews", String.join("\n", reviews),
                    "format", converter.getFormat()
            ));

            AiSummaryResponse response = converter.convert(
                    chatModel.call(prompt).getResult().getOutput().getContent()
            );

            if (response == null || response.keywords() == null) {
                log.warn("AI 응답이 올바르지 않습니다. - Course ID: {}", courseId);
                return;
            }

            String keywordsJson = convertToJson(response.keywords());

            // 4. DB 업데이트
            courseMapper.updateAiAnalysis(courseId, response.summary(), keywordsJson);

        } catch (Exception e) {
            log.error("AI 분석 중 오류 발생 - Course ID: {}", courseId, e);
        }
    }

    private String convertToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("JSON 변환 실패", e); // 여기도 로그
            throw new RuntimeException("JSON 변환 실패", e);
        }
    }
}
