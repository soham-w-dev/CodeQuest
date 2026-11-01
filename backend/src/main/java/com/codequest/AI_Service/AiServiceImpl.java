package com.codequest.AI_Service;

import com.codequest.entity.Level;
import com.codequest.repository.LevelRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final LevelRepository levelRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.groq.api-key:}")
    private String configuredApiKey;

    @Value("${app.groq.model:llama-3.3-70b-versatile}")
    private String defaultModel;

    @Value("${app.groq.url:https://api.groq.com/openai/v1/chat/completions}")
    private String groqApiUrl;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    @Override
    public AiResponse debugError(AiRequest request) {
        Level level = getLevelOrNull(request.levelId());
        String apiKey = resolveApiKey();

        if (apiKey != null && !apiKey.isBlank()) {
            try {
                String systemPrompt = getSystemPrompt();
                StringBuilder userPrompt = new StringBuilder();
                userPrompt.append("Task: DEBUG THE PLAYER'S CODE ERROR IN CODEQUEST\n\n");
                if (level != null) {
                    userPrompt.append("Level Title: ").append(level.getTitle()).append("\n");
                    userPrompt.append("Target Concept: ").append(level.getConcept()).append("\n");
                    userPrompt.append("Mission Goal: ").append(level.getMissionText()).append("\n");
                }
                userPrompt.append("Language: ").append(request.language() != null ? request.language() : "PYTHON").append("\n\n");
                userPrompt.append("Player's Current Code:\n```").append(request.language()).append("\n")
                        .append(request.userCode() != null ? request.userCode() : "# No code submitted")
                        .append("\n```\n\n");

                userPrompt.append("Observed Error / Failure Message:\n")
                        .append(request.errorMessage() != null ? request.errorMessage() : "Execution failed without message")
                        .append("\n\n");

                if (request.stdout() != null && !request.stdout().isBlank()) {
                    userPrompt.append("Console Output:\n").append(request.stdout()).append("\n\n");
                }

                userPrompt.append("Instructions:\n")
                        .append("1. Clearly explain in simple, friendly beginner terms WHY this error occurred.\n")
                        .append("2. Identify the exact line or syntax/logic issue causing the problem.\n")
                        .append("3. Provide the corrected code snippet that resolves this error.\n")
                        .append("Format with friendly emojis and clean Markdown headers.");

                AiResponse groqResp = callGroqApi(apiKey, systemPrompt, userPrompt.toString());
                if (groqResp.success()) {
                    return AiResponse.builder()
                            .success(true)
                            .message("AI Error Debugging Analysis Complete")
                            .explanation(groqResp.explanation())
                            .suggestedCode(groqResp.suggestedCode())
                            .build();
                }
            } catch (Exception e) {
                log.warn("Groq AI debug error call failed, falling back to local diagnosis: {}", e.getMessage());
            }
        }

        // Resilient built-in offline debugging fallback
        return generateLocalDebugDiagnosis(request, level);
    }

    @Override
    public AiResponse giveSolution(AiRequest request) {
        Level level = getLevelOrNull(request.levelId());
        String solutionCode = (level != null && level.getSolutionCode() != null)
                ? level.getSolutionCode()
                : (request.userCode() != null ? request.userCode() : "");

        String apiKey = resolveApiKey();

        if (apiKey != null && !apiKey.isBlank()) {
            try {
                String systemPrompt = getSystemPrompt();
                StringBuilder userPrompt = new StringBuilder();
                userPrompt.append("Task: PROVIDE SOLUTION & COMPREHENSIVE STEP-BY-STEP EXPLANATION\n\n");
                if (level != null) {
                    userPrompt.append("Level Title: ").append(level.getTitle()).append("\n");
                    userPrompt.append("Concept: ").append(level.getConcept()).append("\n");
                    userPrompt.append("Mission Goal: ").append(level.getMissionText()).append("\n");
                }
                userPrompt.append("Language: ").append(request.language() != null ? request.language() : "PYTHON").append("\n\n");
                userPrompt.append("Solution Code:\n```").append(request.language()).append("\n")
                        .append(solutionCode)
                        .append("\n```\n\n");

                userPrompt.append("Instructions for Maximum Readability:\n")
                        .append("Format your response with these clear, beautifully formatted Markdown sections:\n\n")
                        .append("## 🎯 Mission Strategy\n")
                        .append("Explain the overall game strategy, start and target coordinates, and how to navigate around walls or collect gems.\n\n")
                        .append("## 🚶 Step-by-Step Code Walkthrough\n")
                        .append("Break down each phase of the solution code. For each phase:\n")
                        .append("- Provide a subheader (### 🔹 Step X: Description)\n")
                        .append("- Quote the exact code snippet in a small code block\n")
                        .append("- Explain what the rover does on the grid (direction, step count, tiles reached, gem collected)\n")
                        .append("- Explain WHY that specific loop, condition, or turn is necessary\n\n")
                        .append("## 💡 Core Concept in Action: ").append(level != null ? level.getConcept() : "Programming Principles").append("\n")
                        .append("Explain how this concept works and why using it is far better than brute-force repetition.\n\n")
                        .append("## ⚠️ Common Pitfalls to Avoid\n")
                        .append("List 1-2 common mistakes players often make on this puzzle.\n\n")
                        .append("Formatting Rules:\n")
                        .append("- Do NOT use raw HTML tags (like <br> or <table>). Use pure clean Markdown.\n")
                        .append("- Use bold text for commands like `move()`, `turn_right()`, and coordinates.\n")
                        .append("- Use bullet points and small code blocks for high readability.");

                AiResponse groqResp = callGroqApi(apiKey, systemPrompt, userPrompt.toString());
                if (groqResp.success()) {
                    return AiResponse.builder()
                            .success(true)
                            .message("Solution Walkthrough Generated")
                            .explanation(groqResp.explanation())
                            .suggestedCode(solutionCode)
                            .build();
                }
            } catch (Exception e) {
                log.warn("Groq AI give solution call failed, falling back to local explanation: {}", e.getMessage());
            }
        }

        // Resilient built-in offline solution walkthrough
        return generateLocalSolutionWalkthrough(level, solutionCode, request.language());
    }

    @Override
    public AiResponse explainConcept(AiRequest request) {
        Level level = getLevelOrNull(request.levelId());
        String concept = (level != null && level.getConcept() != null) ? level.getConcept() : "Programming Fundamentals";
        String apiKey = resolveApiKey();

        if (apiKey != null && !apiKey.isBlank()) {
            try {
                String systemPrompt = getSystemPrompt();
                StringBuilder userPrompt = new StringBuilder();
                userPrompt.append("Task: EXPLAIN THE PROGRAMMING CONCEPTS USED IN THIS QUESTION\n\n");
                if (level != null) {
                    userPrompt.append("Question / Level Title: ").append(level.getTitle()).append("\n");
                    userPrompt.append("Target Concept: ").append(concept).append("\n");
                    userPrompt.append("Question Mission: ").append(level.getMissionText()).append("\n");
                }
                userPrompt.append("Programming Language: ").append(request.language() != null ? request.language() : "PYTHON").append("\n\n");
                userPrompt.append("Instructions:\n")
                        .append("1. In-depth explanation of the concept: What is it, and why does it exist in computer science?\n")
                        .append("2. How is this concept applied in this specific question?\n")
                        .append("3. Syntax guide with examples in ").append(request.language() != null ? request.language() : "PYTHON").append(".\n")
                        .append("4. Common pitfalls and mistakes beginners make with this concept.\n")
                        .append("Format in Markdown with headers, bullet points, and code snippets.");

                AiResponse groqResp = callGroqApi(apiKey, systemPrompt, userPrompt.toString());
                if (groqResp.success()) {
                    return AiResponse.builder()
                            .success(true)
                            .message("Concept Explanation Generated")
                            .conceptTitle(concept)
                            .explanation(groqResp.explanation())
                            .build();
                }
            } catch (Exception e) {
                log.warn("Groq AI explain concept call failed, falling back to local concept knowledge: {}", e.getMessage());
            }
        }

        // Resilient built-in offline concept explanation
        return generateLocalConceptExplanation(level, concept, request.language());
    }

    @Override
    public AiResponse getHint(AiRequest request) {
        Level level = getLevelOrNull(request.levelId());
        String apiKey = resolveApiKey();

        if (apiKey != null && !apiKey.isBlank()) {
            try {
                String systemPrompt = getSystemPrompt();
                StringBuilder userPrompt = new StringBuilder();
                userPrompt.append("Task: PROVIDE A TACTICAL HINT FOR CODEQUEST\n\n");
                if (level != null) {
                    userPrompt.append("Level: ").append(level.getTitle()).append("\n");
                    userPrompt.append("Concept: ").append(level.getConcept()).append("\n");
                    userPrompt.append("Mission Goal: ").append(level.getMissionText()).append("\n");
                }
                userPrompt.append("Language: ").append(request.language() != null ? request.language() : "PYTHON").append("\n\n");
                userPrompt.append("Current Code:\n```").append(request.language()).append("\n")
                        .append(request.userCode() != null ? request.userCode() : "# Empty")
                        .append("\n```\n\n");
                userPrompt.append("Provide a smart, progressive tactical hint that guides the student without revealing the whole answer.");

                AiResponse groqResp = callGroqApi(apiKey, systemPrompt, userPrompt.toString());
                if (groqResp.success()) {
                    return groqResp;
                }
            } catch (Exception e) {
                log.warn("Groq AI hint call failed: {}", e.getMessage());
            }
        }

        return AiResponse.builder()
                .success(true)
                .message("Tactical Hint")
                .explanation("💡 **Tactical Hint:**\n\nLook closely at your rover's starting coordinates and the goal tile. Break down the path into simple movements. If a segment repeats multiple tiles in a row, consider using a loop to write cleaner code!")
                .build();
    }

    // --- Private Helper & Engine Methods ---

    private AiResponse callGroqApi(String apiKey, String systemPrompt, String userPrompt) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("model", defaultModel);
            payload.put("temperature", 0.3);
            payload.put("max_tokens", 1200);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            messages.add(Map.of("role", "user", "content", userPrompt));
            payload.put("messages", messages);

            String requestBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(groqApiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey.trim())
                    .header("User-Agent", "CodeQuest-AI/1.0")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode choices = root.get("choices");
                if (choices != null && choices.isArray() && choices.size() > 0) {
                    JsonNode msg = choices.get(0).path("message");
                    String content = msg.path("content").asText("");
                    if (content.isBlank() && msg.has("reasoning")) {
                        content = msg.path("reasoning").asText("");
                    }
                    return AiResponse.builder()
                            .success(true)
                            .message("AI analysis complete")
                            .explanation(content)
                            .build();
                }
            }
            log.warn("Groq API returned HTTP {}: {}", response.statusCode(), response.body());
        } catch (Exception e) {
            log.error("Failed communicating with Groq API", e);
        }

        return AiResponse.builder()
                .success(false)
                .error("API_CALL_FAILED")
                .build();
    }

    private AiResponse generateLocalDebugDiagnosis(AiRequest request, Level level) {
        String err = request.errorMessage() != null ? request.errorMessage() : "Execution failed";
        String userCode = request.userCode() != null ? request.userCode() : "";
        String lang = request.language() != null ? request.language().toUpperCase() : "PYTHON";

        StringBuilder sb = new StringBuilder();
        sb.append("### 🔍 Byte's Debug Analysis\n\n");

        if (err.contains("SyntaxError") || err.contains("invalid syntax")) {
            sb.append("**Syntax Error Detected:**\n");
            if (lang.contains("PYTHON") && !userCode.contains(":")) {
                sb.append("- In Python, compound statements such as `for`, `while`, `if`, and `def` **must end with a colon `:`**.\n");
                sb.append("- Ensure every loop header or conditional header ends with a colon `:`.\n");
            } else {
                sb.append("- Check your syntax around parentheses, colons, or indentation.\n");
            }
        } else if (err.contains("IndentationError")) {
            sb.append("**Indentation Error:**\n");
            sb.append("- Python relies on whitespace indentation to define code blocks.\n");
            sb.append("- Make sure all lines inside loops or functions are consistently indented by 4 spaces.\n");
        } else if (err.contains(";") || err.contains("expected ';'")) {
            sb.append("**Missing Semicolon (Java):**\n");
            sb.append("- Every executable statement in Java must end with a semicolon `;`.\n");
            sb.append("- Check your `move();`, `turnRight();`, and variable assignments.\n");
        } else if (err.contains("wall") || err.contains("obstacle") || err.contains("collided")) {
            sb.append("**Obstacle Collision:**\n");
            sb.append("- The rover collided with an impassable stone wall!\n");
            sb.append("- You must steer around obstacles using `turn_right()` / `turn_left()` before stepping forward.\n");
        } else if (err.contains("boundary") || err.contains("grid") || err.contains("out of bounds")) {
            sb.append("**Grid Boundary Exceeded:**\n");
            sb.append("- The rover walked off the edge of the world map!\n");
            sb.append("- Check the number of steps in your loops to ensure you don't overshoot the goal.\n");
        } else if (err.contains("gem") || err.contains("crystal")) {
            sb.append("**Missing Collectibles:**\n");
            sb.append("- Not all required gems/crystals were collected!\n");
            sb.append("- Remember to call `collect()` (or `collect();` in Java) while standing on each crystal tile.\n");
        } else {
            sb.append("**Execution Issue:**\n");
            sb.append("- Observed message: `").append(err).append("`\n");
            sb.append("- Compare your code against the level goal. Ensure commands are spelled correctly with matching parentheses.\n");
        }

        if (level != null && level.getSolutionCode() != null) {
            sb.append("\n**💡 Guidance Tip:**\n");
            sb.append("This mission focuses on **").append(level.getConcept()).append("**.\n");
            sb.append("Ensure you follow the required sequence of movements and commands to reach the portal safely.");
        }

        return AiResponse.builder()
                .success(true)
                .message("Debug Analysis (Backend Engine)")
                .explanation(sb.toString())
                .suggestedCode(level != null ? level.getSolutionCode() : null)
                .build();
    }

    private AiResponse generateLocalSolutionWalkthrough(Level level, String solutionCode, String language) {
        String title = level != null ? level.getTitle() : "Level Solution";
        String concept = level != null ? level.getConcept() : "Programming Fundamentals";
        String mission = level != null ? level.getMissionText() : "";
        String lang = language != null ? language.toLowerCase() : "python";
        String code = solutionCode != null ? solutionCode : "";

        StringBuilder sb = new StringBuilder();
        sb.append("## 🎯 Mission Strategy: ").append(title).append("\n\n");
        sb.append("**Target Concept:** `").append(concept).append("`\n\n");
        if (!mission.isBlank()) {
            sb.append("> 📌 **Objective:** ").append(mission).append("\n\n");
        }

        sb.append("## 🚶 Step-by-Step Code Walkthrough\n\n");

        List<String> stepCards = buildStepBreakdown(code, lang);
        if (!stepCards.isEmpty()) {
            for (String card : stepCards) {
                sb.append(card).append("\n\n");
            }
        } else {
            sb.append("### 🔹 Phase 1: Grid Navigation & Execution\n");
            sb.append("```").append(lang).append("\n").append(code).append("\n```\n");
            sb.append("- **Movement Plan:** Guides the rover forward across tiles, turning to avoid barriers and reach the goal.\n");
            sb.append("- **Concept Application:** Applies **").append(concept).append("** to solve the puzzle cleanly.\n\n");
        }

        sb.append("## 💡 Core Concept: ").append(concept).append("\n\n");
        sb.append(getConceptExplanationSummary(concept, lang)).append("\n\n");

        sb.append("## ⚠️ Key Tips & Common Pitfalls\n\n");
        sb.append("- **Orientation Awareness:** The rover only advances in the direction it is currently facing. A `turn_right()` / `turnRight()` rotates 90° clockwise, while `turn_left()` / `turnLeft()` rotates 90° counter-clockwise.\n");
        sb.append("- **Avoid Collisions:** Always plan your turn commands BEFORE reaching stone walls or grid boundaries.\n");
        sb.append("- **Collectibles:** If energy crystals are present, call `collect()` while standing directly on the crystal tile before stepping into the portal.");

        return AiResponse.builder()
                .success(true)
                .message("Solution Walkthrough (Backend Engine)")
                .explanation(sb.toString())
                .suggestedCode(solutionCode)
                .build();
    }

    private List<String> buildStepBreakdown(String code, String lang) {
        List<String> steps = new ArrayList<>();
        if (code == null || code.isBlank()) return steps;

        String[] lines = code.split("\r?\n");
        String currentTitle = null;
        StringBuilder currentBlock = new StringBuilder();
        int stepCount = 0;

        for (String line : lines) {
            String trimmed = line.trim();
            boolean isStepComment = (trimmed.startsWith("#") || trimmed.startsWith("//")) &&
                    (trimmed.matches("(?i)[#/]+\\s*(step|segment|phase|gem|sweep|part|final).*") ||
                     trimmed.matches("(?i)[#/]+\\s*---.*"));

            if (isStepComment) {
                if (currentBlock.length() > 0 && currentTitle != null) {
                    steps.add(formatStepCard(stepCount, currentTitle, currentBlock.toString().trim(), lang));
                    currentBlock.setLength(0);
                }
                stepCount++;
                currentTitle = trimmed.replaceAll("^[#/\\-\\s]+", "").replaceAll("[#/\\-\\s]+$", "");
            } else if (!trimmed.isEmpty() && !trimmed.startsWith("#") && !trimmed.startsWith("//")) {
                currentBlock.append(line).append("\n");
            }
        }

        if (currentBlock.length() > 0) {
            if (currentTitle == null) {
                currentTitle = "Code Execution";
                stepCount = 1;
            }
            steps.add(formatStepCard(stepCount, currentTitle, currentBlock.toString().trim(), lang));
        }

        return steps;
    }

    private String formatStepCard(int stepNum, String title, String snippet, String lang) {
        StringBuilder sb = new StringBuilder();
        sb.append("### 🔹 Step ").append(stepNum).append(": ").append(title).append("\n\n");
        sb.append("```").append(lang).append("\n").append(snippet).append("\n```\n\n");

        sb.append("- **Action:** ");
        if (snippet.contains("for") || snippet.contains("while")) {
            sb.append("Executes repetitive movements efficiently using a loop construct.\n");
        } else if (snippet.contains("turn")) {
            sb.append("Changes the rover's heading to adjust course around grid terrain.\n");
        } else if (snippet.contains("collect")) {
            sb.append("Gathers the energy crystal to power the exit portal.\n");
        } else if (snippet.contains("def") || snippet.contains("void")) {
            sb.append("Defines a modular helper function to reuse movement patterns.\n");
        } else {
            sb.append("Advances the rover forward across the corridor.\n");
        }
        return sb.toString();
    }

    private String getConceptExplanationSummary(String concept, String lang) {
        String lower = concept.toLowerCase();
        if (lower.contains("sequencing") || lower.contains("statement")) {
            return "Sequencing ensures instructions execute strictly in order from top to bottom. Order matters: moving before turning yields a completely different coordinate than turning before moving.";
        } else if (lower.contains("for loop") || lower.contains("repetition")) {
            return "For loops apply the DRY (Don't Repeat Yourself) principle. Instead of writing multiple identical `move()` commands, a loop executes the action the exact number of times needed in just 2 lines.";
        } else if (lower.contains("variable") || lower.contains("primitive")) {
            return "Variables store distances or state in memory. Changing a single variable automatically updates all movements and loops that depend on it, making your code flexible and easy to maintain.";
        } else if (lower.contains("direction") || lower.contains("obstacle") || lower.contains("maze")) {
            return "Direction control allows spatial steering around impassable walls. Combining `turn_right()` and `turn_left()` allows navigation across any 2D coordinate space.";
        } else if (lower.contains("conditional") || lower.contains("if") || lower.contains("branch")) {
            return "Conditionals (`if` statements) evaluate conditions at runtime. Here, checking the current step count allows the rover to execute specific turning maneuvers at the exact fork in the path.";
        } else if (lower.contains("while")) {
            return "While loops iterate dynamically based on a condition test. Using a counter variable (`count < 5`), the rover advances tile-by-tile until the destination condition is satisfied.";
        } else if (lower.contains("function") || lower.contains("method") || lower.contains("modular")) {
            return "Functions encapsulate movement logic into reusable blocks. By accepting distance parameters, a single function can handle different legs of a journey with zero code duplication.";
        } else if (lower.contains("boss") || lower.contains("nested") || lower.contains("gauntlet") || lower.contains("spiral")) {
            return "Advanced navigation combines loops, turns, and conditional logic to conquer complex labyrinths with minimal statements and maximum efficiency.";
        }
        return "This concept teaches structured algorithmic problem solving, helping you navigate complex spatial puzzles cleanly and efficiently.";
    }

    private AiResponse generateLocalConceptExplanation(Level level, String concept, String language) {
        String lang = language != null ? language.toUpperCase() : "PYTHON";
        StringBuilder sb = new StringBuilder();

        sb.append("### 📚 Concept Deep Dive: ").append(concept).append("\n\n");

        if (concept.toLowerCase().contains("sequencing") || concept.toLowerCase().contains("first steps")) {
            sb.append("#### What is Sequencing?\n");
            sb.append("**Sequencing** is the foundational principle of computer programming. A computer program is an ordered list of instructions executed step-by-step from top to bottom.\n\n");
            sb.append("#### Why is Sequencing Important?\n");
            sb.append("- Order matters! Moving before turning leads to a completely different location than turning before moving.\n");
            sb.append("- Each command transitions the world from one state to the next.\n\n");
            sb.append("#### Syntax Example (").append(lang).append("):\n");
            if (lang.contains("JAVA")) {
                sb.append("```java\nmove();\nmove();\nmove();\n```\n");
            } else {
                sb.append("```python\nmove()\nmove()\nmove()\n```\n");
            }
        } else if (concept.toLowerCase().contains("for loop") || concept.toLowerCase().contains("repetition")) {
            sb.append("#### What is a For Loop?\n");
            sb.append("A **For Loop** allows you to repeat a block of code a specific number of times without having to write duplicate statements.\n\n");
            sb.append("#### The DRY Principle (Don't Repeat Yourself):\n");
            sb.append("- Instead of typing `move()` 100 times, a loop lets you express the exact same action in just 2 lines.\n");
            sb.append("- Loops make your code shorter, easier to read, and less prone to typos.\n\n");
            sb.append("#### Syntax Example (").append(lang).append("):\n");
            if (lang.contains("JAVA")) {
                sb.append("```java\nfor (int i = 0; i < 6; i++) {\n    move();\n}\n```\n");
            } else {
                sb.append("```python\nfor i in range(6):\n    move()\n```\n");
            }
        } else if (concept.toLowerCase().contains("variable")) {
            sb.append("#### What is a Variable?\n");
            sb.append("A **Variable** is a named storage container in computer memory that holds a value (like numbers, text, or coordinates) so you can reference and reuse it throughout your program.\n\n");
            sb.append("#### Why Use Variables?\n");
            sb.append("- Flexibility: Change the variable value once, and all loops or calculations using it update automatically.\n");
            sb.append("- Readability: Giving meaningful names (e.g., `steps = 4`) makes your code self-documenting.\n\n");
            sb.append("#### Syntax Example (").append(lang).append("):\n");
            if (lang.contains("JAVA")) {
                sb.append("```java\nint steps = 4;\nfor (int i = 0; i < steps; i++) {\n    move();\n}\n```\n");
            } else {
                sb.append("```python\nsteps = 4\nfor i in range(steps):\n    move()\n```\n");
            }
        } else if (concept.toLowerCase().contains("conditional") || concept.toLowerCase().contains("if")) {
            sb.append("#### What is Conditional Branching (if / else)?\n");
            sb.append("Conditionals enable programs to make **decisions** dynamically based on conditions that evaluate to `true` or `false`.\n\n");
            sb.append("#### In This Mission:\n");
            sb.append("- As your loop index advances, you test `if step == 2:` (or `if (step == 2)` in Java).\n");
            sb.append("- When the condition is met, the rover executes a special action (like turning).\n\n");
            sb.append("#### Syntax Example (").append(lang).append("):\n");
            if (lang.contains("JAVA")) {
                sb.append("```java\nif (step == 2) {\n    turnRight();\n}\n```\n");
            } else {
                sb.append("```python\nif step == 2:\n    turn_right()\n```\n");
            }
        } else if (concept.toLowerCase().contains("while")) {
            sb.append("#### What is a While Loop?\n");
            sb.append("A **While Loop** repeatedly executes a block of code **as long as a specified condition remains true**.\n\n");
            sb.append("#### Key Components of a While Loop:\n");
            sb.append("1. **Initialization:** Set up a counter (e.g., `count = 0`).\n");
            sb.append("2. **Condition Test:** Check if `count < 5` before every repetition.\n");
            sb.append("3. **Update / Increment:** Modify `count` inside the loop body (`count += 1`) so the loop eventually terminates and doesn't run forever!\n\n");
            sb.append("#### Syntax Example (").append(lang).append("):\n");
            if (lang.contains("JAVA")) {
                sb.append("```java\nint count = 0;\nwhile (count < 5) {\n    move();\n    count++;\n}\n```\n");
            } else {
                sb.append("```python\ncount = 0\nwhile count < 5:\n    move()\n    count += 1\n```\n");
            }
        } else if (concept.toLowerCase().contains("function")) {
            sb.append("#### What is a Function?\n");
            sb.append("A **Function** is a reusable, named block of code that performs a specific task. Functions can accept **parameters** to customize their behavior.\n\n");
            sb.append("#### Why Use Functions?\n");
            sb.append("- Modularity: Break large, complicated missions into small, understandable chunks.\n");
            sb.append("- Reusability: Write `advance(n)` once, then call `advance(3)` anywhere with different distances.\n\n");
            sb.append("#### Syntax Example (").append(lang).append("):\n");
            if (lang.contains("JAVA")) {
                sb.append("```java\nvoid advance(int n) {\n    for (int i = 0; i < n; i++) {\n        move();\n    }\n}\n```\n");
            } else {
                sb.append("```python\ndef advance(n):\n    for i in range(n):\n        move()\n```\n");
            }
        } else {
            sb.append("#### Overview of ").append(concept).append("\n");
            sb.append("This level challenges you to combine core coding constructs to solve spatial and algorithmic puzzles.\n\n");
            sb.append("Key elements demonstrated:\n");
            sb.append("- State tracking and movement verification\n");
            sb.append("- Algorithmic path planning\n");
            sb.append("- Efficient syntax and clean code structure\n");
        }

        return AiResponse.builder()
                .success(true)
                .message("Concept Explanation (Backend Engine)")
                .conceptTitle(concept)
                .explanation(sb.toString())
                .build();
    }

    private String resolveApiKey() {
        if (configuredApiKey != null && !configuredApiKey.trim().isEmpty()) {
            return configuredApiKey.trim();
        }
        String envKey = System.getenv("GROQ_API_KEY");
        if (envKey != null && !envKey.trim().isEmpty()) {
            return envKey.trim();
        }
        return null;
    }

    private Level getLevelOrNull(Long levelId) {
        if (levelId == null) return null;
        return levelRepository.findById(levelId).orElse(null);
    }

    private String getSystemPrompt() {
        return "You are Byte, the friendly, super-intelligent AI companion and coding tutor in 'CodeQuest'.\n" +
                "CodeQuest is an educational adventure game where students learn programming by guiding a rover across a 2D tile grid.\n" +
                "The rover can perform commands like move(), turn_left(), turn_right(), and collect() in Python, or move(); turnRight(); turnLeft(); collect(); in Java.\n" +
                "Your role is to:\n" +
                "- Teach programming concepts with patience, enthusiasm, and clarity.\n" +
                "- When debugging errors, pinpoint the exact line, explain why it happened in beginner-friendly terms, and show the exact fix.\n" +
                "- When explaining solutions, break down the code step-by-step with clear logic.\n" +
                "- When explaining concepts, provide real-world analogies, syntax guides, and common pitfalls.\n" +
                "- Use clean Markdown formatting, code snippets, and cheerful emojis.";
    }
}
