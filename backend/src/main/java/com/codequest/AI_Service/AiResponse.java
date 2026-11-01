package com.codequest.AI_Service;

import lombok.Builder;

@Builder
public record AiResponse(
        boolean success,
        String message,
        String explanation,
        String suggestedCode,
        String conceptTitle,
        String conceptSummary,
        String error
) {}
