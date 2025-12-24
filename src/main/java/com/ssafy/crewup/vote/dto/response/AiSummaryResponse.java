package com.ssafy.crewup.vote.dto.response;

public record AiSummaryResponse (
    String summary,
    AiKeywords keywords
) {}
