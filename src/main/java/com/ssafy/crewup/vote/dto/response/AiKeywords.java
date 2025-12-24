package com.ssafy.crewup.vote.dto.response;

import java.util.List;

public record AiKeywords(
        List<String> positive,
        List<String> negative
) {}
