package com.codequest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserProgress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;

    @Builder.Default
    private Boolean completed = false;

    @Builder.Default
    private Integer stars = 0; // 0-3

    @Builder.Default
    private Integer attempts = 0;

    private Long bestTimeMs;

    @Builder.Default
    private Boolean usedHint = false;

    private LocalDateTime completedAt;
}
