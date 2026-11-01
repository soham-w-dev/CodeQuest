package com.codequest.repository;

import com.codequest.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByUserIdAndLevelId(Long userId, Long levelId);
    List<UserProgress> findByUserId(Long userId);
    List<UserProgress> findByUserIdAndLevelWorldId(Long userId, Long worldId);
}
