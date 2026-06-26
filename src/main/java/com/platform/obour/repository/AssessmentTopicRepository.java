package com.platform.obour.repository;

import com.platform.obour.entity.AssessmentTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssessmentTopicRepository extends JpaRepository<AssessmentTopic, Long> {
    List<AssessmentTopic> findByCategoryIdAndIsActiveTrueOrderByOrderIndexAsc(Long categoryId);
}
