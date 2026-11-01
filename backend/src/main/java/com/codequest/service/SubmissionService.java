package com.codequest.service;

import com.codequest.dto.GameExecutionResult;
import com.codequest.dto.SubmissionRequest;
import com.codequest.engine.CodeExecutionService;
import com.codequest.engine.GameEvaluationService;
import com.codequest.entity.Level;
import com.codequest.entity.Submission;
import com.codequest.entity.User;
import com.codequest.entity.UserProgress;
import com.codequest.repository.LevelRepository;
import com.codequest.repository.SubmissionRepository;
import com.codequest.repository.UserProgressRepository;
import com.codequest.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final SubmissionRepository submissionRepository;
    private final UserProgressRepository userProgressRepository;
    private final CodeExecutionService codeExecutionService;
    private final GameEvaluationService evaluationService;
    private final AchievementService achievementService;
    private final ObjectMapper objectMapper;

    @Transactional
    public GameExecutionResult submitCode(String username, SubmissionRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Level level = levelRepository.findById(request.levelId()).orElseThrow();

        long startTime = System.currentTimeMillis();
        var execResult = codeExecutionService.executeCode(request.language(), request.code());
        long execTimeMs = System.currentTimeMillis() - startTime;

        GameExecutionResult gameResult = evaluationService.evaluate(
                execResult,
                level.getLevelConfig(),
                request.language(),
                request.code(),
                level.getTitle(),
                level.getConcept()
        );
        
        // Save submission
        Submission.Status status = gameResult.success() ? Submission.Status.SUCCESS :
                                   gameResult.error() ? Submission.Status.ERROR : Submission.Status.FAILURE;

        String actionsJson = null;
        try {
            actionsJson = objectMapper.writeValueAsString(gameResult.actions());
        } catch (Exception ignored) {}

        Submission submission = Submission.builder()
                .user(user)
                .level(level)
                .language(request.language())
                .code(request.code())
                .status(status)
                .executionTimeMs(execTimeMs)
                .actionsJson(actionsJson)
                .errorMessage(gameResult.errorMessage())
                .build();
        submissionRepository.save(submission);

        if (gameResult.success()) {
            int xpAwarded = level.getXpReward();
            int coinsAwarded = level.getCoinReward();

            // Update user progress
            UserProgress progress = userProgressRepository.findByUserIdAndLevelId(user.getId(), level.getId())
                    .orElse(UserProgress.builder().user(user).level(level).build());

            progress.setAttempts(progress.getAttempts() + 1);
            boolean firstTimeCompletion = !progress.getCompleted();
            if (firstTimeCompletion) {
                progress.setCompleted(true);
                progress.setCompletedAt(LocalDateTime.now());
                progress.setStars(Math.max(progress.getStars(), gameResult.stars()));
                
                if (progress.getBestTimeMs() == null || execTimeMs < progress.getBestTimeMs()) {
                    progress.setBestTimeMs(execTimeMs);
                }

                user.setXp(user.getXp() + xpAwarded);
                user.setCoins(user.getCoins() + coinsAwarded);
                user.setCompletedLevels((user.getCompletedLevels() != null ? user.getCompletedLevels() : 0) + 1);
                user.setTotalStars((user.getTotalStars() != null ? user.getTotalStars() : 0) + gameResult.stars());

                // Update Level rank based on XP (every 200 XP = +1 level)
                int calculatedLevel = Math.max(1, (user.getXp() / 200) + 1);
                user.setLevel(calculatedLevel);

                userRepository.save(user);
                achievementService.checkAndGrantAchievements(user);
            }
            userProgressRepository.save(progress);

            gameResult = new GameExecutionResult(
                    gameResult.success(), gameResult.error(), gameResult.errorMessage(),
                    gameResult.errorLine(), gameResult.errorExplanation(), gameResult.hint(),
                    gameResult.goalReached(), gameResult.gemsCollected(), gameResult.totalGems(),
                    gameResult.actions(), gameResult.stars(), xpAwarded, coinsAwarded, gameResult.stdout()
            );
        }

        return gameResult;
    }
}
