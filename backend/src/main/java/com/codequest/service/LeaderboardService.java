package com.codequest.service;

import com.codequest.dto.LeaderboardEntryDto;
import com.codequest.entity.User;
import com.codequest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;

    public List<LeaderboardEntryDto> getLeaderboard(String timeframe) {
        // Sort by XP descending, then level descending
        List<User> topUsers = userRepository.findAll(Sort.by(Sort.Direction.DESC, "xp", "level"));

        List<LeaderboardEntryDto> leaderboard = new ArrayList<>();
        int rank = 1;
        for (User user : topUsers) {
            leaderboard.add(LeaderboardEntryDto.builder()
                    .rank(rank++)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .avatar(user.getAvatar() != null ? user.getAvatar() : "robot")
                    .xp(user.getXp() != null ? user.getXp() : 0)
                    .level(user.getLevel() != null ? user.getLevel() : 1)
                    .completedLevels(user.getCompletedLevels() != null ? user.getCompletedLevels() : 0)
                    .streak(user.getStreak() != null ? user.getStreak() : 0)
                    .build());
        }
        return leaderboard;
    }
}
