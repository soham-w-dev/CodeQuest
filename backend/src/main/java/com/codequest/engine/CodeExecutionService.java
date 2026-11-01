package com.codequest.engine;

import com.codequest.exception.GameExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeExecutionService {

    @Value("${app.code-execution.timeout-ms:10000}")
    private long timeoutMs;

    @Value("${app.code-execution.temp-dir:./temp-execution}")
    private String tempDir;

    private final CodeWrapperService wrapperService;

    public ExecutionResult executeCode(String language, String userCode) {
        String wrappedCode;
        if ("PYTHON".equalsIgnoreCase(language)) {
            wrappedCode = wrapperService.wrapPython(userCode);
            return runPython(wrappedCode);
        } else if ("JAVA".equalsIgnoreCase(language)) {
            wrappedCode = wrapperService.wrapJava(userCode);
            return runJava(wrappedCode);
        } else {
            throw new GameExecutionException("Unsupported language: " + language);
        }
    }

    private ExecutionResult runPython(String wrappedCode) {
        File tempFile = null;
        try {
            ensureTempDir();
            tempFile = new File(tempDir, "script_" + UUID.randomUUID() + ".py");
            Files.writeString(tempFile.toPath(), wrappedCode);

            ProcessBuilder pb = new ProcessBuilder("python", tempFile.getAbsolutePath());
            return runProcess(pb);
        } catch (Exception e) {
            log.warn("Python execution issue: {}", e.getMessage());
            return new ExecutionResult(false, "", "Python runtime error: " + e.getMessage(), 1);
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private ExecutionResult runJava(String wrappedCode) {
        File tempFolder = null;
        try {
            ensureTempDir();
            tempFolder = new File(tempDir, "java_" + UUID.randomUUID());
            tempFolder.mkdirs();
            
            File sourceFile = new File(tempFolder, "Solution.java");
            Files.writeString(sourceFile.toPath(), wrappedCode);

            // Compile
            ProcessBuilder compilePb = new ProcessBuilder("javac", sourceFile.getAbsolutePath());
            ExecutionResult compileResult = runProcess(compilePb);
            if (compileResult.exitCode() != 0) {
                return new ExecutionResult(false, "", compileResult.errorOutput(), compileResult.exitCode());
            }

            // Run
            ProcessBuilder runPb = new ProcessBuilder("java", "-cp", tempFolder.getAbsolutePath(), "Solution");
            return runProcess(runPb);
        } catch (Exception e) {
            log.warn("Java execution issue: {}", e.getMessage());
            return new ExecutionResult(false, "", "Java runtime error: " + e.getMessage(), 1);
        } finally {
            if (tempFolder != null && tempFolder.exists()) {
                for (File f : tempFolder.listFiles()) f.delete();
                tempFolder.delete();
            }
        }
    }

    private ExecutionResult runProcess(ProcessBuilder pb) throws IOException, InterruptedException {
        pb.redirectErrorStream(false);
        Process process = pb.start();

        boolean finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS);
        if (!finished) {
            process.destroyForcibly();
            return new ExecutionResult(false, "Execution timed out", null);
        }

        String output = readStream(process.getInputStream());
        String errorOutput = readStream(process.getErrorStream());

        return new ExecutionResult(process.exitValue() == 0, output, errorOutput);
    }

    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
                if (sb.length() > 10240) { // 10KB limit
                    sb.append("... [OUTPUT TRUNCATED]");
                    break;
                }
            }
        }
        return sb.toString();
    }

    private void ensureTempDir() {
        File dir = new File(tempDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public record ExecutionResult(boolean success, String output, String errorOutput, int exitCode) {
        public ExecutionResult(boolean success, String output, String errorOutput) {
            this(success, output, errorOutput, success ? 0 : 1);
        }
    }
}
