package com.codequest.dto;

import com.codequest.entity.Level;
import lombok.Builder;

@Builder
public record LevelSummaryResponse(
        Long id,
        String title,
        Level.Difficulty difficulty,
        String concept,
        Integer xpReward,
        Integer coinReward,
        Integer orderIndex,
        Boolean completed,
        Integer stars,
        Boolean unlocked
) {}

