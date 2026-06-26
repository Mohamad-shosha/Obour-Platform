package com.platform.obour.repository;

import com.platform.obour.entity.AssessmentSession;
import com.platform.obour.entity.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface AssessmentSessionRepository extends JpaRepository<AssessmentSession, Long> {
    List<AssessmentSession> findByUserIdOrderByStartedAtDesc(Long userId);

    @Query("SELECT s FROM AssessmentSession s WHERE s.user.id = :userId AND s.template.id = :templateId AND s.status IN ('STARTED','IN_PROGRESS') ORDER BY s.startedAt DESC")
    Optional<AssessmentSession> findActiveSession(Long userId, Long templateId);

    List<AssessmentSession> findByUserIdAndStatus(Long userId, SessionStatus status);

    long countByUserIdAndStatus(Long userId, SessionStatus status);
}
