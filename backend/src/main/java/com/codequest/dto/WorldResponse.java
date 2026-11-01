package com.codequest.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record WorldResponse(
        Long id,
        Long courseId,
        String name,
        String description,
        String theme,
        Integer orderIndex,
        Integer unlockStarsRequired,
        List<LevelSummaryResponse> levels
) {}
