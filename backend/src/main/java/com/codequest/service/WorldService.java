package com.codequest.service;

import com.codequest.dto.LevelSummaryResponse;
import com.codequest.dto.WorldResponse;
import com.codequest.entity.Level;
import com.codequest.entity.User;
import com.codequest.entity.UserProgress;
import com.codequest.entity.World;
import com.codequest.exception.ResourceNotFoundException;
import com.codequest.repository.UserProgressRepository;
import com.codequest.repository.UserRepository;
import com.codequest.repository.WorldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorldService {

    private final WorldRepository worldRepository;
    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;

    public List<WorldResponse> getWorldsByCourse(Long courseId, String username) {
        Map<Long, UserProgress> userProgressMap = getUserProgressMap(username);
        return worldRepository.findByCourseIdOrderByOrderIndexAsc(courseId).stream()
                .map(world -> mapToResponse(world, userProgressMap))
                .collect(Collectors.toList());
    }

    public WorldResponse getWorldById(Long id, String username) {
        World world = worldRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("World not found with id: " + id));
        Map<Long, UserProgress> userProgressMap = getUserProgressMap(username);
        return mapToResponse(world, userProgressMap);
    }

    private Map<Long, UserProgress> getUserProgressMap(String username) {
        if (username == null) return Collections.emptyMap();
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) return Collections.emptyMap();
        
        List<UserProgress> list = userProgressRepository.findByUserId(userOpt.get().getId());
        Map<Long, UserProgress> map = new HashMap<>();
        for (UserProgress up : list) {
            map.put(up.getLevel().getId(), up);
        }
        return map;
    }

    private WorldResponse mapToResponse(World world, Map<Long, UserProgress> progressMap) {
        List<Level> rawLevels = world.getLevels() != null ? new ArrayList<>(world.getLevels()) : new ArrayList<>();
        rawLevels.sort(Comparator.comparingInt(Level::getOrderIndex));

        List<LevelSummaryResponse> summaryList = new ArrayList<>();
        boolean previousCompleted = true; // First level is always unlocked

        for (int i = 0; i < rawLevels.size(); i++) {
            Level level = rawLevels.get(i);
            UserProgress up = progressMap.get(level.getId());
            boolean isCompleted = up != null && Boolean.TRUE.equals(up.getCompleted());
            int stars = up != null && up.getStars() != null ? up.getStars() : 0;
            boolean isUnlocked = true; // All levels in world are playable!

            summaryList.add(LevelSummaryResponse.builder()
                    .id(level.getId())
                    .title(level.getTitle())
                    .difficulty(level.getDifficulty())
                    .concept(level.getConcept())
                    .xpReward(level.getXpReward())
                    .coinReward(level.getCoinReward())
                    .orderIndex(level.getOrderIndex())
                    .completed(isCompleted)
                    .stars(stars)
                    .unlocked(isUnlocked)
                    .build());
        }

        return WorldResponse.builder()
                .id(world.getId())
                .courseId(world.getCourse().getId())
                .name(world.getName())
                .description(world.getDescription())
                .theme(world.getTheme())
                .orderIndex(world.getOrderIndex())
                .unlockStarsRequired(world.getUnlockStarsRequired())
                .levels(summaryList)
                .build();
    }
}

