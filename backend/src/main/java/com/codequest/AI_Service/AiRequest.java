package com.codequest.AI_Service;

import lombok.Builder;

@Builder
public record AiRequest(
        Long levelId,
        String language,
        String userCode,
        String errorMessage,
        String stdout,
        String mode
) {}
