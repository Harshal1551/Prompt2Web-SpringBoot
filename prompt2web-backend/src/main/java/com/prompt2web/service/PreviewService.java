package com.prompt2web.service;

import com.prompt2web.dto.PreviewResponse;
import com.prompt2web.entity.Project;
import com.prompt2web.entity.ProjectFile;
import com.prompt2web.exception.ResourceNotFoundException;
import com.prompt2web.repository.ProjectFileRepository;
import com.prompt2web.repository.ProjectRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PreviewService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;

    /*
     * Running Vite process for each project.
     */
    private final Map<String, Process> runningProcesses =
            new ConcurrentHashMap<>();

    /*
     * Preview URL for each running project.
     */
    private final Map<String, String> runningPreviewUrls =
            new ConcurrentHashMap<>();

    /*
     * Temporary directory for each preview.
     */
    private final Map<String, Path> previewDirectories =
            new ConcurrentHashMap<>();


    // ============================================================
    // START PREVIEW
    // ============================================================

    public PreviewResponse startPreview(
            String projectId,
            String userId
    ) {

        // --------------------------------------------------------
        // 1. Verify project ownership
        // --------------------------------------------------------

        Project project = projectRepository
                .findByIdAndUserId(projectId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project not found"
                        )
                );


        // --------------------------------------------------------
        // 2. If preview is already running, reuse it
        // --------------------------------------------------------

        Process existingProcess =
                runningProcesses.get(projectId);

        String existingUrl =
                runningPreviewUrls.get(projectId);


        if (
                existingProcess != null &&
                        existingProcess.isAlive() &&
                        existingUrl != null
        ) {

            System.out.println(
                    "[Preview] Reusing existing preview: "
                            + existingUrl
            );

            return new PreviewResponse(
                    "Preview already running",
                    existingUrl
            );
        }


        // --------------------------------------------------------
        // 3. Clean stale preview data
        // --------------------------------------------------------

        if (existingProcess != null) {
            runningProcesses.remove(projectId);
        }

        if (existingUrl != null) {
            runningPreviewUrls.remove(projectId);
        }


        // --------------------------------------------------------
        // 4. Get project files
        // --------------------------------------------------------

        List<ProjectFile> files =
                projectFileRepository.findByProjectId(
                        projectId
                );


        if (files.isEmpty()) {

            throw new ResourceNotFoundException(
                    "Project has no files to preview"
            );
        }


        Path rootDirectory = null;
        Process viteProcess = null;


        try {

            // ----------------------------------------------------
            // 5. Create temporary directory
            // ----------------------------------------------------

            rootDirectory =
                    Files.createTempDirectory(
                            "prompt2web-" +
                                    project.getId() +
                                    "-"
                    );


            previewDirectories.put(
                    projectId,
                    rootDirectory
            );


            // ----------------------------------------------------
            // 6. Write project files
            // ----------------------------------------------------

            for (ProjectFile file : files) {

                writeProjectFile(
                        rootDirectory,
                        file
                );
            }


            // ----------------------------------------------------
            // 7. Create Vite config if missing
            // ----------------------------------------------------

            createViteConfig(
                    rootDirectory
            );


            // ----------------------------------------------------
            // 8. Make sure index.html exists
            // ----------------------------------------------------

            Path indexHtml =
                    rootDirectory.resolve(
                            "index.html"
                    );


            if (!Files.exists(indexHtml)) {

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


            // ----------------------------------------------------
            // 9. Find available port
            // ----------------------------------------------------

            int port =
                    findAvailablePort();


            // ----------------------------------------------------
            // 10. Install dependencies
            // ----------------------------------------------------

            System.out.println(
                    "[Preview] Running npm install..."
            );


            runNpmInstall(
                    rootDirectory
            );


            // ----------------------------------------------------
            // 11. Start Vite
            // ----------------------------------------------------

            System.out.println(
                    "[Preview] Starting Vite..."
            );


            viteProcess =
                    startVite(
                            rootDirectory,
                            port
                    );


            runningProcesses.put(
                    projectId,
                    viteProcess
            );


            // ----------------------------------------------------
            // 12. Give Vite time to initialize
            // ----------------------------------------------------

            Thread.sleep(3000);


            // ----------------------------------------------------
            // 13. Verify Vite process
            // ----------------------------------------------------

            if (!viteProcess.isAlive()) {

                runningProcesses.remove(
                        projectId
                );

                throw new RuntimeException(
                        "Vite server stopped unexpectedly."
                );
            }


            // ----------------------------------------------------
            // 14. Build preview URL
            // ----------------------------------------------------

            /*
             * Vite listens on 0.0.0.0, while the browser uses
             * localhost to access the preview.
             */
            String previewUrl =
                    "http://localhost:" + port;


            runningPreviewUrls.put(
                    projectId,
                    previewUrl
            );


            // ----------------------------------------------------
            // 15. Log and return
            // ----------------------------------------------------

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


            // ----------------------------------------------------
            // Cleanup failed preview
            // ----------------------------------------------------

            if (
                    viteProcess != null &&
                            viteProcess.isAlive()
            ) {

                stopProcess(
                        viteProcess
                );
            }


            runningProcesses.remove(
                    projectId
            );

            runningPreviewUrls.remove(
                    projectId
            );


            Path directory =
                    previewDirectories.remove(
                            projectId
                    );


            if (directory != null) {

                deleteDirectory(
                        directory
                );
            }


            throw new RuntimeException(
                    "Failed to start project preview: "
                            + e.getMessage(),
                    e
            );
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
                        .resolve(
                                file.getFilePath()
                        )
                        .normalize();


        // Prevent path traversal
        if (!filePath.startsWith(rootDirectory)) {

            throw new SecurityException(
                    "Invalid project file path: "
                            + file.getFilePath()
            );
        }


        Path parent =
                filePath.getParent();


        if (parent != null) {

            Files.createDirectories(
                    parent
            );
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
    // CREATE VITE CONFIG
    // ============================================================

    private void createViteConfig(
            Path rootDirectory
    ) throws IOException {

        Path viteConfig =
                rootDirectory.resolve(
                        "vite.config.js"
                );


        /*
         * If the generated project already has a Vite config,
         * don't overwrite it.
         */
        if (Files.exists(viteConfig)) {
            return;
        }


        String config = """
                import { defineConfig } from "vite";
                import react from "@vitejs/plugin-react";

                export default defineConfig({
                    plugins: [
                        react()
                    ]
                });
                """;


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
                                    .append(System.lineSeparator());


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
                        "0.0.0.0",
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
                runningProcesses.remove(
                        projectId
                );


        runningPreviewUrls.remove(
                projectId
        );


        if (
                process != null &&
                        process.isAlive()
        ) {

            stopProcess(
                    process
            );
        }


        Path directory =
                previewDirectories.remove(
                        projectId
                );


        if (directory != null) {

            try {

                Thread.sleep(500);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }


            deleteDirectory(
                    directory
            );
        }
    }


    // ============================================================
    // STOP PROCESS
    // ============================================================

    private void stopProcess(
            Process process
    ) {

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


                if (
                        !process.waitFor(
                                5,
                                TimeUnit.SECONDS
                        )
                ) {

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
    // CLEANUP WHEN SPRING BOOT STOPS
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