package com.codequest.dto;

import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String username,
        String email,
        String avatar,
        Integer xp,
        Integer level,
        Integer coins,
        String selectedLanguage,
        Integer streak,
        Integer completedLevels,
        Integer totalStars
) {}
