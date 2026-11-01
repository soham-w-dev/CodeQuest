package com.codequest.service;

import com.codequest.dto.DailyChallengeDto;
import com.codequest.entity.Level;
import com.codequest.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyChallengeService {

    private final LevelRepository levelRepository;

    public DailyChallengeDto getTodayChallenge() {
        List<Level> levels = levelRepository.findAll();
        if (levels.isEmpty()) {
            return DailyChallengeDto.builder()
                    .id(100L)
                    .title("The Lost Robot")
                    .description("Solve today's featured maze puzzle!")
                    .difficulty("HARD")
                    .concept("Loops & Conditions")
                    .missionText("The lost robot needs to reach the portal while collecting all glowing crystals!")
                    .xpReward(200)
                    .coinReward(50)
                    .levelConfig("{\"rows\":6,\"cols\":6,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":5,\"col\":5},\"walls\":[{\"row\":0,\"col\":3},{\"row\":1,\"col\":3}],\"gems\":[{\"row\":2,\"col\":2},{\"row\":4,\"col\":4}]}")
                    .starterCodePython("# Navigate around walls to collect all crystals and reach the portal!\n\n# TODO: Write your code below\n")
                    .starterCodeJava("// Navigate around walls to collect all crystals and reach the portal!\n\n// TODO: Write your code below\n")
                    .build();
        }

        // Pick daily level deterministically based on day of year
        int dayIndex = LocalDate.now().getDayOfYear() % levels.size();
        Level dailyLevel = levels.get(dayIndex);

        return DailyChallengeDto.builder()
                .id(dailyLevel.getId())
                .title("Daily Quest: " + dailyLevel.getTitle())
                .description("Special 2x XP daily featured mission!")
                .difficulty(dailyLevel.getDifficulty() != null ? dailyLevel.getDifficulty().name() : "MEDIUM")
                .concept(dailyLevel.getConcept())
                .missionText(dailyLevel.getMissionText())
                .xpReward(dailyLevel.getXpReward() * 2)
                .coinReward(dailyLevel.getCoinReward() * 2)
                .levelConfig(dailyLevel.getLevelConfig())
                .starterCodePython(dailyLevel.getStarterCode())
                .starterCodeJava(dailyLevel.getStarterCode())
                .build();
    }
}
