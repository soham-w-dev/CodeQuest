package com.codequest.controller;

import com.codequest.dto.LeaderboardEntryDto;
import com.codequest.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard(
            @RequestParam(defaultValue = "global") String timeframe) {
        return ResponseEntity.ok(leaderboardService.getLeaderboard(timeframe));
    }
}
