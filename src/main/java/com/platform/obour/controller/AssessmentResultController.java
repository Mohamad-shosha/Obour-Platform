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
    private final AssessmentEngineService engineService;

    @GetMapping("/{sessionId}")
    public ResponseEntity<?> getResultBySession(@PathVariable Long sessionId) {
        try {
            return ResponseEntity.ok(engineService.getResultBySessionId(sessionId));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getResultsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(engineService.getResultsByUserId(userId));
    }
}
