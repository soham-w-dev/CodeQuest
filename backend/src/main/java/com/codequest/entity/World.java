package com.codequest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "worlds")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class World extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private String name;

    private String description;

    private String theme;

    private Integer orderIndex;

    @Builder.Default
    private Integer unlockStarsRequired = 0;
    
    @OneToMany(mappedBy = "world", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @lombok.Builder.Default
    private List<Level> levels = new java.util.ArrayList<>();
}
