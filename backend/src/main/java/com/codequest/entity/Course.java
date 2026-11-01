package com.codequest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "courses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Course extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String language; // e.g., PYTHON, JAVA

    @Column(nullable = false)
    private String name;

    private String description;

    private String icon;
}
