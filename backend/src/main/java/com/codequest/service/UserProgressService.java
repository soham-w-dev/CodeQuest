package com.codequest.service;

import com.codequest.dto.ProgressResponse;
import com.codequest.entity.User;
import com.codequest.entity.UserProgress;
import com.codequest.repository.UserProgressRepository;
import com.codequest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProgressService {

    private final UserProgressRepository userProgressRepository;
    private final UserRepository userRepository;

    public List<ProgressResponse> getUserProgress(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return userProgressRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProgressResponse getLevelProgress(String username, Long levelId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return userProgressRepository.findByUserIdAndLevelId(user.getId(), levelId)
                .map(this::mapToResponse)
                .orElse(null);
    }

    private ProgressResponse mapToResponse(UserProgress progress) {
        return ProgressResponse.builder()
                .levelId(progress.getLevel().getId())
                .completed(progress.getCompleted())
                .stars(progress.getStars())
                .attempts(progress.getAttempts())
                .bestTimeMs(progress.getBestTimeMs())
                .usedHint(progress.getUsedHint())
                .completedAt(progress.getCompletedAt())
                .build();
    }
}
