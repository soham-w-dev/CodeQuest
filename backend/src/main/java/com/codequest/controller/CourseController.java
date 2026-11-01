package com.codequest.controller;

import com.codequest.dto.CourseResponse;
import com.codequest.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{language}")
    public ResponseEntity<CourseResponse> getCourseByLanguage(@PathVariable String language) {
        return ResponseEntity.ok(courseService.getCourseByLanguage(language));
    }
}
