package com.platform.obour.repository;

import com.platform.obour.entity.AssessmentTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssessmentTemplateRepository extends JpaRepository<AssessmentTemplate, Long> {
    List<AssessmentTemplate> findByIsActiveTrueOrderByIdAsc();
    List<AssessmentTemplate> findByDomainIdAndIsActiveTrue(Long domainId);
    List<AssessmentTemplate> findByCategoryIdAndIsActiveTrue(Long categoryId);
}
