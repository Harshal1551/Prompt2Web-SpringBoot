package com.prompt2web.service;

import com.prompt2web.dto.PreviewResponse;
import com.prompt2web.entity.Project;
import com.prompt2web.entity.ProjectFile;
import com.prompt2web.exception.ResourceNotFoundException;
import com.prompt2web.repository.ProjectFileRepository;
import com.prompt2web.repository.ProjectRepository;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PreviewService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;

    private final Map<String, Process> runningProcesses =
            new ConcurrentHashMap<>();

    private final Map<String, Path> previewDirectories =
            new ConcurrentHashMap<>();

    private final Map<String, Integer> previewPorts =
            new ConcurrentHashMap<>();

    private final Map<String, String> previewTokens =
            new ConcurrentHashMap<>();


    // ============================================================
    // START PREVIEW
    // ============================================================

    public PreviewResponse startPreview(
            String projectId,
            String userId,
            String baseUrl
    ) {

        Project project = projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        List<ProjectFile> files =
                projectFileRepository.findByProjectId(projectId);

        if (files.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Project has no files to preview"
            );
        }

        try {

            // Stop previous preview
            stopPreview(projectId);

            // Create temp directory
            Path rootDirectory = Files.createTempDirectory(
                    "prompt2web-" + project.getId() + "-"
            );

            previewDirectories.put(
                    projectId,
                    rootDirectory
            );

            // Create secure random preview token
            String token = UUID.randomUUID().toString();

            previewTokens.put(
                    projectId,
                    token
            );

            // Write project files
            for (ProjectFile file : files) {

                writeProjectFile(
                        rootDirectory,
                        file
                );
            }

            // Public proxy path
            String previewBasePath =
                    "/api/projects/"
                            + projectId
                            + "/preview/public/"
                            + token
                            + "/";

            // Create Vite config
            createViteConfig(
                    rootDirectory,
                    previewBasePath
            );

            // Make sure index.html exists
            createIndexHtmlIfMissing(rootDirectory);

            // Find port
            int port = findAvailablePort();

            previewPorts.put(
                    projectId,
                    port
            );

            // Install dependencies
            System.out.println(
                    "[Preview] Running npm install..."
            );

            runNpmInstall(rootDirectory);

            // Start Vite
            System.out.println(
                    "[Preview] Starting Vite..."
            );

            Process viteProcess =
                    startVite(
                            rootDirectory,
                            port
                    );

            runningProcesses.put(
                    projectId,
                    viteProcess
            );

            // Give Vite time to initialize
            Thread.sleep(3000);

            if (!viteProcess.isAlive()) {

                runningProcesses.remove(projectId);

                throw new RuntimeException(
                        "Vite server stopped unexpectedly."
                );
            }

            String previewUrl =
                    baseUrl
                            + previewBasePath;

            System.out.println(
                    "[Preview] Preview ready: "
                            + previewUrl
            );

            return new PreviewResponse(
                    "Preview started successfully",
                    previewUrl
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to start project preview: "
                            + e.getMessage(),
                    e
            );
        }
    }


    // ============================================================
    // PROXY PREVIEW REQUEST
    // ============================================================

    public ResponseEntity<byte[]> proxyPreview(
            String projectId,
            String token,
            String path,
            HttpServletRequest request
    ) {

        String storedToken =
                previewTokens.get(projectId);

        Integer port =
                previewPorts.get(projectId);

        Process process =
                runningProcesses.get(projectId);

        if (storedToken == null
                || !storedToken.equals(token)
                || port == null
                || process == null
                || !process.isAlive()) {

            return ResponseEntity.notFound().build();
        }

        try {

            String targetPath = "/";

            if (path != null && !path.isBlank()) {

                targetPath =
                        path.startsWith("/")
                                ? path
                                : "/" + path;
            }

            String query =
                    request.getQueryString();

            String targetUrl =
                    "http://127.0.0.1:"
                            + port
                            + targetPath;

            if (query != null && !query.isBlank()) {
                targetUrl += "?" + query;
            }

            HttpClient client =
                    HttpClient.newHttpClient();

            HttpRequest httpRequest =
                    HttpRequest.newBuilder()
                            .uri(URI.create(targetUrl))
                            .GET()
                            .build();

            HttpResponse<byte[]> response =
                    client.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofByteArray()
                    );

            HttpHeaders headers =
                    new HttpHeaders();

            response.headers()
                    .firstValue("Content-Type")
                    .ifPresent(
                            value ->
                                    headers.set(
                                            "Content-Type",
                                            value
                                    )
                    );

            response.headers()
                    .firstValue("Cache-Control")
                    .ifPresent(
                            value ->
                                    headers.set(
                                            "Cache-Control",
                                            value
                                    )
                    );

            return ResponseEntity
                    .status(
                            HttpStatusCode.valueOf(
                                    response.statusCode()
                            )
                    )
                    .headers(headers)
                    .body(response.body());

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }


    // ============================================================
    // WRITE PROJECT FILE
    // ============================================================

    private void writeProjectFile(
            Path rootDirectory,
            ProjectFile file
    ) throws IOException {

        Path filePath =
                rootDirectory
                        .resolve(file.getFilePath())
                        .normalize();

        if (!filePath.startsWith(rootDirectory)) {

            throw new SecurityException(
                    "Invalid project file path: "
                            + file.getFilePath()
            );
        }

        Path parent =
                filePath.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(
                filePath,

                file.getContent() == null
                        ? ""
                        : file.getContent(),

                StandardCharsets.UTF_8,

                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }


    // ============================================================
    // CREATE INDEX.HTML
    // ============================================================

    private void createIndexHtmlIfMissing(
            Path rootDirectory
    ) throws IOException {

        Path indexHtml =
                rootDirectory.resolve("index.html");

        if (Files.exists(indexHtml)) {
            return;
        }

        String indexContent = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">

                    <meta
                        name="viewport"
                        content="width=device-width, initial-scale=1.0"
                    >

                    <title>Prompt2Web Preview</title>
                </head>

                <body>

                    <div id="root"></div>

                    <script
                        type="module"
                        src="/src/main.jsx"
                    ></script>

                </body>
                </html>
                """;

        Files.writeString(
                indexHtml,
                indexContent,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE
        );
    }


    // ============================================================
    // CREATE VITE CONFIG
    // ============================================================

    private void createViteConfig(
            Path rootDirectory,
            String basePath
    ) throws IOException {

        Path viteConfig =
                rootDirectory.resolve("vite.config.js");

        String config =
                """
                import { defineConfig } from "vite";
                import react from "@vitejs/plugin-react";

                export default defineConfig({
                    base: "%s",

                    plugins: [
                        react()
                    ],

                    server: {
                        hmr: false
                    }
                });
                """
                        .formatted(basePath);

        Files.writeString(
                viteConfig,
                config,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        System.out.println(
                "[Preview] Created vite.config.js"
        );
    }


    // ============================================================
    // RUN NPM INSTALL
    // ============================================================

    private void runNpmInstall(
            Path workingDirectory
    ) throws Exception {

        String npmCommand =
                isWindows()
                        ? "npm.cmd"
                        : "npm";

        ProcessBuilder processBuilder =
                new ProcessBuilder(
                        npmCommand,
                        "install"
                );

        processBuilder.directory(
                workingDirectory.toFile()
        );

        processBuilder.redirectErrorStream(
                true
        );

        Process process =
                processBuilder.start();

        StringBuilder output =
                new StringBuilder();

        Thread outputThread =
                new Thread(() -> {

                    try (
                            BufferedReader reader =
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    process.getInputStream()
                                            )
                                    )
                    ) {

                        String line;

                        while (
                                (line = reader.readLine())
                                        != null
                        ) {

                            output
                                    .append(line)
                                    .append(
                                            System.lineSeparator()
                                    );

                            System.out.println(
                                    "[Preview npm] "
                                            + line
                            );
                        }

                    } catch (IOException e) {

                        System.err.println(
                                "[Preview npm] "
                                        + e.getMessage()
                        );
                    }

                });

        outputThread.setDaemon(true);
        outputThread.start();

        boolean finished =
                process.waitFor(
                        3,
                        TimeUnit.MINUTES
                );

        if (!finished) {

            process.destroyForcibly();

            throw new RuntimeException(
                    "npm install timed out"
            );
        }

        if (process.exitValue() != 0) {

            throw new RuntimeException(
                    "npm install failed:\n"
                            + output
            );
        }

        System.out.println(
                "[Preview] npm install completed"
        );
    }


    // ============================================================
    // START VITE
    // ============================================================

    private Process startVite(
            Path workingDirectory,
            int port
    ) throws IOException {

        String npmCommand =
                isWindows()
                        ? "npm.cmd"
                        : "npm";

        ProcessBuilder processBuilder =
                new ProcessBuilder(
                        npmCommand,
                        "run",
                        "dev",
                        "--",
                        "--host",
                        "127.0.0.1",
                        "--port",
                        String.valueOf(port)
                );

        processBuilder.directory(
                workingDirectory.toFile()
        );

        processBuilder.redirectErrorStream(
                true
        );

        Process process =
                processBuilder.start();

        Thread outputThread =
                new Thread(() -> {

                    try (
                            BufferedReader reader =
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    process.getInputStream()
                                            )
                                    )
                    ) {

                        String line;

                        while (
                                (line = reader.readLine())
                                        != null
                        ) {

                            System.out.println(
                                    "[Preview Vite] "
                                            + line
                            );
                        }

                    } catch (IOException e) {

                        System.err.println(
                                "[Preview Vite] "
                                        + e.getMessage()
                        );
                    }

                });

        outputThread.setDaemon(true);
        outputThread.start();

        return process;
    }


    // ============================================================
    // FIND AVAILABLE PORT
    // ============================================================

    private int findAvailablePort()
            throws IOException {

        try (
                ServerSocket socket =
                        new ServerSocket(0)
        ) {

            return socket.getLocalPort();
        }
    }


    // ============================================================
    // STOP PREVIEW
    // ============================================================

    public void stopPreview(
            String projectId
    ) {

        Process process =
                runningProcesses.remove(projectId);

        if (process != null && process.isAlive()) {

            try {

                if (isWindows()) {

                    new ProcessBuilder(
                            "taskkill",
                            "/F",
                            "/T",
                            "/PID",
                            String.valueOf(
                                    process.pid()
                            )
                    )
                            .start()
                            .waitFor(
                                    10,
                                    TimeUnit.SECONDS
                            );

                } else {

                    process.destroy();

                    if (!process.waitFor(
                            5,
                            TimeUnit.SECONDS
                    )) {

                        process.destroyForcibly();
                    }
                }

            } catch (Exception e) {

                System.err.println(
                        "[Preview] Failed to stop process: "
                                + e.getMessage()
                );

                process.destroyForcibly();
            }
        }

        previewPorts.remove(projectId);
        previewTokens.remove(projectId);

        Path directory =
                previewDirectories.remove(
                        projectId
                );

        if (directory != null) {

            deleteDirectory(directory);
        }
    }


    // ============================================================
    // DELETE DIRECTORY
    // ============================================================

    private void deleteDirectory(
            Path directory
    ) {

        try {

            if (!Files.exists(directory)) {
                return;
            }

            Files.walk(directory)
                    .sorted(
                            (a, b) ->
                                    b.compareTo(a)
                    )
                    .forEach(path -> {

                        try {

                            Files.deleteIfExists(
                                    path
                            );

                        } catch (IOException e) {

                            System.err.println(
                                    "[Preview] Failed to delete: "
                                            + path
                            );
                        }
                    });

        } catch (IOException e) {

            System.err.println(
                    "[Preview] Failed to clean preview directory: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // WINDOWS CHECK
    // ============================================================

    private boolean isWindows() {

        return System
                .getProperty("os.name")
                .toLowerCase()
                .contains("win");
    }


    // ============================================================
    // CLEANUP
    // ============================================================

    @PreDestroy
    public void shutdown() {

        runningProcesses
                .keySet()
                .forEach(
                        this::stopPreview
                );
    }
}