package com.platform.obour.controller;

import com.platform.obour.dto.AssessmentResultDTO;
import com.platform.obour.dto.AssessmentSessionDTO;
import com.platform.obour.dto.SaveAnswerRequest;
import com.platform.obour.dto.StartSessionRequest;
import com.platform.obour.service.AssessmentEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class AssessmentSessionController {

    private final AssessmentEngineService engineService;

    @PostMapping("/start")
    public ResponseEntity<AssessmentSessionDTO> startSession(@RequestBody StartSessionRequest request) {
        return ResponseEntity.ok(engineService.startOrResumeSession(request));
    }

    @PutMapping("/{sessionId}/answer")
    public ResponseEntity<Void> saveAnswer(@PathVariable Long sessionId, @RequestBody SaveAnswerRequest request) {
        engineService.saveAnswer(sessionId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{sessionId}/submit")
    public ResponseEntity<AssessmentResultDTO> submitAssessment(@PathVariable Long sessionId) {
        return ResponseEntity.ok(engineService.submitAssessment(sessionId));
    }
}
