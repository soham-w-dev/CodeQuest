package com.codequest.service;

import com.codequest.dto.UserResponse;
import com.codequest.entity.User;
import com.codequest.exception.ResourceNotFoundException;
import com.codequest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getCurrentUser(String username) {
        User user = getUserByUsername(username);
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateLanguage(String username, String language) {
        User user = getUserByUsername(username);
        user.setSelectedLanguage(language);
        userRepository.save(user);
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateAvatar(String username, String avatar) {
        User user = getUserByUsername(username);
        user.setAvatar(avatar);
        userRepository.save(user);
        return mapToResponse(user);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatar(user.getAvatar() != null ? user.getAvatar() : "robot")
                .xp(user.getXp())
                .level(user.getLevel())
                .coins(user.getCoins())
                .selectedLanguage(user.getSelectedLanguage())
                .streak(user.getStreak() != null ? user.getStreak() : 0)
                .completedLevels(user.getCompletedLevels() != null ? user.getCompletedLevels() : 0)
                .totalStars(user.getTotalStars() != null ? user.getTotalStars() : 0)
                .build();
    }
}
