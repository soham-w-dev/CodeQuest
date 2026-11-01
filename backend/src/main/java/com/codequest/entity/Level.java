package com.codequest.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "levels")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Level extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "world_id", nullable = false)
    private World world;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private String concept;

    @Column(length = 2000)
    private String missionText;

    @Column(columnDefinition = "TEXT")
    private String levelConfig; // JSON format

    @Column(columnDefinition = "TEXT")
    private String solutionCode;

    @Column(columnDefinition = "TEXT")
    private String starterCode;

    private Integer xpReward;

    private Integer coinReward;

    private Integer orderIndex;

    @Column(columnDefinition = "TEXT")
    private String hints; // JSON array format

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}
