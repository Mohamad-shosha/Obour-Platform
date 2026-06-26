package com.platform.obour.controller;

import com.platform.obour.dto.AssessmentCategoryDTO;
import com.platform.obour.dto.AssessmentDomainDTO;
import com.platform.obour.dto.AssessmentTopicDTO;
import com.platform.obour.service.AssessmentDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domains")
@RequiredArgsConstructor
public class AssessmentDomainController {

    private final AssessmentDomainService domainService;

    @GetMapping
    public ResponseEntity<List<AssessmentDomainDTO>> getAllDomains() {
        return ResponseEntity.ok(domainService.getAllActiveDomains());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssessmentDomainDTO> getDomain(@PathVariable Long id) {
        return ResponseEntity.ok(domainService.getDomainById(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<AssessmentDomainDTO> getDomainBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(domainService.getDomainBySlug(slug));
    }

    @GetMapping("/{domainId}/categories")
    public ResponseEntity<List<AssessmentCategoryDTO>> getCategories(@PathVariable Long domainId) {
        return ResponseEntity.ok(domainService.getCategoriesByDomain(domainId));
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<AssessmentCategoryDTO> getCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(domainService.getCategoryById(categoryId));
    }

    @GetMapping("/categories/{categoryId}/topics")
    public ResponseEntity<List<AssessmentTopicDTO>> getTopics(@PathVariable Long categoryId) {
        return ResponseEntity.ok(domainService.getTopicsByCategory(categoryId));
    }
}
