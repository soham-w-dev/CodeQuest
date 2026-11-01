package com.codequest.dto;

import lombok.Builder;

@Builder
public record LeaderboardEntryDto(
        int rank,
        Long userId,
        String username,
        String avatar,
        Integer xp,
        Integer level,
        Integer completedLevels,
        Integer streak
) {}
