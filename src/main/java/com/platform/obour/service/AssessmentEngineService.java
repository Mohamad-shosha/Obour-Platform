package com.platform.obour.service;

import com.platform.obour.dto.*;
import com.platform.obour.entity.*;
import com.platform.obour.entity.enums.DifficultyLevel;
import com.platform.obour.entity.enums.SessionStatus;
import com.platform.obour.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentEngineService {

    private final AssessmentSessionRepository sessionRepo;
    private final AssessmentTemplateRepository templateRepo;
    private final QuestionBankRepository questionRepo;
    private final SessionAnswerRepository answerRepo;
    private final AssessmentResultRepository resultRepo;
    private final AssessmentCategoryRepository categoryRepo;
    private final UserRepository userRepo;

    // ── Get Session by ID ───────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public AssessmentSessionDTO getSession(Long sessionId) {
        AssessmentSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));
        return buildSessionDTO(session, session.getTemplate());
    }

    // ── Start or Resume Session ────────────────────────────────────────────────
    @Transactional
    public AssessmentSessionDTO startOrResumeSession(StartSessionRequest request) {
        AssessmentTemplate template = null;

        if (request.getCategoryId() != null) {
            Long catId = request.getCategoryId();
            List<AssessmentTemplate> templates = templateRepo.findByCategoryIdAndIsActiveTrue(catId);
            if (templates != null && !templates.isEmpty()) {
                template = templates.get(0);
            } else {
                AssessmentCategory category = categoryRepo.findById(catId)
                    .orElseThrow(() -> new RuntimeException("Category not found: " + catId));
                    
                AssessmentTemplate newTemplate = AssessmentTemplate.builder()
                    .domain(category.getDomain())
                    .category(category)
                    .name("تقييم " + (category.getNameAr() != null ? category.getNameAr() : category.getName()))
                    .nameAr("تقييم " + (category.getNameAr() != null ? category.getNameAr() : category.getName()))
                    .questionCount(15)
                    .timeLimitMins(20)
                    .passingScore(60)
                    .allowResume(true)
                    .randomize(true)
                    .isActive(true)
                    .build();
                template = templateRepo.save(newTemplate);
            }
        } else if (request.getTemplateId() != null) {
            Long templateId = request.getTemplateId();
            template = templateRepo.findById(templateId).orElseGet(() -> {
                List<AssessmentTemplate> templates = templateRepo.findByCategoryIdAndIsActiveTrue(templateId);
                if (templates != null && !templates.isEmpty()) {
                    return templates.get(0);
                }
                
                AssessmentCategory category = categoryRepo.findById(templateId)
                    .orElseThrow(() -> new RuntimeException("Template/Category not found: " + templateId));
                    
                AssessmentTemplate newTemplate = AssessmentTemplate.builder()
                    .domain(category.getDomain())
                    .category(category)
                    .name("تقييم " + (category.getNameAr() != null ? category.getNameAr() : category.getName()))
                    .nameAr("تقييم " + (category.getNameAr() != null ? category.getNameAr() : category.getName()))
                    .questionCount(15)
                    .timeLimitMins(20)
                    .passingScore(60)
                    .allowResume(true)
                    .randomize(true)
                    .isActive(true)
                    .build();
                return templateRepo.save(newTemplate);
            });
        }

        if (template == null) {
            throw new RuntimeException("No template or category ID provided.");
        }

        // Update the request with the actual template ID so existing session lookup works properly
        request.setTemplateId(template.getId());

        // Check for existing active session
        if (Boolean.TRUE.equals(request.getResumeIfExists()) && Boolean.TRUE.equals(template.getAllowResume())) {
            Optional<AssessmentSession> existing = sessionRepo.findActiveSession(
                    request.getUserId(), request.getTemplateId());
            if (existing.isPresent()) {
                log.info("Resuming session {} for user {}", existing.get().getId(), request.getUserId());
                return buildSessionDTO(existing.get(), template);
            }
        }

        // Prevent retaking if already submitted
        Optional<AssessmentSession> submitted = sessionRepo.findFirstByUserIdAndTemplateIdAndStatusOrderByStartedAtDesc(
                request.getUserId(), request.getTemplateId(), SessionStatus.SUBMITTED);
        if (submitted.isPresent()) {
            log.info("Returning already submitted session {} for user {}", submitted.get().getId(), request.getUserId());
            return buildSessionDTO(submitted.get(), template);
        }

        // Fetch user from DB to avoid TransientPropertyValueException
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        AssessmentSession session = AssessmentSession.builder()
                .user(user)
                .template(template)
                .status(SessionStatus.STARTED)
                .startedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(template.getTimeLimitMins() + 5))
                .timeSpentSecs(0)
                .currentQuestionIndex(0)
                .build();

        session = sessionRepo.save(session);

        // Select questions based on template config
        List<QuestionBank> selected = selectQuestions(template);

        // Create session questions (shuffled if configured)
        List<SessionQuestion> sessionQuestions = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            sessionQuestions.add(SessionQuestion.builder()
                    .session(session)
                    .question(selected.get(i))
                    .orderIndex(i)
                    .isFlagged(false)
                    .build());
        }
        session.setSessionQuestions(sessionQuestions);
        session.setStatus(SessionStatus.IN_PROGRESS);
        session = sessionRepo.save(session);

        log.info("Created new session {} with {} questions for user {}", session.getId(), selected.size(), request.getUserId());
        return buildSessionDTO(session, template);
    }

    // ── Save Answer (Auto-save) ────────────────────────────────────────────────
    @Transactional
    public void saveAnswer(Long sessionId, SaveAnswerRequest request) {
        AssessmentSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        if (session.getStatus() == SessionStatus.SUBMITTED) {
            throw new RuntimeException("Session already submitted");
        }

        QuestionBank question = questionRepo.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        // Upsert answer
        SessionAnswer answer = answerRepo
                .findBySessionIdAndQuestionId(sessionId, request.getQuestionId())
                .orElse(SessionAnswer.builder().session(session).question(question).build());

        if (request.getChoiceId() != null) {
            QuestionBankChoice choice = new QuestionBankChoice();
            choice.setId(request.getChoiceId());
            answer.setChoice(choice);

            // Check correctness
            question.getChoices().stream()
                    .filter(c -> c.getId().equals(request.getChoiceId()))
                    .findFirst()
                    .ifPresent(c -> {
                        answer.setIsCorrect(c.getIsCorrect());
                        answer.setPointsEarned(Boolean.TRUE.equals(c.getIsCorrect()) ? question.getPoints() : 0);
                    });
        }

        answer.setTextAnswer(request.getTextAnswer());
        answer.setAnsweredAt(LocalDateTime.now());
        answerRepo.save(answer);

        // Update session progress
        if (request.getTimeSpentSecs() != null) {
            session.setTimeSpentSecs(request.getTimeSpentSecs());
        }
        sessionRepo.save(session);
    }

    // ── Submit Assessment ──────────────────────────────────────────────────────
    @Transactional
    public AssessmentResultDTO submitAssessment(Long sessionId) {
        AssessmentSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        if (session.getStatus() == SessionStatus.SUBMITTED) {
            // Return existing result
            return resultRepo.findBySessionId(sessionId)
                    .map(r -> toResultDTO(r, true))
                    .orElseThrow(() -> new RuntimeException("Result not found for submitted session"));
        }

        session.setStatus(SessionStatus.SUBMITTED);
        session.setSubmittedAt(LocalDateTime.now());
        sessionRepo.save(session);

        // Calculate scores
        List<SessionAnswer> answers = answerRepo.findBySessionId(sessionId);
        int totalQ = session.getSessionQuestions() != null ? session.getSessionQuestions().size() : 0;
        int answered = answers.size();
        int correct = (int) answers.stream().filter(a -> Boolean.TRUE.equals(a.getIsCorrect())).count();
        int totalPts = session.getSessionQuestions() != null
                ? session.getSessionQuestions().stream().mapToInt(sq -> sq.getQuestion().getPoints()).sum()
                : 0;
        int earnedPts = answers.stream().mapToInt(a -> a.getPointsEarned() != null ? a.getPointsEarned() : 0).sum();
        BigDecimal scorePct = totalQ > 0
                ? BigDecimal.valueOf(correct * 100.0 / totalQ).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        boolean passed = scorePct.compareTo(BigDecimal.valueOf(session.getTemplate().getPassingScore())) >= 0;
        DifficultyLevel level = scorePct.compareTo(BigDecimal.valueOf(80)) >= 0 ? DifficultyLevel.ADVANCED
                : scorePct.compareTo(BigDecimal.valueOf(60)) >= 0 ? DifficultyLevel.INTERMEDIATE
                : DifficultyLevel.BEGINNER;

        AssessmentResult result = AssessmentResult.builder()
                .session(session)
                .user(session.getUser())
                .template(session.getTemplate())
                .totalQuestions(totalQ)
                .answered(answered)
                .correct(correct)
                .scorePercent(scorePct)
                .totalPoints(totalPts)
                .earnedPoints(earnedPts)
                .passed(passed)
                .timeTakenSecs(session.getTimeSpentSecs())
                .levelAchieved(level)
                .completedAt(LocalDateTime.now())
                .build();

        result = resultRepo.save(result);
        log.info("Assessment submitted. Session={} Score={}%", sessionId, scorePct);
        return toResultDTO(result, true);
    }
    // ── Get Result ─────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public AssessmentResultDTO getResultBySessionId(Long sessionId) {
        return resultRepo.findBySessionId(sessionId)
                .map(r -> toResultDTO(r, true))
                .orElseThrow(() -> new RuntimeException("Result not found for session: " + sessionId));
    }

    @Transactional(readOnly = true)
    public List<AssessmentResultDTO> getResultsByUserId(Long userId) {
        return resultRepo.findByUserIdOrderByCompletedAtDesc(userId).stream()
                .map(r -> toResultDTO(r, false))
                .collect(Collectors.toList());
    }

    // ── Question Selection Logic ───────────────────────────────────────────────
    private List<QuestionBank> selectQuestions(AssessmentTemplate template) {
        List<QuestionBank> pool;
        int count = template.getQuestionCount();
        PageRequest page = PageRequest.of(0, count * 3); // over-fetch for shuffling

        if (template.getTopic() != null) {
            pool = questionRepo.findRandomByTopic(template.getTopic().getId(), page);
        } else if (template.getCategory() != null) {
            DifficultyLevel diff = (template.getDifficulty() == DifficultyLevel.MIXED)
                    ? null : template.getDifficulty();
            if (diff != null) {
                pool = questionRepo.findRandomByCategoryAndDifficulty(
                        template.getCategory().getId(), diff, page);
            } else {
                pool = questionRepo.findRandomByCategory(
                        template.getCategory().getId(), page);
            }
        } else if (template.getDomain() != null) {
            pool = questionRepo.findRandomByDomain(template.getDomain().getId(), page);
        } else {
            pool = questionRepo.findAll(PageRequest.of(0, count)).getContent();
        }

        if (Boolean.TRUE.equals(template.getRandomize())) {
            Collections.shuffle(pool);
        }

        if (pool == null || pool.isEmpty()) {
            throw new RuntimeException("No active questions found for the selected assessment template.");
        }

        return pool.stream().limit(count).collect(Collectors.toList());
    }

    // ── DTO Builders ──────────────────────────────────────────────────────────
    private AssessmentSessionDTO buildSessionDTO(AssessmentSession session, AssessmentTemplate template) {
        List<QuestionBankDTO> questions = session.getSessionQuestions() != null
                ? session.getSessionQuestions().stream()
                    .map(sq -> toQuestionDTO(sq.getQuestion(), false))
                    .collect(Collectors.toList())
                : List.of();

        List<SessionAnswer> savedAnswers = answerRepo.findBySessionId(session.getId());

        return AssessmentSessionDTO.builder()
                .id(session.getId())
                .userId(session.getUser().getId())
                .templateId(template.getId())
                .templateName(template.getName())
                .templateNameAr(template.getNameAr())
                .questionCount(questions.size())
                .timeLimitMins(template.getTimeLimitMins())
                .passingScore(template.getPassingScore())
                .showExplanation(template.getShowExplanation())
                .status(session.getStatus().name())
                .startedAt(session.getStartedAt())
                .expiresAt(session.getExpiresAt())
                .timeSpentSecs(session.getTimeSpentSecs())
                .currentQuestionIndex(session.getCurrentQuestionIndex())
                .answeredCount(savedAnswers.size())
                .questions(questions)
                .build();
    }

    public QuestionBankDTO toQuestionDTO(QuestionBank q, boolean includeAnswers) {
        List<QuestionBankChoiceDTO> choices = q.getChoices() != null
                ? q.getChoices().stream().map(c -> QuestionBankChoiceDTO.builder()
                        .id(c.getId())
                        .choiceText(c.getChoiceText())
                        .choiceTextAr(c.getChoiceTextAr())
                        .isCorrect(includeAnswers ? c.getIsCorrect() : null)
                        .explanation(includeAnswers ? c.getExplanation() : null)
                        .orderIndex(c.getOrderIndex())
                        .build()).collect(Collectors.toList())
                : List.of();

        return QuestionBankDTO.builder()
                .id(q.getId())
                .domainId(q.getDomain() != null ? q.getDomain().getId() : null)
                .categoryId(q.getCategory() != null ? q.getCategory().getId() : null)
                .topicId(q.getTopic() != null ? q.getTopic().getId() : null)
                .questionText(q.getQuestionText())
                .questionTextAr(q.getQuestionTextAr())
                .questionType(q.getQuestionType() != null ? q.getQuestionType().name() : null)
                .difficulty(q.getDifficulty() != null ? q.getDifficulty().name() : null)
                .explanation(includeAnswers ? q.getExplanation() : null)
                .codeSnippet(q.getCodeSnippet())
                .codeLanguage(q.getCodeLanguage())
                .points(q.getPoints())
                .timeLimitSecs(q.getTimeLimitSecs())
                .choices(choices)
                .tags(q.getTags() != null ? q.getTags().stream().map(QuestionTag::getName).collect(Collectors.toList()) : List.of())
                .build();
    }

    private AssessmentResultDTO toResultDTO(AssessmentResult r, boolean includeReviews) {
        AssessmentSession session = r.getSession();
        boolean showExplanation = Boolean.TRUE.equals(r.getTemplate().getShowExplanation());
        
        List<QuestionReviewDTO> reviews = List.of();
        
        if (includeReviews) {
            List<SessionAnswer> answers = answerRepo.findBySessionId(session.getId());
            Map<Long, SessionAnswer> answerMap = answers.stream()
                    .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a));
                    
            reviews = session.getSessionQuestions() != null ? session.getSessionQuestions().stream()
                    .map(sq -> {
                        QuestionBank q = sq.getQuestion();
                        SessionAnswer ans = answerMap.get(q.getId());
                        return QuestionReviewDTO.builder()
                                .questionId(q.getId())
                                .questionText(q.getQuestionText())
                                .questionTextAr(q.getQuestionTextAr())
                                .userChoiceId(ans != null && ans.getChoice() != null ? ans.getChoice().getId() : null)
                                .userChoiceText(ans != null && ans.getChoice() != null ? ans.getChoice().getChoiceText() : null)
                                .userChoiceTextAr(ans != null && ans.getChoice() != null ? ans.getChoice().getChoiceTextAr() : null)
                                .isCorrect(ans != null && Boolean.TRUE.equals(ans.getIsCorrect()))
                                .correctChoiceId(showExplanation ? q.getChoices().stream().filter(c -> Boolean.TRUE.equals(c.getIsCorrect())).map(c -> c.getId()).findFirst().orElse(null) : null)
                                .explanation(showExplanation ? q.getExplanation() : null)
                                .build();
                    })
                    .collect(Collectors.toList()) : List.of();
        }

        return AssessmentResultDTO.builder()
                .id(r.getId())
                .sessionId(r.getSession().getId())
                .userId(r.getUser().getId())
                .templateId(r.getTemplate().getId())
                .templateName(r.getTemplate().getName())
                .totalQuestions(r.getTotalQuestions())
                .answered(r.getAnswered())
                .correct(r.getCorrect())
                .scorePercent(r.getScorePercent())
                .totalPoints(r.getTotalPoints())
                .earnedPoints(r.getEarnedPoints())
                .passed(r.getPassed())
                .timeTakenSecs(r.getTimeTakenSecs())
                .levelAchieved(r.getLevelAchieved() != null ? r.getLevelAchieved().name() : null)
                .completedAt(r.getCompletedAt())
                .questionReviews(reviews)
                .build();
    }
}
