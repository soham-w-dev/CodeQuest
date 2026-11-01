package com.codequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SubmissionRequest(
        @NotNull(message = "Level ID is required")
        Long levelId,
        
        @NotBlank(message = "Language is required")
        String language,
        
        @NotBlank(message = "Code is required")
        String code
) {}
