package com.codequest.controller;

import com.codequest.dto.LevelDetailResponse;
import com.codequest.service.LevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/levels")
@RequiredArgsConstructor
public class LevelController {

    private final LevelService levelService;

    @GetMapping("/{id}")
    public ResponseEntity<LevelDetailResponse> getLevel(@PathVariable Long id) {
        return ResponseEntity.ok(levelService.getLevelById(id));
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<?> getLevelProgress(@PathVariable Long id, org.springframework.security.core.Authentication authentication) {
        // Will be wired later or use a generic approach
        return ResponseEntity.ok().build();
    }
}
