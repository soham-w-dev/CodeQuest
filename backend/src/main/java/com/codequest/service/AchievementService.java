package com.codequest.service;

import com.codequest.dto.AchievementDto;
import com.codequest.entity.Achievement;
import com.codequest.entity.User;
import com.codequest.entity.UserAchievement;
import com.codequest.exception.ResourceNotFoundException;
import com.codequest.repository.AchievementRepository;
import com.codequest.repository.UserAchievementRepository;
import com.codequest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;

    public List<AchievementDto> getAllAchievements(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        List<Achievement> allAchievements = achievementRepository.findAll();
        List<UserAchievement> userUnlocks = userAchievementRepository.findByUserId(user.getId());

        Map<Long, UserAchievement> unlockMap = userUnlocks.stream()
                .collect(java.util.stream.Collectors.toMap(
                        ua -> ua.getAchievement().getId(),
                        ua -> ua
                ));

        return allAchievements.stream().map(a -> {
            boolean isUnlocked = unlockMap.containsKey(a.getId());
            return AchievementDto.builder()
                    .id(a.getId())
                    .name(a.getName())
                    .description(a.getDescription())
                    .icon(a.getIcon())
                    .category(a.getCategory())
                    .xpReward(a.getXpReward())
                    .coinReward(a.getCoinReward())
                    .unlocked(isUnlocked)
                    .unlockedAt(isUnlocked ? unlockMap.get(a.getId()).getUnlockedAt() : null)
                    .build();
        }).toList();
    }

    @Transactional
    public void checkAndGrantAchievements(User user) {
        // 1. First Steps: complete 1 level
        if (user.getCompletedLevels() >= 1) {
            grantAchievement(user, "First Steps");
        }
        // 2. Loop Master: complete 3 levels
        if (user.getCompletedLevels() >= 3) {
            grantAchievement(user, "Loop Master");
        }
        // 3. Code Warrior: complete 5 levels
        if (user.getCompletedLevels() >= 5) {
            grantAchievement(user, "Code Warrior");
        }
        // 4. Star Collector: collect 10 stars
        if (user.getTotalStars() >= 10) {
            grantAchievement(user, "Star Collector");
        }
    }

    private void grantAchievement(User user, String achievementName) {
        achievementRepository.findByName(achievementName).ifPresent(achievement -> {
            if (!userAchievementRepository.existsByUserIdAndAchievementId(user.getId(), achievement.getId())) {
                UserAchievement userAchievement = UserAchievement.builder()
                        .user(user)
                        .achievement(achievement)
                        .build();
                userAchievementRepository.save(userAchievement);

                user.setXp(user.getXp() + achievement.getXpReward());
                user.setCoins(user.getCoins() + achievement.getCoinReward());
                userRepository.save(user);
                log.info("Unlocked achievement {} for user {}", achievementName, user.getUsername());
            }
        });
    }
}
