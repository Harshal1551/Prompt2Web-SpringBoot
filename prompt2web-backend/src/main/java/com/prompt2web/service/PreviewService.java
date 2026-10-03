package com.prompt2web.service;

import com.prompt2web.dto.PreviewResponse;
import com.prompt2web.entity.Project;
import com.prompt2web.entity.ProjectFile;
import com.prompt2web.exception.ResourceNotFoundException;
import com.prompt2web.repository.ProjectFileRepository;
import com.prompt2web.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PreviewService {

    private final ProjectRepository projectRepository;

    private final ProjectFileRepository projectFileRepository;

    // ============================================================
    // PREVIEW DIRECTORIES
    // ============================================================

    private final Map<String, Path> previewDirectories =
            new ConcurrentHashMap<>();

    // ============================================================
    // PREVIEW TOKENS
    // ============================================================

    private final Map<String, String> previewTokens =
            new ConcurrentHashMap<>();

    // ============================================================
    // START PREVIEW
    // ============================================================

    public PreviewResponse startPreview(
            String projectId,
            String userId
    ) {

        // Verify project ownership
        projectRepository
                .findByIdAndUserId(
                        projectId,
                        userId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        try {

            // Build production preview
            buildPreview(
                    projectId,
                    userId
            );

            // Generate public preview token
            String token =
                    UUID.randomUUID().toString();

            previewTokens.put(
                    projectId,
                    token
            );

            // Build preview URL
            String previewUrl =
                    "/api/projects/"
                            + projectId
                            + "/preview/public/"
                            + token
                            + "/";

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
                    "Failed to build project preview: "
                            + e.getMessage(),
                    e
            );
        }
    }

    // ============================================================
    // BUILD PREVIEW
    // ============================================================

    public void buildPreview(
            String projectId,
            String userId
    ) throws Exception {

        // --------------------------------------------------------
        // Verify ownership
        // --------------------------------------------------------

        projectRepository
                .findByIdAndUserId(
                        projectId,
                        userId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        // --------------------------------------------------------
        // Get project files from database
        // --------------------------------------------------------

        List<ProjectFile> files =
                projectFileRepository
                        .findByProjectId(projectId);

        if (files.isEmpty()) {

            throw new ResourceNotFoundException(
                    "Project has no files to preview"
            );
        }

        // --------------------------------------------------------
        // Get preview directory
        // --------------------------------------------------------

        Path rootDirectory =
                getOrCreatePreviewDirectory(
                        projectId
                );

        previewDirectories.put(
                projectId,
                rootDirectory
        );

        // --------------------------------------------------------
        // Write generated files
        // --------------------------------------------------------

        for (ProjectFile file : files) {

            writeProjectFile(
                    rootDirectory,
                    file
            );
        }

        // --------------------------------------------------------
        // Verify package.json
        // --------------------------------------------------------

        Path packageJson =
                rootDirectory.resolve(
                        "package.json"
                );

        if (!Files.exists(packageJson)) {

            throw new RuntimeException(
                    "Generated project does not contain package.json"
            );
        }

        // --------------------------------------------------------
        // Install dependencies
        // --------------------------------------------------------

        installDependenciesIfRequired(
                rootDirectory
        );

        // --------------------------------------------------------
        // Build React application
        // --------------------------------------------------------

        runNpmBuild(
                rootDirectory
        );

        // --------------------------------------------------------
        // Verify dist
        // --------------------------------------------------------

        Path distDirectory =
                rootDirectory.resolve("dist");

        Path distIndex =
                distDirectory.resolve(
                        "index.html"
                );

        if (!Files.exists(distDirectory)
                || !Files.exists(distIndex)) {

            throw new RuntimeException(
                    "React build completed but dist/index.html was not created."
            );
        }

        System.out.println(
                "[Preview] Production build completed."
        );

        System.out.println(
                "[Preview] Dist directory: "
                        + distDirectory.toAbsolutePath()
        );
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

            return existingDirectory;
        }

        Path baseDirectory =
                Paths.get(
                        System.getProperty("user.dir"),
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
                "[Preview] Preview directory: "
                        + projectDirectory.toAbsolutePath()
        );

        return projectDirectory;
    }

    // ============================================================
    // WRITE PROJECT FILE
    // ============================================================

    private void writeProjectFile(
            Path rootDirectory,
            ProjectFile file
    ) throws IOException {

        String relativePath =
                file.getFilePath()
                        .replace("\\", "/");

        // --------------------------------------------------------
        // Prevent absolute paths
        // --------------------------------------------------------

        if (relativePath.startsWith("/")
                || relativePath.contains(":")) {

            throw new SecurityException(
                    "Invalid project file path: "
                            + relativePath
            );
        }

        // --------------------------------------------------------
        // Resolve file path
        // --------------------------------------------------------

        Path filePath =
                rootDirectory
                        .resolve(relativePath)
                        .normalize();

        // --------------------------------------------------------
        // Security check
        // --------------------------------------------------------

        if (!filePath.startsWith(
                rootDirectory.normalize()
        )) {

            throw new SecurityException(
                    "Invalid project file path: "
                            + relativePath
            );
        }

        // --------------------------------------------------------
        // Create parent directories
        // --------------------------------------------------------

        Path parent =
                filePath.getParent();

        if (parent != null) {

            Files.createDirectories(
                    parent
            );
        }

        // --------------------------------------------------------
        // Write file
        // --------------------------------------------------------

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

        // --------------------------------------------------------
        // Reuse existing node_modules
        // --------------------------------------------------------

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

                return;
            }
        }

        // --------------------------------------------------------
        // Install dependencies
        // --------------------------------------------------------

        System.out.println(
                "[Preview] Installing dependencies..."
        );

        runNpmInstall(
                workingDirectory
        );

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

        return HexFormat
                .of()
                .formatHex(hash);
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
                        "--no-fund"
                );

        processBuilder.directory(
                workingDirectory.toFile()
        );

        processBuilder.redirectErrorStream(
                true
        );

        Process process =
                processBuilder.start();

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        process.getInputStream(),
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {

            String line;

            while (
                    (line = reader.readLine())
                            != null
            ) {

                System.out.println(
                        "[Preview npm] "
                                + line
                );
            }
        }

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
                    "npm install failed with exit code "
                            + process.exitValue()
            );
        }
    }

    // ============================================================
    // NPM BUILD
    // ============================================================

    private void runNpmBuild(
            Path workingDirectory
    ) throws Exception {

        String npmCommand =
                isWindows()
                        ? "npm.cmd"
                        : "npm";

        System.out.println(
                "[Preview] Building React application..."
        );

        ProcessBuilder processBuilder =
                new ProcessBuilder(
                        npmCommand,
                        "run",
                        "build"
                );

        processBuilder.directory(
                workingDirectory.toFile()
        );

        processBuilder.redirectErrorStream(
                true
        );

        Process process =
                processBuilder.start();

        try (
                BufferedReader reader =
                        new BufferedReader(
                                new InputStreamReader(
                                        process.getInputStream(),
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {

            String line;

            while (
                    (line = reader.readLine())
                            != null
            ) {

                System.out.println(
                        "[Preview build] "
                                + line
                );
            }
        }

        boolean finished =
                process.waitFor(
                        5,
                        TimeUnit.MINUTES
                );

        if (!finished) {

            process.destroyForcibly();

            throw new RuntimeException(
                    "npm run build timed out"
            );
        }

        if (process.exitValue() != 0) {

            throw new RuntimeException(
                    "npm run build failed with exit code "
                            + process.exitValue()
            );
        }

        System.out.println(
                "[Preview] React production build completed."
        );
    }

    // ============================================================
    // SERVE PREVIEW FILE
    // ============================================================

    public ResponseEntity<Resource> servePreviewFile(
            String projectId,
            String token,
            String requestedPath
    ) {

        // --------------------------------------------------------
        // Validate token
        // --------------------------------------------------------

        String storedToken =
                previewTokens.get(
                        projectId
                );

        if (storedToken == null
                || !storedToken.equals(token)) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .build();
        }

        try {

            // ----------------------------------------------------
            // Get preview directory
            // ----------------------------------------------------

            Path rootDirectory =
                    previewDirectories.get(
                            projectId
                    );

            if (rootDirectory == null
                    || !Files.exists(rootDirectory)) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
            }

            // ----------------------------------------------------
            // Get dist directory
            // ----------------------------------------------------

            Path distDirectory =
                    rootDirectory
                            .resolve("dist")
                            .normalize();

            if (!Files.exists(distDirectory)
                    || !Files.isDirectory(distDirectory)) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
            }

            // ----------------------------------------------------
            // Clean requested path
            // ----------------------------------------------------

            String cleanPath =
                    requestedPath == null
                            ? ""
                            : requestedPath;

            while (cleanPath.startsWith("/")) {

                cleanPath =
                        cleanPath.substring(1);
            }

            // Decode accidental leading slash if necessary
            cleanPath =
                    cleanPath.replace(
                            "\\",
                            "/"
                    );

            // ----------------------------------------------------
            // Resolve requested file
            // ----------------------------------------------------

            Path requestedFile =
                    distDirectory
                            .resolve(cleanPath)
                            .normalize();

            // ----------------------------------------------------
            // Security check
            // ----------------------------------------------------

            if (!requestedFile.startsWith(
                    distDirectory.normalize()
            )) {

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .build();
            }

            // ====================================================
            // ROOT -> index.html
            // ====================================================

            if (cleanPath.isEmpty()
                    || cleanPath.equals("/")) {

                requestedFile =
                        distDirectory.resolve(
                                "index.html"
                        );
            }

            // ====================================================
            // FILE DOES NOT EXIST
            // ====================================================

            if (!Files.exists(requestedFile)
                    || Files.isDirectory(requestedFile)) {

                /*
                 * IMPORTANT:
                 *
                 * Only use SPA fallback for routes.
                 *
                 * Do NOT return index.html when the browser
                 * requests a missing .js/.css/.png/etc.
                 */

                if (hasFileExtension(cleanPath)) {

                    System.out.println(
                            "[Preview] Asset not found: "
                                    + requestedFile.toAbsolutePath()
                    );

                    return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .build();
                }

                // SPA route fallback
                requestedFile =
                        distDirectory.resolve(
                                "index.html"
                        );
            }

            // ----------------------------------------------------
            // Final existence check
            // ----------------------------------------------------

            if (!Files.exists(requestedFile)
                    || Files.isDirectory(requestedFile)) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
            }

            // ----------------------------------------------------
            // Create Resource
            // ----------------------------------------------------

            Resource resource =
                    new UrlResource(
                            requestedFile.toUri()
                    );

            if (!resource.exists()
                    || !resource.isReadable()) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
            }

            // ----------------------------------------------------
            // Determine MIME type
            // ----------------------------------------------------

            MediaType mediaType =
                    determineMediaType(
                            requestedFile
                    );

            System.out.println(
                    "[Preview] Serving: "
                            + requestedFile.getFileName()
                            + " | MIME: "
                            + mediaType
            );

            // ----------------------------------------------------
            // Return resource
            // ----------------------------------------------------

            return ResponseEntity
                    .ok()
                    .header(
                            HttpHeaders.CACHE_CONTROL,
                            "no-cache, no-store, must-revalidate"
                    )
                    .header(
                            HttpHeaders.PRAGMA,
                            "no-cache"
                    )
                    .header(
                            HttpHeaders.CONTENT_TYPE,
                            mediaType.toString()
                    )
                    .contentType(
                            mediaType
                    )
                    .body(resource);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .status(
                            HttpStatus.INTERNAL_SERVER_ERROR
                    )
                    .build();
        }
    }

    // ============================================================
    // DETERMINE MIME TYPE
    // ============================================================

    private MediaType determineMediaType(
            Path file
    ) {

        String fileName =
                file.getFileName()
                        .toString()
                        .toLowerCase(
                                Locale.ROOT
                        );

        // --------------------------------------------------------
        // HTML
        // --------------------------------------------------------

        if (fileName.endsWith(".html")
                || fileName.endsWith(".htm")) {

            return MediaType.TEXT_HTML;
        }

        // --------------------------------------------------------
        // CSS
        // --------------------------------------------------------

        if (fileName.endsWith(".css")) {

            return MediaType.valueOf(
                    "text/css"
            );
        }

        // --------------------------------------------------------
        // JavaScript
        // --------------------------------------------------------

        if (fileName.endsWith(".js")
                || fileName.endsWith(".mjs")) {

            return MediaType.valueOf(
                    "application/javascript"
            );
        }

        // --------------------------------------------------------
        // JSON
        // --------------------------------------------------------

        if (fileName.endsWith(".json")) {

            return MediaType.APPLICATION_JSON;
        }

        // --------------------------------------------------------
        // SVG
        // --------------------------------------------------------

        if (fileName.endsWith(".svg")) {

            return MediaType.valueOf(
                    "image/svg+xml"
            );
        }

        // --------------------------------------------------------
        // PNG
        // --------------------------------------------------------

        if (fileName.endsWith(".png")) {

            return MediaType.valueOf(
                    "image/png"
            );
        }

        // --------------------------------------------------------
        // JPEG
        // --------------------------------------------------------

        if (fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")) {

            return MediaType.valueOf(
                    "image/jpeg"
            );
        }

        // --------------------------------------------------------
        // GIF
        // --------------------------------------------------------

        if (fileName.endsWith(".gif")) {

            return MediaType.valueOf(
                    "image/gif"
            );
        }

        // --------------------------------------------------------
        // WEBP
        // --------------------------------------------------------

        if (fileName.endsWith(".webp")) {

            return MediaType.valueOf(
                    "image/webp"
            );
        }

        // --------------------------------------------------------
        // ICO
        // --------------------------------------------------------

        if (fileName.endsWith(".ico")) {

            return MediaType.valueOf(
                    "image/x-icon"
            );
        }

        // --------------------------------------------------------
        // WOFF
        // --------------------------------------------------------

        if (fileName.endsWith(".woff")) {

            return MediaType.valueOf(
                    "font/woff"
            );
        }

        // --------------------------------------------------------
        // WOFF2
        // --------------------------------------------------------

        if (fileName.endsWith(".woff2")) {

            return MediaType.valueOf(
                    "font/woff2"
            );
        }

        // --------------------------------------------------------
        // TTF
        // --------------------------------------------------------

        if (fileName.endsWith(".ttf")) {

            return MediaType.valueOf(
                    "font/ttf"
            );
        }

        // --------------------------------------------------------
        // Default
        // --------------------------------------------------------

        try {

            String detected =
                    Files.probeContentType(file);

            if (detected != null
                    && !detected.isBlank()) {

                return MediaType.parseMediaType(
                        detected
                );
            }

        } catch (Exception ignored) {
            // Fall through to octet-stream
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }

    // ============================================================
    // CHECK WHETHER PATH HAS FILE EXTENSION
    // ============================================================

    private boolean hasFileExtension(
            String path
    ) {

        if (path == null
                || path.isBlank()) {

            return false;
        }

        String fileName =
                Paths.get(path)
                        .getFileName()
                        .toString();

        return fileName.contains(".")
                && !fileName.endsWith(".");
    }

    // ============================================================
    // STOP PREVIEW
    // ============================================================

    public void stopPreview(
            String projectId,
            String userId
    ) {

        projectRepository
                .findByIdAndUserId(
                        projectId,
                        userId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Project not found"
                        )
                );

        // Remove public token
        previewTokens.remove(
                projectId
        );

        System.out.println(
                "[Preview] Preview stopped for project: "
                        + projectId
        );

        /*
         * Directory intentionally remains.
         *
         * This allows node_modules and the generated
         * project files to be reused on the next preview.
         */
    }

    // ============================================================
    // WINDOWS CHECK
    // ============================================================

    private boolean isWindows() {

        return System
                .getProperty("os.name")
                .toLowerCase(
                        Locale.ROOT
                )
                .contains("win");
    }
}