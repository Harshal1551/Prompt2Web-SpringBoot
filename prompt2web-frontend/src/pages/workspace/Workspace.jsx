import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Editor from "@monaco-editor/react";

import { useAuth } from "../../context/AuthContext";
import projectFileService from "../../services/projectFileService";
import aiChatService from "../../services/aiChatService";
import projectExportService from "../../services/projectExportService";
import projectService from "../../services/projectService";
import previewService from "../../services/previewService";

import Preview from "../../components/workspace/Preview";



const buildFileTree = (files) => {

    const root = {
        type: "folder",
        name: "",
        children: {},
    };


    files.forEach((file) => {

        const parts = file.filePath
            .split("/")
            .filter(Boolean);

        let current = root;


        parts.forEach((part, index) => {

            const isFile =
                index === parts.length - 1;


            if (isFile) {

                current.children[part] = {
                    type: "file",
                    name: part,
                    file,
                };

            } else {

                if (!current.children[part]) {

                    current.children[part] = {
                        type: "folder",
                        name: part,
                        children: {},
                    };
                }


                current =
                    current.children[part];
            }
        });
    });


    return root;
};



const FileTreeNode = ({
    node,
    path,
    openFolders,
    setOpenFolders,
    selectedFile,
    onFileSelect,
}) => {

    const isOpen =
        openFolders[path];


    const toggleFolder = () => {

        setOpenFolders((current) => ({
            ...current,
            [path]: !current[path],
        }));
    };


    if (node.type === "file") {

        return (
            <button
                onClick={() =>
                    onFileSelect(node.file)
                }
                className={`flex w-full items-center gap-2 rounded-md px-2 py-1.5 text-left text-sm transition ${
                    selectedFile?.id === node.file.id
                        ? "bg-blue-600/20 text-blue-400"
                        : "text-slate-400 hover:bg-slate-800 hover:text-white"
                }`}
            >

                <span className="text-xs">

                    {node.name.endsWith(".jsx") ||
                    node.name.endsWith(".js")
                        ? "JS"
                        : node.name.endsWith(".css")
                            ? "CSS"
                            : node.name.endsWith(".html")
                                ? "HTML"
                                : node.name.endsWith(".json")
                                    ? "{}"
                                    : "•"}

                </span>


                <span className="truncate">
                    {node.name}
                </span>

            </button>
        );
    }


    const children =
        Object.values(node.children);


    children.sort((a, b) => {

        if (
            a.type === "folder" &&
            b.type === "file"
        ) {
            return -1;
        }


        if (
            a.type === "file" &&
            b.type === "folder"
        ) {
            return 1;
        }


        return a.name.localeCompare(
            b.name
        );
    });


    return (
        <div>

            {node.name && (
                <button
                    onClick={toggleFolder}
                    className="flex w-full items-center gap-2 rounded-md px-2 py-1.5 text-left text-sm text-slate-300 hover:bg-slate-800"
                >

                    <span className="text-xs">
                        {isOpen ? "▼" : "▶"}
                    </span>

                    <span>
                        📁
                    </span>

                    <span className="truncate">
                        {node.name}
                    </span>

                </button>
            )}


            {isOpen && (

                <div
                    className={
                        node.name
                            ? "ml-4 border-l border-slate-800 pl-2"
                            : ""
                    }
                >

                    {children.map((child) => (

                        <FileTreeNode
                            key={
                                child.type === "file"
                                    ? child.file.id
                                    : `${path}/${child.name}`
                            }
                            node={child}
                            path={`${path}/${child.name}`}
                            openFolders={openFolders}
                            setOpenFolders={setOpenFolders}
                            selectedFile={selectedFile}
                            onFileSelect={onFileSelect}
                        />

                    ))}

                </div>
            )}

        </div>
    );
};



const Workspace = () => {

    const { projectId } =
        useParams();

    const navigate =
        useNavigate();

    const { logout } =
        useAuth();


    const [files, setFiles] =
        useState([]);

    const [selectedFile, setSelectedFile] =
        useState(null);

    const [savedCode, setSavedCode] =
        useState("");

    const [code, setCode] =
        useState("");

    const [openFolders, setOpenFolders] =
        useState({});


    const fileTree =
        buildFileTree(files);


    const [loadingFiles, setLoadingFiles] =
        useState(true);

    const [loadingFile, setLoadingFile] =
        useState(false);

    const [saving, setSaving] =
        useState(false);


    const [error, setError] =
        useState("");

    const [saveMessage, setSaveMessage] =
        useState("");


    const [chatMessages, setChatMessages] =
        useState([]);

    const [chatInput, setChatInput] =
        useState("");

    const [chatLoading, setChatLoading] =
        useState(false);


    const [project, setProject] =
        useState(null);


    const [showPreview, setShowPreview] =
        useState(false);



    // ============================================================
    // LOAD PROJECT FILES
    // ============================================================

    useEffect(() => {

        const loadFiles = async () => {

            try {

                setLoadingFiles(true);
                setError("");


                const projectFiles =
                    await projectFileService.getProjectFiles(
                        projectId
                    );


                setFiles(
                    projectFiles
                );

            } catch (error) {

                console.error(
                    "Failed to load files:",
                    error
                );


                setError(
                    error.response?.data?.message ||
                    "Failed to load project files."
                );

            } finally {

                setLoadingFiles(false);
            }
        };


        loadFiles();

    }, [projectId]);



    // ============================================================
    // LOAD CHAT HISTORY
    // ============================================================

    useEffect(() => {

        const loadChatHistory =
            async () => {

                try {

                    const history =
                        await aiChatService.getChatHistory(
                            projectId
                        );


                    setChatMessages(
                        history
                    );

                } catch (error) {

                    console.error(
                        "Failed to load chat history:",
                        error
                    );
                }
            };


        loadChatHistory();

    }, [projectId]);



    // ============================================================
    // LOAD PROJECT
    // ============================================================

    useEffect(() => {

        const loadProject =
            async () => {

                try {

                    const data =
                        await projectService.getProjectById(
                            projectId
                        );


                    setProject(
                        data
                    );

                } catch (error) {

                    console.error(
                        "Failed to load project:",
                        error
                    );
                }
            };


        loadProject();

    }, [projectId]);



    // ============================================================
    // BROWSER REFRESH PROTECTION
    // ============================================================

    useEffect(() => {

        const handleBeforeUnload =
            (event) => {

                if (
                    selectedFile &&
                    code !== savedCode
                ) {

                    event.preventDefault();

                    event.returnValue = "";
                }
            };


        window.addEventListener(
            "beforeunload",
            handleBeforeUnload
        );


        return () => {

            window.removeEventListener(
                "beforeunload",
                handleBeforeUnload
            );

        };

    }, [
        selectedFile,
        code,
        savedCode
    ]);



    // ============================================================
    // SELECT FILE
    // ============================================================

    const handleFileSelect =
        async (file) => {

            if (
                selectedFile &&
                code !== savedCode
            ) {

                const discard =
                    window.confirm(
                        "You have unsaved changes. Do you want to discard them?"
                    );


                if (!discard) {
                    return;
                }
            }


            try {

                setLoadingFile(true);
                setSaveMessage("");
                setError("");


                const fullFile =
                    await projectFileService.getFile(
                        projectId,
                        file.id
                    );


                setSelectedFile(
                    fullFile
                );

                setCode(
                    fullFile.content || ""
                );

                setSavedCode(
                    fullFile.content || ""
                );

            } catch (error) {

                console.error(
                    "Failed to open file:",
                    error
                );


                setError(
                    error.response?.data?.message ||
                    "Failed to open file."
                );

            } finally {

                setLoadingFile(false);
            }
        };



    // ============================================================
    // REFRESH FILES
    // ============================================================

    const handleRefreshFiles =
        async () => {

            try {

                setLoadingFiles(true);
                setError("");


                const projectFiles =
                    await projectFileService.getProjectFiles(
                        projectId
                    );


                setFiles(
                    projectFiles
                );

            } catch (error) {

                console.error(
                    "Failed to refresh files:",
                    error
                );


                setError(
                    error.response?.data?.message ||
                    "Failed to refresh project files."
                );

            } finally {

                setLoadingFiles(false);
            }
        };



    // ============================================================
    // SAVE FILE
    // ============================================================

    const handleSave =
        async () => {

            if (!selectedFile) {
                return;
            }


            try {

                setSaving(true);
                setSaveMessage("");
                setError("");


                const updatedFile =
                    await projectFileService.updateFile(
                        projectId,
                        selectedFile.id,
                        {
                            filePath:
                                selectedFile.filePath,

                            fileName:
                                selectedFile.fileName,

                            content:
                                code,

                            language:
                                selectedFile.language
                        }
                    );


                setSelectedFile(
                    updatedFile
                );

                setCode(
                    updatedFile.content || ""
                );

                setSavedCode(
                    updatedFile.content || ""
                );


                setFiles(
                    (currentFiles) =>
                        currentFiles.map(
                            (file) =>
                                file.id ===
                                updatedFile.id
                                    ? updatedFile
                                    : file
                        )
                );


                setSaveMessage(
                    "Saved successfully"
                );

            } catch (error) {

                console.error(
                    "Failed to save file:",
                    error
                );


                setError(
                    error.response?.data?.message ||
                    "Failed to save file."
                );

            } finally {

                setSaving(false);
            }
        };



    // ============================================================
    // DETERMINE MONACO LANGUAGE
    // ============================================================

    const getEditorLanguage =
        (file) => {

            if (!file) {
                return "plaintext";
            }


            const fileName =
                file.fileName?.toLowerCase() ||
                "";


            const language =
                file.language?.toLowerCase() ||
                "";


            if (
                language === "javascript" ||
                language === "js"
            ) {
                return "javascript";
            }


            if (language === "jsx") {
                return "javascript";
            }


            if (
                language === "typescript" ||
                language === "ts"
            ) {
                return "typescript";
            }


            if (language === "html") {
                return "html";
            }


            if (language === "css") {
                return "css";
            }


            if (language === "json") {
                return "json";
            }


            if (language === "java") {
                return "java";
            }


            if (fileName.endsWith(".jsx")) {
                return "javascript";
            }


            if (fileName.endsWith(".js")) {
                return "javascript";
            }


            if (fileName.endsWith(".ts")) {
                return "typescript";
            }


            if (fileName.endsWith(".tsx")) {
                return "typescript";
            }


            if (fileName.endsWith(".html")) {
                return "html";
            }


            if (fileName.endsWith(".css")) {
                return "css";
            }


            if (fileName.endsWith(".json")) {
                return "json";
            }


            if (fileName.endsWith(".java")) {
                return "java";
            }


            return "plaintext";
        };



    // ============================================================
    // STOP PREVIEW
    // ============================================================

    const stopProjectPreview =
        async () => {

            try {

                await previewService.stopPreview(
                    projectId
                );

            } catch (error) {

                console.error(
                    "Failed to stop project preview:",
                    error
                );
            }
        };



    // ============================================================
    // TOGGLE PREVIEW
    // ============================================================

    const handleTogglePreview =
        async () => {

            if (!showPreview) {

                setShowPreview(
                    true
                );

                return;
            }


            await stopProjectPreview();


            setShowPreview(
                false
            );
        };



    // ============================================================
    // BACK TO DASHBOARD
    // ============================================================

    const handleBackToDashboard =
        async () => {

            if (
                selectedFile &&
                code !== savedCode
            ) {

                const discard =
                    window.confirm(
                        "You have unsaved changes. Do you want to leave without saving?"
                    );


                if (!discard) {
                    return;
                }
            }


            await stopProjectPreview();


            navigate(
                "/dashboard"
            );
        };



    // ============================================================
    // LOGOUT
    // ============================================================

    const handleLogout =
        async () => {

            await stopProjectPreview();


            logout();

            navigate(
                "/login"
            );
        };



    // ============================================================
    // SEND AI CHAT
    // ============================================================

    const handleSendChat =
        async () => {

            const message =
                chatInput.trim();


            if (
                !message ||
                chatLoading
            ) {
                return;
            }


            try {

                setChatLoading(
                    true
                );


                const userMessage = {

                    id:
                        `user-${Date.now()}`,

                    role:
                        "USER",

                    message,

                    fileId:
                        selectedFile?.id ||
                        null,

                    createdAt:
                        new Date().toISOString(),
                };


                setChatMessages(
                    (current) => [
                        ...current,
                        userMessage,
                    ]
                );


                setChatInput("");


                const response =
                    await aiChatService.sendMessage(
                        projectId,
                        message,
                        selectedFile?.id ||
                            null
                    );


                const assistantMessage = {

                    id:
                        `assistant-${Date.now()}`,

                    role:
                        "ASSISTANT",

                    message:
                        response.message,

                    fileId:
                        response.fileId ||
                        null,

                    createdAt:
                        new Date().toISOString(),
                };


                setChatMessages(
                    (current) => [
                        ...current,
                        assistantMessage,
                    ]
                );


                // AI modified selected file
                if (
                    response.updatedContent !== null &&
                    response.updatedContent !== undefined &&
                    selectedFile
                ) {

                    setCode(
                        response.updatedContent
                    );


                    setSelectedFile(
                        (current) => ({
                            ...current,
                            content:
                                response.updatedContent,
                        })
                    );


                    setFiles(
                        (currentFiles) =>
                            currentFiles.map(
                                (file) =>
                                    file.id ===
                                    selectedFile.id
                                        ? {
                                            ...file,
                                            content:
                                                response.updatedContent,
                                        }
                                        : file
                            )
                    );
                }

            } catch (error) {

                console.error(
                    "AI chat failed:",
                    error
                );


                const errorMessage = {

                    id:
                        `error-${Date.now()}`,

                    role:
                        "ASSISTANT",

                    message:
                        error.response?.data?.message ||
                        "Failed to process AI request.",

                    fileId:
                        null,

                    createdAt:
                        new Date().toISOString(),
                };


                setChatMessages(
                    (current) => [
                        ...current,
                        errorMessage,
                    ]
                );

            } finally {

                setChatLoading(
                    false
                );
            }
        };



    // ============================================================
    // EXPORT
    // ============================================================

    const handleExport =
        async () => {

            try {

                const response =
                    await projectExportService.exportProject(
                        projectId
                    );


                const blob =
                    new Blob(
                        [response.data],
                        {
                            type:
                                "application/zip",
                        }
                    );


                const url =
                    window.URL.createObjectURL(
                        blob
                    );


                const link =
                    document.createElement(
                        "a"
                    );


                link.href =
                    url;

                link.download =
                    "prompt2web-project.zip";


                document.body.appendChild(
                    link
                );


                link.click();

                link.remove();


                window.URL.revokeObjectURL(
                    url
                );

            } catch (error) {

                console.error(
                    "Project export failed:",
                    error
                );

                alert(
                    "Failed to export project."
                );
            }
        };



    return (
        <div className="min-h-screen bg-slate-950 text-white">

            {/* ===================================================
                HEADER
            =================================================== */}

            <header className="border-b border-slate-800">

                <div className="flex h-16 items-center justify-between px-6">

                    <div className="flex items-center gap-4">

                        <button
                            onClick={
                                handleBackToDashboard
                            }
                            className="text-slate-400 transition hover:text-white"
                        >
                            ←
                        </button>


                        <div>

                            <h1 className="font-semibold">
                                {project?.name ||
                                    "Prompt2Web"}
                            </h1>


                            <p className="text-xs text-slate-500">
                                Project Workspace
                            </p>

                        </div>

                    </div>


                    <div className="flex items-center gap-3">

                        <button
                            onClick={
                                handleTogglePreview
                            }
                            className="rounded-lg border border-slate-700 px-4 py-2 text-sm font-medium text-white transition hover:bg-slate-800"
                        >
                            {showPreview
                                ? "Editor"
                                : "Preview"}
                        </button>


                        <button
                            onClick={
                                handleExport
                            }
                            className="rounded-lg border border-slate-700 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800"
                        >
                            Export
                        </button>


                        <button
                            onClick={
                                handleLogout
                            }
                            className="rounded-lg border border-slate-700 px-4 py-2 text-sm text-slate-300 transition hover:bg-slate-800"
                        >
                            Logout
                        </button>

                    </div>

                </div>

            </header>



            {/* ===================================================
                WORKSPACE
            =================================================== */}

            <main className="flex h-[calc(100vh-4rem)]">


                {/* =================================================
                    FILE EXPLORER
                ================================================= */}

                <aside className="w-64 shrink-0 border-r border-slate-800 bg-slate-900">

                    <div className="flex items-center justify-between border-b border-slate-800 px-4 py-4">

                        <h2 className="text-sm font-semibold">
                            Files
                        </h2>


                        <button
                            onClick={
                                handleRefreshFiles
                            }
                            disabled={
                                loadingFiles
                            }
                            className="rounded-md px-2 py-1 text-xs text-slate-400 transition hover:bg-slate-800 hover:text-white disabled:cursor-not-allowed disabled:opacity-50"
                        >
                            {loadingFiles
                                ? "..."
                                : "Refresh"}
                        </button>

                    </div>



                    <div className="h-[calc(100%-57px)] overflow-y-auto p-3">

                        {loadingFiles && (
                            <p className="px-2 py-3 text-sm text-slate-500">
                                Loading files...
                            </p>
                        )}


                        {error && (
                            <p className="px-2 py-3 text-sm text-red-400">
                                {error}
                            </p>
                        )}


                        {!loadingFiles &&
                            !error &&
                            files.length === 0 && (

                                <p className="px-2 py-3 text-sm text-slate-500">
                                    No files found.
                                </p>
                            )}


                        {!loadingFiles &&
                            !error &&
                            files.length > 0 && (

                                <div className="space-y-1">

                                    {Object.values(
                                        fileTree.children
                                    )
                                        .sort(
                                            (a, b) => {

                                                if (
                                                    a.type ===
                                                        "folder" &&
                                                    b.type ===
                                                        "file"
                                                ) {
                                                    return -1;
                                                }


                                                if (
                                                    a.type ===
                                                        "file" &&
                                                    b.type ===
                                                        "folder"
                                                ) {
                                                    return 1;
                                                }


                                                return a.name.localeCompare(
                                                    b.name
                                                );
                                            }
                                        )
                                        .map(
                                            (node) => (

                                                <FileTreeNode
                                                    key={
                                                        node.type ===
                                                        "file"
                                                            ? node
                                                                .file
                                                                .id
                                                            : node.name
                                                    }
                                                    node={
                                                        node
                                                    }
                                                    path={
                                                        node.name
                                                    }
                                                    openFolders={
                                                        openFolders
                                                    }
                                                    setOpenFolders={
                                                        setOpenFolders
                                                    }
                                                    selectedFile={
                                                        selectedFile
                                                    }
                                                    onFileSelect={
                                                        handleFileSelect
                                                    }
                                                />

                                            )
                                        )}

                                </div>
                            )}

                    </div>

                </aside>



                {/* =================================================
                    CODE EDITOR / LIVE PREVIEW
                ================================================= */}

                <section className="flex min-w-0 flex-1 flex-col">

                    <div className="flex h-12 items-center justify-between border-b border-slate-800 px-5">

                        <span className="text-sm text-slate-400">

                            {showPreview
                                ? "Live Preview"
                                : selectedFile
                                    ? selectedFile.filePath
                                    : "No file selected"}

                        </span>


                        <div className="flex items-center gap-3">

                            {saveMessage &&
                                !showPreview && (

                                    <span className="text-xs text-green-400">
                                        {saveMessage}
                                    </span>
                                )}


                            {!showPreview &&
                                selectedFile && (

                                    <button
                                        onClick={
                                            handleSave
                                        }
                                        disabled={
                                            saving
                                        }
                                        className="rounded-md bg-blue-600 px-4 py-1.5 text-sm font-medium text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
                                    >

                                        {saving
                                            ? "Saving..."
                                            : "Save"}

                                    </button>
                                )}

                        </div>

                    </div>


                    <div className="min-h-0 flex-1 overflow-hidden">

                        {showPreview ? (

                            <Preview
                                projectId={
                                    projectId
                                }
                            />

                        ) : (

                            <>

                                {loadingFile ? (

                                    <div className="flex h-full items-center justify-center text-sm text-slate-500">
                                        Loading file...
                                    </div>

                                ) : selectedFile ? (

                                    <Editor
                                        height="100%"
                                        language={
                                            getEditorLanguage(
                                                selectedFile
                                            )
                                        }
                                        theme="vs-dark"
                                        value={code}
                                        onChange={
                                            (value) =>
                                                setCode(
                                                    value ??
                                                    ""
                                                )
                                        }
                                        options={{
                                            minimap: {
                                                enabled: true
                                            },
                                            fontSize: 14,
                                            wordWrap: "on",
                                            automaticLayout: true,
                                            scrollBeyondLastLine: false,
                                            padding: {
                                                top: 16
                                            }
                                        }}
                                    />

                                ) : (

                                    <div className="flex h-full items-center justify-center">

                                        <div className="text-center">

                                            <div className="text-4xl">
                                                {"</>"}
                                            </div>


                                            <p className="mt-3 text-slate-500">
                                                Select a file to view its code
                                            </p>

                                        </div>

                                    </div>
                                )}

                            </>
                        )}

                    </div>

                </section>



                {/* =================================================
                    AI CHAT
                ================================================= */}

                <aside className="flex w-80 shrink-0 flex-col border-l border-slate-800 bg-slate-900">

                    <div className="border-b border-slate-800 px-4 py-4">

                        <div className="flex items-center justify-between">

                            <h2 className="text-sm font-semibold">
                                AI Assistant
                            </h2>


                            {selectedFile && (

                                <span className="max-w-32 truncate text-xs text-blue-400">
                                    {selectedFile.fileName}
                                </span>
                            )}

                        </div>

                    </div>



                    <div className="flex-1 space-y-4 overflow-y-auto p-4">

                        {chatMessages.length === 0 && (

                            <div className="flex h-full items-center justify-center">

                                <p className="text-center text-sm text-slate-500">
                                    Ask AI to explain or modify your website.
                                </p>

                            </div>
                        )}


                        {chatMessages.map(
                            (message) => (

                                <div
                                    key={
                                        message.id
                                    }
                                    className={
                                        message.role ===
                                        "USER"
                                            ? "flex justify-end"
                                            : "flex justify-start"
                                    }
                                >

                                    <div
                                        className={
                                            message.role ===
                                            "USER"
                                                ? "max-w-[85%] rounded-xl bg-blue-600 px-4 py-3 text-sm text-white"
                                                : "max-w-[85%] rounded-xl border border-slate-700 bg-slate-800 px-4 py-3 text-sm text-slate-300"
                                        }
                                    >
                                        {
                                            message.message
                                        }
                                    </div>

                                </div>
                            )
                        )}


                        {chatLoading && (

                            <div className="flex justify-start">

                                <div className="rounded-xl border border-slate-700 bg-slate-800 px-4 py-3 text-sm text-slate-400">
                                    AI is thinking...
                                </div>

                            </div>
                        )}

                    </div>



                    <div className="border-t border-slate-800 p-4">

                        {!selectedFile && (

                            <p className="mb-2 text-xs text-slate-500">
                                No file selected — AI will answer normally.
                            </p>
                        )}


                        {selectedFile && (

                            <p className="mb-2 text-xs text-slate-500">
                                AI will modify{" "}
                                {selectedFile.fileName}
                            </p>
                        )}


                        <textarea
                            rows={4}
                            value={chatInput}
                            onChange={
                                (e) =>
                                    setChatInput(
                                        e.target.value
                                    )
                            }
                            onKeyDown={
                                (e) => {

                                    if (
                                        e.key ===
                                            "Enter" &&
                                        !e.shiftKey
                                    ) {

                                        e.preventDefault();

                                        handleSendChat();
                                    }
                                }
                            }
                            placeholder="Ask AI..."
                            className="w-full resize-none rounded-lg border border-slate-700 bg-slate-950 p-3 text-sm text-white outline-none placeholder:text-slate-600 focus:border-blue-500"
                        />


                        <button
                            onClick={
                                handleSendChat
                            }
                            disabled={
                                !chatInput.trim() ||
                                chatLoading
                            }
                            className="mt-2 w-full rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
                        >
                            {chatLoading
                                ? "Thinking..."
                                : "Send"}
                        </button>

                    </div>

                </aside>

            </main>

        </div>
    );
};


export default Workspace;