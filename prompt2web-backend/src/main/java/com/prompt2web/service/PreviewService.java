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
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.HexFormat;
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

    /*
     * Running Vite processes.
     *
     * projectId -> Vite process
     */
    private final Map<String, Process> runningProcesses =
            new ConcurrentHashMap<>();

    /*
     * Persistent preview directories.
     *
     * IMPORTANT:
     * We no longer use Files.createTempDirectory()
     * for every Preview click.
     *
     * The directory is reused so node_modules can also
     * be reused.
     */
    private final Map<String, Path> previewDirectories =
            new ConcurrentHashMap<>();

    /*
     * Currently running Vite port.
     */
    private final Map<String, Integer> previewPorts =
            new ConcurrentHashMap<>();

    /*
     * Public preview security token.
     */
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
        // 2. Get generated project files
        // --------------------------------------------------------

        List<ProjectFile> files =
                projectFileRepository.findByProjectId(projectId);

        if (files.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Project has no files to preview"
            );
        }


        try {

            // ----------------------------------------------------
            // 3. Stop only the running Vite process
            //
            // IMPORTANT:
            // We DO NOT delete the project directory.
            // This allows node_modules to be reused.
            // ----------------------------------------------------

            stopRunningProcess(projectId);


            // ----------------------------------------------------
            // 4. Get or create persistent project directory
            // ----------------------------------------------------

            Path rootDirectory =
                    getOrCreatePreviewDirectory(projectId);

            previewDirectories.put(
                    projectId,
                    rootDirectory
            );


            // ----------------------------------------------------
            // 5. Write generated project files
            // ----------------------------------------------------

            for (ProjectFile file : files) {

                writeProjectFile(
                        rootDirectory,
                        file
                );
            }


            // ----------------------------------------------------
            // 6. Make sure package.json exists
            // ----------------------------------------------------

            createPackageJsonIfMissing(
                    rootDirectory
            );


            // ----------------------------------------------------
            // 7. Make sure index.html exists
            // ----------------------------------------------------

            createIndexHtmlIfMissing(
                    rootDirectory
            );


            // ----------------------------------------------------
            // 8. Create preview token
            // ----------------------------------------------------

            String token =
                    UUID.randomUUID().toString();

            previewTokens.put(
                    projectId,
                    token
            );


            // ----------------------------------------------------
            // 9. Public preview path
            // ----------------------------------------------------

            String previewBasePath =
                    "/api/projects/"
                            + projectId
                            + "/preview/public/"
                            + token
                            + "/";


            // ----------------------------------------------------
            // 10. Create Vite configuration
            // ----------------------------------------------------

            createViteConfig(
                    rootDirectory,
                    previewBasePath
            );


            // ----------------------------------------------------
            // 11. Find available port
            // ----------------------------------------------------

            int port =
                    findAvailablePort();

            previewPorts.put(
                    projectId,
                    port
            );


            // ----------------------------------------------------
            // 12. Install dependencies ONLY when required
            // ----------------------------------------------------

            installDependenciesIfRequired(
                    rootDirectory
            );


            // ----------------------------------------------------
            // 13. Start Vite
            // ----------------------------------------------------

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


            // ----------------------------------------------------
// 14. Wait until Vite is actually ready
// ----------------------------------------------------

            waitForViteReady(
                    port,
                    viteProcess
            );


// ----------------------------------------------------
// 15. Verify Vite is still running
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
            // 16. Build public preview URL
            // ----------------------------------------------------

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
    // GET OR CREATE PREVIEW DIRECTORY
    // ============================================================

    private Path getOrCreatePreviewDirectory(
            String projectId
    ) throws IOException {

        Path existingDirectory =
                previewDirectories.get(
                        projectId
                );

        if (existingDirectory != null
                && Files.exists(existingDirectory)) {

            System.out.println(
                    "[Preview] Reusing existing directory: "
                            + existingDirectory
            );

            return existingDirectory;
        }


        /*
         * Persistent directory inside the application's
         * working directory.
         *
         * Example:
         *
         * /app/prompt2web-previews/<projectId>
         *
         * This survives Preview button clicks and allows
         * node_modules to remain available.
         */

        Path baseDirectory =
                Path.of(
                        System.getProperty(
                                "user.dir"
                        ),
                        "prompt2web-previews"
                );


        Files.createDirectories(
                baseDirectory
        );


        Path projectDirectory =
                baseDirectory.resolve(
                        projectId
                );


        Files.createDirectories(
                projectDirectory
        );


        System.out.println(
                "[Preview] Created preview directory: "
                        + projectDirectory
        );


        return projectDirectory;
    }


    // ============================================================
    // CREATE PACKAGE.JSON
    // ============================================================

    private void createPackageJsonIfMissing(
            Path rootDirectory
    ) throws IOException {

        Path packageJson =
                rootDirectory.resolve(
                        "package.json"
                );


        /*
         * If AI generated package.json,
         * don't overwrite it.
         */

        if (Files.exists(packageJson)) {

            System.out.println(
                    "[Preview] package.json already exists"
            );

            return;
        }


        String packageContent = """
                {
                  "name": "prompt2web-preview",
                  "private": true,
                  "version": "1.0.0",
                  "type": "module",
                  "scripts": {
                    "dev": "vite"
                  },
                  "dependencies": {
                    "react": "^18.3.1",
                    "react-dom": "^18.3.1",
                    "react-router-dom": "^6.28.0",
                    "lucide-react": "^0.468.0"
                  },
                  "devDependencies": {
                    "@vitejs/plugin-react": "^4.3.4",
                    "vite": "^5.4.11"
                  }
                }
                """;


        Files.writeString(
                packageJson,
                packageContent,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );


        System.out.println(
                "[Preview] Created package.json"
        );
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


        // Security check

        if (!filePath.startsWith(
                rootDirectory
        )) {

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
    // CREATE INDEX.HTML
    // ============================================================

    private void createIndexHtmlIfMissing(
            Path rootDirectory
    ) throws IOException {

        Path indexHtml =
                rootDirectory.resolve(
                        "index.html"
                );


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


        System.out.println(
                "[Preview] Created index.html"
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
                rootDirectory.resolve(
                        "vite.config.js"
                );


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
                        host: "127.0.0.1",
                        hmr: false
                    }

                });
                """
                        .formatted(
                                basePath
                        );


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
    // INSTALL DEPENDENCIES IF REQUIRED
    // ============================================================

    private void installDependenciesIfRequired(
            Path workingDirectory
    ) throws Exception {

        Path packageJson =
                workingDirectory.resolve(
                        "package.json"
                );

        Path nodeModules =
                workingDirectory.resolve(
                        "node_modules"
                );

        Path installSignature =
                workingDirectory.resolve(
                        ".prompt2web-install-signature"
                );


        if (!Files.exists(packageJson)) {

            throw new RuntimeException(
                    "package.json not found"
            );
        }


        String packageContent =
                Files.readString(
                        packageJson,
                        StandardCharsets.UTF_8
                );


        String currentSignature =
                createHash(
                        packageContent
                );


        /*
         * CASE 1:
         *
         * node_modules exists
         * AND
         * package.json has not changed.
         *
         * Therefore npm install is NOT required.
         */

        if (Files.exists(nodeModules)
                && Files.exists(installSignature)) {

            String savedSignature =
                    Files.readString(
                            installSignature,
                            StandardCharsets.UTF_8
                    );


            if (savedSignature.equals(
                    currentSignature
            )) {

                System.out.println(
                        "[Preview] Dependencies already installed."
                );

                System.out.println(
                        "[Preview] Skipping npm install."
                );

                return;
            }
        }


        /*
         * CASE 2:
         *
         * First Preview
         *
         * OR
         *
         * package.json changed.
         *
         * Therefore npm install is required.
         */

        System.out.println(
                "[Preview] Installing dependencies..."
        );


        runNpmInstall(
                workingDirectory
        );


        /*
         * Save package.json signature.
         *
         * Next Preview can compare it and skip npm install.
         */

        Files.writeString(
                installSignature,
                currentSignature,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );


        System.out.println(
                "[Preview] Dependency installation completed."
        );
    }


    // ============================================================
    // CREATE HASH
    // ============================================================

    private String createHash(
            String content
    ) throws Exception {

        MessageDigest digest =
                MessageDigest.getInstance(
                        "SHA-256"
                );


        byte[] hash =
                digest.digest(
                        content.getBytes(
                                StandardCharsets.UTF_8
                        )
                );


        return HexFormat.of().formatHex(
                hash
        );
    }


    // ============================================================
    // NPM INSTALL
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
                        "install",
                        "--no-audit",
                        "--no-fund",
                        "--prefer-offline"
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
                                (line =
                                        reader.readLine())
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


        outputThread.setDaemon(
                true
        );

        outputThread.start();


        boolean finished =
                process.waitFor(
                        5,
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
                                (line =
                                        reader.readLine())
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


        outputThread.setDaemon(
                true
        );

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
// WAIT FOR VITE TO BECOME READY
// ============================================================

    private void waitForViteReady(
            int port,
            Process viteProcess
    ) throws Exception {

        long timeout =
                System.currentTimeMillis() + 30_000;

        System.out.println(
                "[Preview] Waiting for Vite to become ready..."
        );

        while (System.currentTimeMillis() < timeout) {

            // --------------------------------------------------------
            // 1. Check if Vite process has crashed
            // --------------------------------------------------------

            if (!viteProcess.isAlive()) {

                throw new RuntimeException(
                        "Vite process stopped while starting."
                );
            }


            // --------------------------------------------------------
            // 2. Check whether Vite is accepting TCP connections
            // --------------------------------------------------------

            try (
                    java.net.Socket socket =
                            new java.net.Socket()
            ) {

                socket.connect(
                        new java.net.InetSocketAddress(
                                "127.0.0.1",
                                port
                        ),
                        1000
                );

                System.out.println(
                        "[Preview] Vite is ready on port "
                                + port
                );

                return;

            } catch (IOException ignored) {

                // Vite is still starting.
                // Continue checking.
            }


            // --------------------------------------------------------
            // 3. Wait before checking again
            // --------------------------------------------------------

            Thread.sleep(500);
        }


        // ------------------------------------------------------------
        // 4. Vite did not become available
        // ------------------------------------------------------------

        throw new RuntimeException(
                "Vite server did not become ready within 30 seconds."
        );
    }

    // ============================================================
    // PROXY PREVIEW
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

            return ResponseEntity
                    .notFound()
                    .build();
        }

        try {

            // --------------------------------------------------------
            // Vite base path
            // --------------------------------------------------------

            String previewBasePath =
                    "/api/projects/"
                            + projectId
                            + "/preview/public/"
                            + token
                            + "/";


            // --------------------------------------------------------
            // Requested resource
            // --------------------------------------------------------

            String resourcePath = "";

            if (path != null && !path.isBlank()) {

                resourcePath =
                        path.startsWith("/")
                                ? path.substring(1)
                                : path;
            }


            // --------------------------------------------------------
            // Build target URL
            //
            // Example:
            //
            // http://127.0.0.1:35221
            // /api/projects/{projectId}/preview/public/{token}/
            // assets/index.js
            // --------------------------------------------------------

            String targetUrl =
                    "http://127.0.0.1:"
                            + port
                            + previewBasePath
                            + resourcePath;


            // --------------------------------------------------------
            // Preserve query parameters
            // --------------------------------------------------------

            String query =
                    request.getQueryString();

            if (query != null
                    && !query.isBlank()) {

                targetUrl +=
                        "?" + query;
            }


            System.out.println(
                    "[Preview Proxy] "
                            + request.getMethod()
                            + " "
                            + request.getRequestURI()
                            + " -> "
                            + targetUrl
            );


            // --------------------------------------------------------
            // Send request to Vite
            // --------------------------------------------------------

            HttpClient client =
                    HttpClient.newBuilder()
                            .connectTimeout(
                                    java.time.Duration.ofSeconds(5)
                            )
                            .build();


            HttpRequest httpRequest =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(targetUrl)
                            )
                            .timeout(
                                    java.time.Duration.ofSeconds(10)
                            )
                            .GET()
                            .build();


            HttpResponse<byte[]> response =
                    client.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofByteArray()
                    );


            // --------------------------------------------------------
            // Copy important response headers
            // --------------------------------------------------------

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


            response.headers()
                    .firstValue("Content-Length")
                    .ifPresent(
                            value ->
                                    headers.set(
                                            "Content-Length",
                                            value
                                    )
                    );


            // --------------------------------------------------------
            // Return Vite response
            // --------------------------------------------------------

            return ResponseEntity
                    .status(
                            HttpStatusCode.valueOf(
                                    response.statusCode()
                            )
                    )
                    .headers(headers)
                    .body(
                            response.body()
                    );


        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }


    // ============================================================
    // STOP PREVIEW
    // ============================================================

    public void stopPreview(
            String projectId
    ) {

        stopRunningProcess(
                projectId
        );


        /*
         * IMPORTANT:
         *
         * We DO NOT remove:
         *
         * previewDirectories
         *
         * We DO NOT delete the directory.
         *
         * node_modules remains available for the next Preview.
         */


        previewPorts.remove(
                projectId
        );


        previewTokens.remove(
                projectId
        );
    }


    // ============================================================
    // STOP ONLY VITE PROCESS
    // ============================================================

    private void stopRunningProcess(
            String projectId
    ) {

        Process process =
                runningProcesses.remove(
                        projectId
                );


        if (process == null
                || !process.isAlive()) {

            return;
        }


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


    // ============================================================
    // WINDOWS CHECK
    // ============================================================

    private boolean isWindows() {

        return System
                .getProperty(
                        "os.name"
                )
                .toLowerCase()
                .contains("win");
    }


    // ============================================================
    // APPLICATION SHUTDOWN
    // ============================================================

    @PreDestroy
    public void shutdown() {

        runningProcesses
                .keySet()
                .forEach(
                        this::stopRunningProcess
                );
    }
}