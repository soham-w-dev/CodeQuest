package com.codequest.repository;

import com.codequest.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LevelRepository extends JpaRepository<Level, Long> {
    List<Level> findByWorldIdOrderByOrderIndexAsc(Long worldId);
    java.util.Optional<Level> findByWorldIdAndOrderIndex(Long worldId, Integer orderIndex);
}
