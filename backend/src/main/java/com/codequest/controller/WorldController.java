package com.codequest.controller;

import com.codequest.dto.WorldResponse;
import com.codequest.service.WorldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/worlds")
@RequiredArgsConstructor
public class WorldController {

    private final WorldService worldService;

    @GetMapping
    public ResponseEntity<List<WorldResponse>> getWorlds(Authentication authentication, @RequestParam Long courseId) {
        String username = (authentication != null) ? authentication.getName() : null;
        return ResponseEntity.ok(worldService.getWorldsByCourse(courseId, username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorldResponse> getWorld(Authentication authentication, @PathVariable Long id) {
        String username = (authentication != null) ? authentication.getName() : null;
        return ResponseEntity.ok(worldService.getWorldById(id, username));
    }
}

