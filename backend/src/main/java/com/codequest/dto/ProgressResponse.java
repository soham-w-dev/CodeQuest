package com.codequest.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProgressResponse(
        Long levelId,
        Boolean completed,
        Integer stars,
        Integer attempts,
        Long bestTimeMs,
        Boolean usedHint,
        LocalDateTime completedAt
) {}
