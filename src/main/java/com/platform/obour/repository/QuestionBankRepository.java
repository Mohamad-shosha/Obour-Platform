package com.platform.obour.repository;

import com.platform.obour.entity.QuestionBank;
import com.platform.obour.entity.enums.DifficultyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long> {

    List<QuestionBank> findByDomainIdAndIsActiveTrueOrderByIdAsc(Long domainId);

    List<QuestionBank> findByCategoryIdAndIsActiveTrueOrderByIdAsc(Long categoryId);

    List<QuestionBank> findByTopicIdAndIsActiveTrueOrderByIdAsc(Long topicId);

    List<QuestionBank> findByDomainIdAndDifficultyAndIsActiveTrue(Long domainId, DifficultyLevel difficulty);

    @Query("SELECT q FROM QuestionBank q WHERE q.domain.id = :domainId AND q.isActive = true ORDER BY RAND()")
    List<QuestionBank> findRandomByDomain(@Param("domainId") Long domainId, Pageable pageable);

    @Query("SELECT q FROM QuestionBank q WHERE q.category.id = :categoryId AND q.difficulty = :difficulty AND q.isActive = true ORDER BY RAND()")
    List<QuestionBank> findRandomByCategoryAndDifficulty(@Param("categoryId") Long categoryId,
                                                          @Param("difficulty") DifficultyLevel difficulty,
                                                          Pageable pageable);

    @Query("SELECT q FROM QuestionBank q WHERE q.topic.id = :topicId AND q.isActive = true ORDER BY RAND()")
    List<QuestionBank> findRandomByTopic(@Param("topicId") Long topicId, Pageable pageable);

    @Query("SELECT q FROM QuestionBank q WHERE q.category.id = :categoryId AND q.isActive = true ORDER BY RAND()")
    List<QuestionBank> findRandomByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    Page<QuestionBank> findByDomainId(Long domainId, Pageable pageable);

    long countByDomainIdAndIsActiveTrue(Long domainId);
    long countByCategoryIdAndIsActiveTrue(Long categoryId);
}
