package com.platform.obour.repository;

import com.platform.obour.entity.AssessmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AssessmentCategoryRepository extends JpaRepository<AssessmentCategory, Long> {
    List<AssessmentCategory> findByDomainIdAndIsActiveTrueOrderByOrderIndexAsc(Long domainId);

    @Query("SELECT c FROM AssessmentCategory c LEFT JOIN FETCH c.topics WHERE c.domain.id = :domainId AND c.isActive = true ORDER BY c.orderIndex ASC")
    List<AssessmentCategory> findByDomainIdWithTopics(Long domainId);
}
