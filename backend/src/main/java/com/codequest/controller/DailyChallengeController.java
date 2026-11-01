package com.codequest.controller;

import com.codequest.dto.DailyChallengeDto;
import com.codequest.service.DailyChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/daily-challenge")
@RequiredArgsConstructor
public class DailyChallengeController {

    private final DailyChallengeService dailyChallengeService;

    @GetMapping
    public ResponseEntity<DailyChallengeDto> getDailyChallenge() {
        return ResponseEntity.ok(dailyChallengeService.getTodayChallenge());
    }
}
