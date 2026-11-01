package com.codequest.engine;

import com.codequest.dto.GameExecutionResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameEvaluationService {

    private final ObjectMapper objectMapper;

    public GameExecutionResult evaluate(
            CodeExecutionService.ExecutionResult result,
            String levelConfigJson,
            String language,
            String userCode,
            String levelTitle,
            String levelConcept
    ) {
        String lang = language != null ? language.toUpperCase() : "PYTHON";
        String[] codeLines = userCode != null ? userCode.split("\n", -1) : new String[0];

        if (!result.success()) {
            String rawErr = result.errorOutput() != null && !result.errorOutput().isBlank() ? result.errorOutput() : result.output();
            
            // Extract error line and explanation
            Integer errorLine = extractErrorLine(rawErr, lang, codeLines.length);
            String lineContent = getLineContent(codeLines, errorLine);
            String explanation = generateLineErrorExplanation(rawErr, errorLine, lineContent, lang);

            String cleanTrace = cleanErrorTrace(rawErr);
            String out = result.output() != null && !result.output().isBlank() ? result.output() : cleanTrace;

            return GameExecutionResult.builder()
                    .success(false)
                    .error(true)
                    .errorMessage(explanation)
                    .errorLine(errorLine)
                    .errorExplanation(explanation)
                    .stdout(out)
                    .build();
        }

        String output = result.output();
        List<GameExecutionResult.Action> actions = new ArrayList<>();
        String stdout = "";

        if (output != null && output.contains("__ACTIONS__")) {
            String[] parts = output.split("__ACTIONS__");
            stdout = parts[0].trim();
            if (parts.length > 1) {
                String actionsJson = parts[1].trim();
                try {
                    actions = objectMapper.readValue(actionsJson, new TypeReference<>() {});
                } catch (Exception e) {
                    log.error("Failed to parse actions JSON", e);
                }
            }
        } else {
            stdout = output != null ? output.trim() : "";
        }

        boolean hasRuntimeError = false;
        String runtimeErrorMessage = null;
        Integer runtimeErrorLine = null;

        for (var act : actions) {
            if ("error".equals(act.type())) {
                hasRuntimeError = true;
                runtimeErrorMessage = act.details();
                break;
            }
        }

        if (hasRuntimeError) {
            Integer line = extractErrorLine(runtimeErrorMessage, lang, codeLines.length);
            String lineContent = getLineContent(codeLines, line);
            String explanation = generateLineErrorExplanation(runtimeErrorMessage, line, lineContent, lang);

            return GameExecutionResult.builder()
                    .success(false)
                    .error(true)
                    .errorMessage(explanation)
                    .errorLine(line)
                    .errorExplanation(explanation)
                    .actions(actions)
                    .stdout(stdout)
                    .build();
        }

        return evaluateSimulation(actions, levelConfigJson, stdout, levelConcept);
    }

    private GameExecutionResult evaluateSimulation(
            List<GameExecutionResult.Action> actions,
            String levelConfigJson,
            String stdout,
            String levelConcept
    ) {
        try {
            JsonNode config = objectMapper.readTree(levelConfigJson);
            JsonNode start = config.get("playerStart");
            JsonNode goal = config.get("goal");
            JsonNode walls = config.get("walls");
            JsonNode gems = config.get("gems");
            int rows = config.has("rows") ? config.get("rows").asInt() : 5;
            int cols = config.has("cols") ? config.get("cols").asInt() : 5;

            int startR = start.get("row").asInt();
            int startC = start.get("col").asInt();
            int r = startR;
            int c = startC;
            String dir = config.get("playerDirection").asText(); // N, E, S, W
            
            int goalR = goal.get("row").asInt();
            int goalC = goal.get("col").asInt();

            boolean goalReached = false;
            int gemsCollected = 0;
            int totalGems = gems != null ? gems.size() : 0;
            List<String> collectedGemCoords = new ArrayList<>();

            for (var action : actions) {
                if ("move".equals(action.type())) {
                    int prevR = r;
                    int prevC = c;

                    if ("N".equals(dir)) r--;
                    else if ("S".equals(dir)) r++;
                    else if ("E".equals(dir)) c++;
                    else if ("W".equals(dir)) c--;

                    // check boundary out of bounds
                    if (r < 0 || r >= rows || c < 0 || c >= cols) {
                        String errMsg = "Rover walked off the grid boundary at (" + r + ", " + c + ")!";
                        String hint = "💡 Tactical Hint: Your rover stepped off the map grid edge. Reduce your forward movements or check your loop count so the rover stays inside the grid.";
                        return buildResult(false, false, errMsg, null, hint, false, gemsCollected, totalGems, actions, 0, stdout);
                    }

                    // check wall collision
                    if (walls != null) {
                        for (JsonNode w : walls) {
                            if (w.get("row").asInt() == r && w.get("col").asInt() == c) {
                                String errMsg = "Collision! Rover hit an impassable stone wall at (" + r + ", " + c + ").";
                                String hint = "💡 Tactical Hint: There is a stone wall at row " + r + ", col " + c + ". Turn using turn_right() or turn_left() before this tile to steer around the barrier!";
                                return buildResult(false, false, errMsg, null, hint, false, gemsCollected, totalGems, actions, 0, stdout);
                            }
                        }
                    }
                } else if ("turn_left".equals(action.type())) {
                    if ("N".equals(dir)) dir = "W";
                    else if ("W".equals(dir)) dir = "S";
                    else if ("S".equals(dir)) dir = "E";
                    else if ("E".equals(dir)) dir = "N";
                } else if ("turn_right".equals(action.type())) {
                    if ("N".equals(dir)) dir = "E";
                    else if ("E".equals(dir)) dir = "S";
                    else if ("S".equals(dir)) dir = "W";
                    else if ("W".equals(dir)) dir = "N";
                } else if ("collect".equals(action.type())) {
                    if (gems != null) {
                        for (JsonNode g : gems) {
                            if (g.get("row").asInt() == r && g.get("col").asInt() == c) {
                                String coord = r + "," + c;
                                if (!collectedGemCoords.contains(coord)) {
                                    gemsCollected++;
                                    collectedGemCoords.add(coord);
                                }
                            }
                        }
                    }
                }

                // Check goal
                if (r == goalR && c == goalC) {
                    goalReached = true;
                }
            }

            boolean allGemsCollected = (totalGems == 0 || gemsCollected == totalGems);
            boolean success = goalReached && allGemsCollected;

            String errMsg = null;
            String hint = null;

            if (!success) {
                if (!goalReached) {
                    int dRow = goalR - r;
                    int dCol = goalC - c;
                    StringBuilder hintSb = new StringBuilder();
                    hintSb.append("💡 Tactical Hint: ");
                    hintSb.append("Your code ran successfully, but the rover stopped at (row ").append(r).append(", col ").append(c).append(")");
                    hintSb.append(" and did not reach the goal portal at (row ").append(goalR).append(", col ").append(goalC).append(").\n");

                    if (r == goalR) {
                        int stepsNeeded = Math.abs(dCol);
                        String stepDir = dCol > 0 ? "East (right)" : "West (left)";
                        hintSb.append("👉 You are in the correct row! You need ").append(stepsNeeded).append(" more forward move() commands to step ").append(stepDir).append(" into the portal.");
                    } else if (c == goalC) {
                        int stepsNeeded = Math.abs(dRow);
                        String stepDir = dRow > 0 ? "South (down)" : "North (up)";
                        hintSb.append("👉 You are in the correct column! You need to turn and move ").append(stepsNeeded).append(" steps ").append(stepDir).append(" into the portal.");
                    } else {
                        hintSb.append("👉 Adjust your movement path to navigate from (").append(r).append(", ").append(c).append(") to (").append(goalR).append(", ").append(goalC).append(").");
                    }

                    if (levelConcept != null && !levelConcept.isBlank()) {
                        hintSb.append("\n📌 Concept tip: Make sure to utilize ").append(levelConcept).append(" to repeat or control your steps efficiently.");
                    }

                    errMsg = "Goal Not Reached: Rover stopped at (" + r + ", " + c + ").";
                    hint = hintSb.toString();
                } else if (!allGemsCollected) {
                    errMsg = "Incomplete Mission: Collected " + gemsCollected + " of " + totalGems + " energy crystals.";
                    hint = "💡 Tactical Hint: The rover reached the portal, but forgot to gather all " + totalGems + " energy crystals! Call collect() when standing on crystal tiles before stepping into the portal.";
                }
            }

            int stars = success ? (gemsCollected == totalGems ? 3 : 2) : 0;
            return buildResult(success, false, errMsg, null, hint, goalReached, gemsCollected, totalGems, actions, stars, stdout);

        } catch (Exception e) {
            log.error("Simulation error", e);
            return buildResult(false, true, "Simulation error: " + e.getMessage(), null, null, false, 0, 0, actions, 0, stdout);
        }
    }

    private Integer extractErrorLine(String rawError, String language, int totalLines) {
        if (rawError == null) return null;

        if ("PYTHON".equalsIgnoreCase(language)) {
            // Python wrapper prefix offset is 47 lines
            Pattern pyPattern = Pattern.compile("line (\\d+)");
            Matcher pyMatcher = pyPattern.matcher(rawError);
            int lastFound = -1;
            while (pyMatcher.find()) {
                lastFound = Integer.parseInt(pyMatcher.group(1));
            }
            if (lastFound != -1) {
                int userLine = lastFound - 47;
                if (userLine >= 1 && (totalLines == 0 || userLine <= totalLines + 5)) {
                    return Math.max(1, userLine);
                }
            }
        } else {
            // Java wrapper prefix offset is 42 lines (Solution.java has 42 lines before user code)
            Pattern javaPattern = Pattern.compile("Solution\\.java:(\\d+):");
            Matcher javaMatcher = javaPattern.matcher(rawError);
            if (javaMatcher.find()) {
                int fileLine = Integer.parseInt(javaMatcher.group(1));
                int userLine = fileLine - 42;
                if (userLine >= 1) {
                    return Math.max(1, userLine);
                }
            }
        }

        return null;
    }

    private String getLineContent(String[] lines, Integer lineNum) {
        if (lineNum == null || lines == null || lineNum < 1 || lineNum > lines.length) {
            return null;
        }
        return lines[lineNum - 1].trim();
    }

    private String generateLineErrorExplanation(String rawError, Integer errorLine, String lineContent, String language) {
        StringBuilder sb = new StringBuilder();

        if (errorLine != null) {
            sb.append("❌ Error on Line ").append(errorLine).append(":\n");
            if (lineContent != null && !lineContent.isBlank()) {
                sb.append("   Code: `").append(lineContent).append("`\n\n");
            }
        } else {
            sb.append("❌ Execution Error Detected:\n\n");
        }

        if (rawError != null) {
            if (rawError.contains("SyntaxError: expected ':'") || rawError.contains("expected ':'")) {
                sb.append("• Missing Colon (`:`):\n");
                sb.append("  In Python, header statements like `for`, `while`, `if`, and `def` must end with a colon (`:`).\n");
                if (errorLine != null) {
                    sb.append("  👉 Add a colon `:` at the end of line ").append(errorLine).append(".");
                }
            } else if (rawError.contains("IndentationError")) {
                sb.append("• Indentation Error:\n");
                sb.append("  Python requires statements inside loops, conditionals, or functions to be indented (typically 4 spaces).\n");
                if (errorLine != null) {
                    sb.append("  👉 Check the spacing on line ").append(errorLine).append(" to ensure it is properly indented.");
                }
            } else if (rawError.contains("NameError")) {
                sb.append("• Unknown Name or Command:\n");
                sb.append("  A variable or command used on this line is not defined.\n");
                sb.append("  👉 Ensure commands are spelled correctly: `move()`, `turn_right()`, `turn_left()`, `collect()`.");
            } else if (rawError.contains("';' expected") || rawError.contains("; expected")) {
                sb.append("• Missing Semicolon (`;`):\n");
                sb.append("  In Java, every executable statement must end with a semicolon (`;`).\n");
                if (errorLine != null) {
                    sb.append("  👉 Add a semicolon `;` at the end of line ").append(errorLine).append(".");
                }
            } else if (rawError.contains("cannot find symbol")) {
                sb.append("• Unrecognized Symbol or Method:\n");
                sb.append("  Java does not recognize the method or variable name.\n");
                sb.append("  👉 Check spelling and camelCase: `move();`, `turnRight();`, `turnLeft();`, `collect();`.");
            } else if (rawError.contains("Too many actions") || rawError.contains("timed out")) {
                sb.append("• Infinite Loop Detected:\n");
                sb.append("  Your code exceeded the maximum allowed steps. Check your while or for loop condition to make sure it terminates.");
            } else {
                sb.append("• Error Details:\n");
                sb.append("  ").append(cleanErrorTrace(rawError));
            }
        }

        return sb.toString();
    }

    private String cleanErrorTrace(String rawError) {
        if (rawError == null) return "";
        String cleaned = rawError.replaceAll("(?i)[a-z]:\\\\[^\\n\r]+\\\\script_[^\\n\r]+\\.py", "main.py");
        cleaned = cleaned.replaceAll("(?i)[a-z]:\\\\[^\\n\r]+\\\\Solution\\.java", "Solution.java");

        try {
            Pattern pyPattern = Pattern.compile("line (\\d+)");
            Matcher pyMatcher = pyPattern.matcher(cleaned);
            StringBuilder sb = new StringBuilder();
            while (pyMatcher.find()) {
                int line = Integer.parseInt(pyMatcher.group(1));
                int userLine = Math.max(1, line - 47);
                pyMatcher.appendReplacement(sb, "line " + userLine);
            }
            pyMatcher.appendTail(sb);
            cleaned = sb.toString();
        } catch (Exception ignored) {}

        try {
            Pattern javaPattern = Pattern.compile("Solution\\.java:(\\d+):");
            Matcher javaMatcher = javaPattern.matcher(cleaned);
            StringBuilder sb = new StringBuilder();
            while (javaMatcher.find()) {
                int line = Integer.parseInt(javaMatcher.group(1));
                int userLine = Math.max(1, line - 42);
                javaMatcher.appendReplacement(sb, "Solution.java: line " + userLine + ":");
            }
            javaMatcher.appendTail(sb);
            cleaned = sb.toString();
        } catch (Exception ignored) {}

        return cleaned;
    }

    private GameExecutionResult buildResult(
            boolean success,
            boolean error,
            String errorMsg,
            Integer errorLine,
            String hint,
            boolean goalReached,
            int gems,
            int totalGems,
            List<GameExecutionResult.Action> actions,
            int stars,
            String stdout
    ) {
        return GameExecutionResult.builder()
                .success(success)
                .error(error)
                .errorMessage(errorMsg)
                .errorLine(errorLine)
                .errorExplanation(errorMsg)
                .hint(hint)
                .goalReached(goalReached)
                .gemsCollected(gems)
                .totalGems(totalGems)
                .actions(actions)
                .stars(stars)
                .stdout(stdout)
                .build();
    }
}
