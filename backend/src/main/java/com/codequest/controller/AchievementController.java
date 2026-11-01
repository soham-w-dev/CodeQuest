package com.codequest.controller;

import com.codequest.dto.AchievementDto;
import com.codequest.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping
    public ResponseEntity<List<AchievementDto>> getAchievements(Authentication authentication) {
        return ResponseEntity.ok(achievementService.getAllAchievements(authentication.getName()));
    }
}
