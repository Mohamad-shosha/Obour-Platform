package com.platform.obour.repository;

import com.platform.obour.entity.AssessmentDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface AssessmentDomainRepository extends JpaRepository<AssessmentDomain, Long> {
    List<AssessmentDomain> findByIsActiveTrueOrderByOrderIndexAsc();
    Optional<AssessmentDomain> findBySlug(String slug);

    @Query("SELECT d FROM AssessmentDomain d LEFT JOIN FETCH d.categories WHERE d.id = :id")
    Optional<AssessmentDomain> findByIdWithCategories(Long id);
}
