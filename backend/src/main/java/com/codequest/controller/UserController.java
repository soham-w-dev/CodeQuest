package com.codequest.controller;

import com.codequest.dto.UserResponse;
import com.codequest.service.UserService;
import com.codequest.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProgressService progressService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication.getName()));
    }

    @PutMapping("/me/language")
    public ResponseEntity<UserResponse> updateLanguage(Authentication authentication, @RequestBody Map<String, String> request) {
        String language = request.get("language");
        if (language == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.updateLanguage(authentication.getName(), language));
    }

    @PutMapping("/me/avatar")
    public ResponseEntity<UserResponse> updateAvatar(Authentication authentication, @RequestBody Map<String, String> request) {
        String avatar = request.get("avatar");
        if (avatar == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.updateAvatar(authentication.getName(), avatar));
    }

    @GetMapping("/me/progress")
    public ResponseEntity<?> getUserProgress(Authentication authentication) {
        return ResponseEntity.ok(progressService.getUserProgressSummary(authentication.getName()));
    }
}
