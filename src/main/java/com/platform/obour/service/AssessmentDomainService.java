package com.platform.obour.service;

import com.platform.obour.dto.*;
import com.platform.obour.entity.*;
import com.platform.obour.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentDomainService {

    private final AssessmentDomainRepository domainRepo;
    private final AssessmentCategoryRepository categoryRepo;
    private final AssessmentTopicRepository topicRepo;
    private final QuestionBankRepository questionRepo;

    // ── Domains ──────────────────────────────────────────────────────────────
    public List<AssessmentDomainDTO> getAllActiveDomains() {
        return domainRepo.findByIsActiveTrueOrderByOrderIndexAsc()
                .stream()
                .map(this::toDomainDTO)
                .collect(Collectors.toList());
    }

    public AssessmentDomainDTO getDomainById(Long id) {
        AssessmentDomain domain = domainRepo.findByIdWithCategories(id)
                .orElseThrow(() -> new RuntimeException("Domain not found: " + id));
        AssessmentDomainDTO dto = toDomainDTO(domain);
        if (domain.getCategories() != null) {
            dto.setCategories(domain.getCategories().stream()
                    .map(c -> toCategoryDTO(c, false))
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public AssessmentDomainDTO getDomainBySlug(String slug) {
        AssessmentDomain domain = domainRepo.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Domain not found: " + slug));
        return toDomainDTO(domain);
    }

    // ── Categories ────────────────────────────────────────────────────────────
    public List<AssessmentCategoryDTO> getCategoriesByDomain(Long domainId) {
        return categoryRepo.findByDomainIdWithTopics(domainId)
                .stream()
                .map(c -> toCategoryDTO(c, true))
                .collect(Collectors.toList());
    }

    public AssessmentCategoryDTO getCategoryById(Long id) {
        AssessmentCategory cat = categoryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
        return toCategoryDTO(cat, true);
    }

    // ── Topics ────────────────────────────────────────────────────────────────
    public List<AssessmentTopicDTO> getTopicsByCategory(Long categoryId) {
        return topicRepo.findByCategoryIdAndIsActiveTrueOrderByOrderIndexAsc(categoryId)
                .stream()
                .map(this::toTopicDTO)
                .collect(Collectors.toList());
    }

    // ── Mappers ───────────────────────────────────────────────────────────────
    private AssessmentDomainDTO toDomainDTO(AssessmentDomain d) {
        return AssessmentDomainDTO.builder()
                .id(d.getId())
                .name(d.getName())
                .nameAr(d.getNameAr())
                .slug(d.getSlug())
                .icon(d.getIcon())
                .color(d.getColor())
                .gradient(d.getGradient())
                .description(d.getDescription())
                .descriptionAr(d.getDescriptionAr())
                .orderIndex(d.getOrderIndex())
                .isActive(d.getIsActive())
                .questionCount(questionRepo.countByDomainIdAndIsActiveTrue(d.getId()))
                .build();
    }

    private AssessmentCategoryDTO toCategoryDTO(AssessmentCategory c, boolean includeTopics) {
        AssessmentCategoryDTO dto = AssessmentCategoryDTO.builder()
                .id(c.getId())
                .domainId(c.getDomain().getId())
                .name(c.getName())
                .nameAr(c.getNameAr())
                .slug(c.getSlug())
                .icon(c.getIcon())
                .description(c.getDescription())
                .orderIndex(c.getOrderIndex())
                .questionCount(questionRepo.countByCategoryIdAndIsActiveTrue(c.getId()))
                .build();
        if (includeTopics && c.getTopics() != null) {
            dto.setTopics(c.getTopics().stream().map(this::toTopicDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    private AssessmentTopicDTO toTopicDTO(AssessmentTopic t) {
        return AssessmentTopicDTO.builder()
                .id(t.getId())
                .categoryId(t.getCategory().getId())
                .name(t.getName())
                .nameAr(t.getNameAr())
                .slug(t.getSlug())
                .icon(t.getIcon())
                .description(t.getDescription())
                .orderIndex(t.getOrderIndex())
                .build();
    }
}
