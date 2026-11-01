package com.codequest.dto;

import com.codequest.entity.Level;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Builder;

@Builder
public record LevelDetailResponse(
        Long id,
        Long worldId,
        String title,
        String description,
        Level.Difficulty difficulty,
        String concept,
        String missionText,
        @JsonRawValue
        String levelConfig,
        String starterCode,
        String solutionCode,
        Integer xpReward,
        Integer coinReward,
        Integer orderIndex,
        @JsonRawValue
        String hints
) {}
