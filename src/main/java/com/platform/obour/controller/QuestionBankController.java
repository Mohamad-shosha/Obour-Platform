package com.platform.obour.controller;

import com.platform.obour.entity.QuestionBank;
import com.platform.obour.repository.QuestionBankRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions/bank")
@RequiredArgsConstructor
public class QuestionBankController {

    private final QuestionBankRepository questionRepo;

    @GetMapping("/domain/{domainId}")
    public ResponseEntity<Page<QuestionBank>> getQuestionsByDomain(
            @PathVariable Long domainId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(questionRepo.findByDomainId(domainId, PageRequest.of(page, size)));
    }
}
