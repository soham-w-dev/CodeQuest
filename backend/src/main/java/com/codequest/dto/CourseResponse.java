package com.codequest.dto;

import lombok.Builder;

@Builder
public record CourseResponse(
        Long id,
        String language,
        String name,
        String description,
        String icon
) {}
