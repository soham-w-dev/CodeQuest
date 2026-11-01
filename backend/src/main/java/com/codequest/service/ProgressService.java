package com.codequest.service;

import com.codequest.entity.User;
import com.codequest.entity.UserProgress;
import com.codequest.repository.UserProgressRepository;
import com.codequest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final UserProgressRepository userProgressRepository;
    private final UserRepository userRepository;

    public Map<String, Object> getUserProgressSummary(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        List<UserProgress> progressList = userProgressRepository.findByUserId(user.getId());

        long completedLevels = progressList.stream().filter(UserProgress::getCompleted).count();
        int totalStars = progressList.stream().mapToInt(UserProgress::getStars).sum();

        return Map.of(
                "completedLevels", completedLevels,
                "totalStars", totalStars,
                "totalXp", user.getXp(),
                "level", user.getLevel(),
                "coins", user.getCoins()
        );
    }
}
