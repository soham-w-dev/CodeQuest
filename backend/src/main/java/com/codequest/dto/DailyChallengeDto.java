package com.codequest.dto;

import lombok.Builder;

@Builder
public record DailyChallengeDto(
        Long id,
        String title,
        String description,
        String difficulty,
        String concept,
        String missionText,
        Integer xpReward,
        Integer coinReward,
        String levelConfig,
        String starterCodePython,
        String starterCodeJava
) {}
