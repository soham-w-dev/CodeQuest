package com.codequest.service;

import com.codequest.dto.CourseResponse;
import com.codequest.entity.Course;
import com.codequest.exception.ResourceNotFoundException;
import com.codequest.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CourseResponse getCourseByLanguage(String language) {
        Course course = courseRepository.findByLanguage(language.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found for language: " + language));
        return mapToResponse(course);
    }

    private CourseResponse mapToResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .language(course.getLanguage())
                .name(course.getName())
                .description(course.getDescription())
                .icon(course.getIcon())
                .build();
    }
}
