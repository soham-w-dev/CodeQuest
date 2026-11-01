package com.codequest.service;

import com.codequest.dto.LevelDetailResponse;
import com.codequest.entity.Level;
import com.codequest.exception.ResourceNotFoundException;
import com.codequest.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LevelService {

    private final LevelRepository levelRepository;

    public LevelDetailResponse getLevelById(Long id) {
        Level level = levelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found with id: " + id));
        return mapToResponse(level);
    }

    private LevelDetailResponse mapToResponse(Level level) {
        return LevelDetailResponse.builder()
                .id(level.getId())
                .worldId(level.getWorld().getId())
                .title(level.getTitle())
                .description(level.getDescription())
                .difficulty(level.getDifficulty())
                .concept(level.getConcept())
                .missionText(level.getMissionText())
                .levelConfig(level.getLevelConfig())
                .starterCode(level.getStarterCode())
                .solutionCode(level.getSolutionCode())
                .xpReward(level.getXpReward())
                .coinReward(level.getCoinReward())
                .orderIndex(level.getOrderIndex())
                .hints(level.getHints())
                .build();
    }
}
