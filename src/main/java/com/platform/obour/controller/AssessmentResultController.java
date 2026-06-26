package com.platform.obour.controller;

import com.platform.obour.dto.AssessmentResultDTO;
import com.platform.obour.repository.AssessmentResultRepository;
import com.platform.obour.service.AssessmentEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class AssessmentResultController {

    private final AssessmentResultRepository resultRepo;
    // We would ideally have an AssessmentResultService, but for brevity we'll just fetch directly or via engine

    @GetMapping("/{sessionId}")
    public ResponseEntity<?> getResultBySession(@PathVariable Long sessionId) {
        // Just return the raw result entity for now, or map it using a mapper
        return resultRepo.findBySessionId(sessionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getResultsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(resultRepo.findByUserIdOrderByCompletedAtDesc(userId));
    }
}
