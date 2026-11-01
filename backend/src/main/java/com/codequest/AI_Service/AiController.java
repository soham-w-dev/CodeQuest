package com.codequest.AI_Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping({"/debug-error", "/explain-error"})
    public ResponseEntity<AiResponse> debugError(@RequestBody AiRequest request) {
        return ResponseEntity.ok(aiService.debugError(request));
    }

    @PostMapping({"/solution", "/explain-solution"})
    public ResponseEntity<AiResponse> giveSolution(@RequestBody AiRequest request) {
        return ResponseEntity.ok(aiService.giveSolution(request));
    }

    @PostMapping("/explain-concept")
    public ResponseEntity<AiResponse> explainConcept(@RequestBody AiRequest request) {
        return ResponseEntity.ok(aiService.explainConcept(request));
    }

    @PostMapping("/hint")
    public ResponseEntity<AiResponse> getHint(@RequestBody AiRequest request) {
        return ResponseEntity.ok(aiService.getHint(request));
    }
}
