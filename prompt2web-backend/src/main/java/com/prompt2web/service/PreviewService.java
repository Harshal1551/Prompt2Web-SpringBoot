package com.prompt2web.service;

import com.prompt2web.dto.PreviewResponse;
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
import java.io.File;
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
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class PreviewService {

    private final ProjectRepository projectRepository;

    private final ProjectFileRepository projectFileRepository;
    private final AiCodeRepairService aiCodeRepairService;

    // PREVIEW DIRECTORIES

    private final Map<String, Path> previewDirectories =
            new ConcurrentHashMap<>();

    // PREVIEW TOKENS

    private final Map<String, String> previewTokens =
            new ConcurrentHashMap<>();

    // Track the signature of the last successful production build.
    private final Map<String, String> successfulBuildSignatures =
            new ConcurrentHashMap<>();

    // START PREVIEW

    public PreviewResponse startPreview(
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

        try {

            buildPreview(
                    projectId,
                    userId
            );

            String token =
                    UUID.randomUUID().toString();

            previewTokens.put(
                    projectId,
                    token
            );

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


    private String createProjectFilesHash(List<ProjectFile> files)
            throws Exception {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        files.stream()
                .sorted(Comparator.comparing(ProjectFile::getFilePath))
                .forEach(file -> {
                    String path = file.getFilePath() == null
                            ? ""
                            : file.getFilePath();

                    String content = file.getContent() == null
                            ? ""
                            : file.getContent();

                    digest.update(path.getBytes(StandardCharsets.UTF_8));
                    digest.update((byte) 0);
                    digest.update(content.getBytes(StandardCharsets.UTF_8));
                    digest.update((byte) 0);
                });

        return HexFormat.of().formatHex(digest.digest());
    }

    // BUILD PREVIEW

    public void buildPreview(
            String projectId,
            String userId
    ) throws Exception {

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

        List<ProjectFile> files =
                projectFileRepository
                        .findByProjectId(projectId);

        if (files.isEmpty()) {

            throw new ResourceNotFoundException(
                    "Project has no files to preview"
            );
        }

        Path rootDirectory =
                getOrCreatePreviewDirectory(
                        projectId
                );

        previewDirectories.put(
                projectId,
                rootDirectory
        );

        // Calculate the signature of the current database files.
        String currentSignature = createProjectFilesHash(files);

// Check whether a valid previous build can be reused.
        Path distDirectory = rootDirectory.resolve("dist");
        Path distIndex = distDirectory.resolve("index.html");

        String previousSignature =
                successfulBuildSignatures.get(projectId);

        boolean canReuseBuild =
                currentSignature.equals(previousSignature)
                        && Files.isRegularFile(distIndex);

        if (canReuseBuild) {

            System.out.println(
                    "[Preview] Project files unchanged. Reusing previous build."
            );

        } else {

            System.out.println(
                    "[Preview] Project files changed or no cached build exists."
            );

            // Write current database content to the preview directory.
            for (ProjectFile file : files) {
                writeProjectFile(rootDirectory, file);
            }

            Path packageJson = rootDirectory.resolve("package.json");

            if (!Files.isRegularFile(packageJson)) {
                throw new RuntimeException(
                        "Generated project does not contain package.json"
                );
            }

            installDependenciesIfRequired(rootDirectory);

            runNpmBuildWithAiRepair(rootDirectory, projectId);

            if (!Files.isRegularFile(distIndex)) {
                throw new RuntimeException(
                        "React build completed but dist/index.html was not created."
                );
            }

            // Save the signature only after a successful build.
            // AI repair may have changed files in the database, so reload them.
            List<ProjectFile> updatedFiles =
                    projectFileRepository.findByProjectId(projectId);

            String successfulSignature =
                    createProjectFilesHash(updatedFiles);

            successfulBuildSignatures.put(
                    projectId,
                    successfulSignature
            );

            System.out.println(
                    "[Preview] Successful build signature saved."
            );
        }

        System.out.println(
                "[Preview] Preview build is ready: "
                        + distDirectory.toAbsolutePath()
        );
    }

    // GET OR CREATE PREVIEW DIRECTORY

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

    // WRITE PROJECT FILE

    private void writeProjectFile(
            Path rootDirectory,
            ProjectFile file
    ) throws IOException {

        String relativePath =
                file.getFilePath()
                        .replace("\\", "/");

        if (relativePath.startsWith("/")
                || relativePath.contains(":")) {

            throw new SecurityException(
                    "Invalid project file path: "
                            + relativePath
            );
        }

        Path filePath =
                rootDirectory
                        .resolve(relativePath)
                        .normalize();

        if (!filePath.startsWith(
                rootDirectory.normalize()
        )) {

            throw new SecurityException(
                    "Invalid project file path: "
                            + relativePath
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

    // INSTALL DEPENDENCIES IF REQUIRED

    private void installDependenciesIfRequired(
            Path workingDirectory
    ) throws Exception {

        Path packageJson =
                workingDirectory.resolve("package.json");

        Path packageLock =
                workingDirectory.resolve("package-lock.json");

        Path nodeModules =
                workingDirectory.resolve("node_modules");

        Path installSignature =
                workingDirectory.resolve(".prompt2web-install-signature");

        if (!Files.exists(packageJson)) {
            throw new RuntimeException("package.json not found");
        }

        String packageContent =
                Files.readString(
                        packageJson,
                        StandardCharsets.UTF_8
                );

        // Include the lock file in the signature when available.
        String lockContent = Files.exists(packageLock)
                ? Files.readString(packageLock, StandardCharsets.UTF_8)
                : "";

        String currentSignature =
                createHash(packageContent + "\n" + lockContent);

        if (Files.exists(nodeModules)
                && Files.exists(installSignature)) {

            String savedSignature =
                    Files.readString(
                            installSignature,
                            StandardCharsets.UTF_8
                    );

            if (savedSignature.equals(currentSignature)) {
                System.out.println(
                        "[Preview] Dependencies already installed."
                );
                return;
            }
        }

        System.out.println("[Preview] Installing dependencies...");

        runNpmInstall(workingDirectory);

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


    // CREATE HASH

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

    // NPM INSTALL

    private void runNpmInstall(
            Path workingDirectory
    ) throws Exception {

        String npmCommand = isWindows() ? "npm.cmd" : "npm";

        Path packageLock =
                workingDirectory.resolve("package-lock.json");

        ProcessBuilder processBuilder;

        if (Files.exists(packageLock)) {
            System.out.println(
                    "[Preview] Using npm ci with package-lock.json."
            );

            processBuilder = new ProcessBuilder(
                    npmCommand,
                    "ci",
                    "--no-audit",
                    "--no-fund"
            );
        } else {
            System.out.println(
                    "[Preview] package-lock.json not found; using npm install."
            );

            processBuilder = new ProcessBuilder(
                    npmCommand,
                    "install",
                    "--no-audit",
                    "--no-fund"
            );
        }


        processBuilder.directory(workingDirectory.toFile());

// Reuse downloaded npm packages across different projects.
        String npmCacheDirectory = System.getProperty("user.home")
                + File.separator + ".prompt2web-npm-cache";

        processBuilder.environment().put(
                "npm_config_cache",
                npmCacheDirectory
        );

        processBuilder.redirectErrorStream(true);


        Process process = processBuilder.start();

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(
                                     process.getInputStream(),
                                     StandardCharsets.UTF_8
                             )
                     )) {

            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println("[Preview npm] " + line);
            }
        }

        boolean finished =
                process.waitFor(5, TimeUnit.MINUTES);

        if (!finished) {
            process.destroyForcibly();

            throw new RuntimeException(
                    "npm dependency installation timed out"
            );
        }

        if (process.exitValue() != 0) {
            throw new RuntimeException(
                    "npm dependency installation failed with exit code "
                            + process.exitValue()
            );
        }
    }



    // NPM BUILD
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

        StringBuilder buildOutput =
                new StringBuilder();

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

                buildOutput
                        .append(line)
                        .append(System.lineSeparator());
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
                    "npm run build timed out\n"
                            + buildOutput
            );
        }

        if (process.exitValue() != 0) {

            throw new RuntimeException(
                    buildOutput.toString().trim()
            );
        }

        System.out.println(
                "[Preview] React production build completed."
        );
    }


    // BUILD WITH AI REPAIR
    private void runNpmBuildWithAiRepair(
            Path workingDirectory,
            String projectId
    ) throws Exception {

        final int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {

            System.out.println(
                    "[Preview] Build attempt "
                            + attempt
                            + "/"
                            + maxAttempts
            );

            try {

                runNpmBuild(
                        workingDirectory
                );

                System.out.println(
                        "[Preview] Build successful on attempt "
                                + attempt
                );

                return;

            } catch (Exception buildException) {

                String buildError =
                        buildException.getMessage();

                System.out.println(
                        "[Preview] Build failed on attempt "
                                + attempt
                );

                System.out.println(
                        "[Preview] Build error: "
                                + buildError
                );

                if (attempt == maxAttempts) {

                    throw new RuntimeException(
                            "React build failed after "
                                    + maxAttempts
                                    + " attempts: "
                                    + buildError,
                            buildException
                    );
                }

                System.out.println(
                        "[Preview AI] Sending build error to AI..."
                );

                List<ProjectFile> projectFiles =
                        projectFileRepository
                                .findByProjectId(projectId);

                AiCodeRepairService.RepairResult repairResult =
                        aiCodeRepairService.repairBuildError(
                                buildError,
                                projectFiles
                        );

                System.out.println(
                        "[Preview AI] AI selected file: "
                                + repairResult.filePath()
                );

                System.out.println(
                        "[Preview AI] Explanation: "
                                + repairResult.explanation()
                );

                ProjectFile projectFile =
                        projectFiles.stream()
                                .filter(file ->
                                        file.getFilePath()
                                                .equals(
                                                        repairResult.filePath()
                                                )
                                )
                                .findFirst()
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "AI selected a file that does not exist: "
                                                        + repairResult.filePath()
                                        )
                                );

                // Save corrected code to database
                projectFile.setContent(
                        repairResult.correctedCode()
                );

                projectFileRepository.save(
                        projectFile
                );

                // Write corrected code to preview directory
                writeProjectFile(
                        workingDirectory,
                        projectFile
                );

                System.out.println(
                        "[Preview AI] File repaired successfully: "
                                + repairResult.filePath()
                );

                System.out.println(
                        "[Preview AI] Retrying build..."
                );
            }
        }
    }



    // SERVE PREVIEW FILE

    public ResponseEntity<Resource> servePreviewFile(
            String projectId,
            String token,
            String requestedPath
    ) {

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

            String cleanPath =
                    requestedPath == null
                            ? ""
                            : requestedPath;

            while (cleanPath.startsWith("/")) {

                cleanPath =
                        cleanPath.substring(1);
            }

            cleanPath =
                    cleanPath.replace(
                            "\\",
                            "/"
                    );

            Path requestedFile =
                    distDirectory
                            .resolve(cleanPath)
                            .normalize();

            if (!requestedFile.startsWith(
                    distDirectory.normalize()
            )) {

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .build();
            }

            if (cleanPath.isEmpty()
                    || cleanPath.equals("/")) {

                requestedFile =
                        distDirectory.resolve(
                                "index.html"
                        );
            }

            if (!Files.exists(requestedFile)
                    || Files.isDirectory(requestedFile)) {

                if (hasFileExtension(cleanPath)) {

                    System.out.println(
                            "[Preview] Asset not found: "
                                    + requestedFile.toAbsolutePath()
                    );

                    return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .build();
                }

                requestedFile =
                        distDirectory.resolve(
                                "index.html"
                        );
            }

            if (!Files.exists(requestedFile)
                    || Files.isDirectory(requestedFile)) {

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .build();
            }

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

    // DETERMINE MIME TYPE

    private MediaType determineMediaType(
            Path file
    ) {

        String fileName =
                file.getFileName()
                        .toString()
                        .toLowerCase(
                                Locale.ROOT
                        );

        if (fileName.endsWith(".html")
                || fileName.endsWith(".htm")) {

            return MediaType.TEXT_HTML;
        }

        if (fileName.endsWith(".css")) {

            return MediaType.valueOf(
                    "text/css"
            );
        }

        if (fileName.endsWith(".js")
                || fileName.endsWith(".mjs")) {

            return MediaType.valueOf(
                    "application/javascript"
            );
        }

        if (fileName.endsWith(".json")) {

            return MediaType.APPLICATION_JSON;
        }

        if (fileName.endsWith(".svg")) {

            return MediaType.valueOf(
                    "image/svg+xml"
            );
        }

        if (fileName.endsWith(".png")) {

            return MediaType.valueOf(
                    "image/png"
            );
        }

        if (fileName.endsWith(".jpg")
                || fileName.endsWith(".jpeg")) {

            return MediaType.valueOf(
                    "image/jpeg"
            );
        }

        if (fileName.endsWith(".gif")) {

            return MediaType.valueOf(
                    "image/gif"
            );
        }

        if (fileName.endsWith(".webp")) {

            return MediaType.valueOf(
                    "image/webp"
            );
        }

        if (fileName.endsWith(".ico")) {

            return MediaType.valueOf(
                    "image/x-icon"
            );
        }

        if (fileName.endsWith(".woff")) {

            return MediaType.valueOf(
                    "font/woff"
            );
        }

        if (fileName.endsWith(".woff2")) {

            return MediaType.valueOf(
                    "font/woff2"
            );
        }

        if (fileName.endsWith(".ttf")) {

            return MediaType.valueOf(
                    "font/ttf"
            );
        }

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
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }

    // CHECK WHETHER PATH HAS FILE EXTENSION

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

    // STOP PREVIEW

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

        previewTokens.remove(
                projectId
        );

        System.out.println(
                "[Preview] Preview stopped for project: "
                        + projectId
        );
    }

    // WINDOWS CHECK

    private boolean isWindows() {

        return System
                .getProperty("os.name")
                .toLowerCase(
                        Locale.ROOT
                )
                .contains("win");
    }
}