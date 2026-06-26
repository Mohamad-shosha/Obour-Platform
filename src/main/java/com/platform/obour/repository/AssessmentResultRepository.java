package com.platform.obour.repository;

import com.platform.obour.entity.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, Long> {
    Optional<AssessmentResult> findBySessionId(Long sessionId);
    List<AssessmentResult> findByUserIdOrderByCompletedAtDesc(Long userId);
    List<AssessmentResult> findByUserIdAndTemplate_Domain_Id(Long userId, Long domainId);
}
