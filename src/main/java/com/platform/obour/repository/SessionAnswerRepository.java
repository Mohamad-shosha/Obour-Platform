package com.platform.obour.repository;

import com.platform.obour.entity.SessionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface SessionAnswerRepository extends JpaRepository<SessionAnswer, Long> {
    List<SessionAnswer> findBySessionId(Long sessionId);
    Optional<SessionAnswer> findBySessionIdAndQuestionId(Long sessionId, Long questionId);

    @Query("SELECT COUNT(a) FROM SessionAnswer a WHERE a.session.id = :sessionId AND a.isCorrect = true")
    int countCorrectAnswers(Long sessionId);

    @Query("SELECT SUM(a.pointsEarned) FROM SessionAnswer a WHERE a.session.id = :sessionId")
    Integer sumEarnedPoints(Long sessionId);
}
