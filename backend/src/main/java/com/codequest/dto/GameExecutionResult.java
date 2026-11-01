package com.codequest.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record GameExecutionResult(
        boolean success,
        boolean error,
        String errorMessage,
        Integer errorLine,
        String errorExplanation,
        String hint,
        boolean goalReached,
        int gemsCollected,
        int totalGems,
        List<Action> actions,
        int stars,
        int xpAwarded,
        int coinsAwarded,
        String stdout
) {
    @Builder
    public record Action(
            String type, // e.g., "move", "turn_left", "turn_right", "collect", "collision", "win", "error"
            String details,
            Integer line
    ) {}
}
