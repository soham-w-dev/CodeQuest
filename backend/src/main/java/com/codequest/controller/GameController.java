package com.codequest.controller;

import com.codequest.dto.GameExecutionResult;
import com.codequest.dto.SubmissionRequest;
import com.codequest.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameController {

    private final SubmissionService submissionService;

    @PostMapping("/execute")
    public ResponseEntity<GameExecutionResult> submitCode(Authentication authentication, @Valid @RequestBody SubmissionRequest request) {
        return ResponseEntity.ok(submissionService.submitCode(authentication.getName(), request));
    }
}
