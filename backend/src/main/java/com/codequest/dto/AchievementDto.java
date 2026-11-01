package com.codequest.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AchievementDto(
        Long id,
        String name,
        String description,
        String icon,
        String category,
        Integer xpReward,
        Integer coinReward,
        boolean unlocked,
        LocalDateTime unlockedAt
) {}
