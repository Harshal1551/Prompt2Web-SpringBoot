import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

import generationService from "../../services/generationService";
import GenerationProgress from "../../components/dashboard/GenerationProgress";
import projectService from "../../services/projectService";

const Dashboard = () => {

    const navigate = useNavigate();
    const { logout } = useAuth();

    const [prompt, setPrompt] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const [generationStep, setGenerationStep] = useState(null);

    const [projects, setProjects] = useState([]);
    const [projectsLoading, setProjectsLoading] = useState(true);

    const [projectStatuses, setProjectStatuses] = useState({});

    useEffect(() => {
        const loadProjects = async () => {
            try {
                setProjectsLoading(true);

                const data = await projectService.getProjects();
                setProjects(data);

                const statuses = {};

                for (const project of data) {
                    try {
                        const generations =
                            await generationService.getProjectGenerations(project.id);

                        if (generations.length === 0) {
                            statuses[project.id] = "NO_GENERATION";
                        } else {
                            const latestGeneration =
                                generations[generations.length - 1];

                            statuses[project.id] = latestGeneration.status;
                        }
                    } catch (error) {
                        console.error(
                            `Failed to load generation status for ${project.id}`,
                            error
                        );

                        statuses[project.id] = "UNKNOWN";
                    }
                }

                setProjectStatuses(statuses);

            } catch (error) {
                console.error("Failed to load projects:", error);
            } finally {
                setProjectsLoading(false);
            }
        };

        loadProjects();
    }, []);


    const generateProjectName = (prompt) => {
        let name = prompt
            .replace(/create\s+(a|an)?\s*/i, "")
            .replace(/website\s+(for|of)\s*/i, "")
            .trim();

        if (!name) {
            return "AI Website";
        }

        name = name
            .replace(/\s+/g, " ")
            .replace(/[.!?,]+$/, "");

        if (name.length > 40) {
            name = name.substring(0, 40).trim();
        }

        return name;
    };


    const handleCreateProject = async (e) => {
        e.preventDefault();

        if (!prompt.trim()) {
            return;
        }

        setLoading(true);
        setError("");
        setGenerationStep("project");

        try {
            // =====================================================
            // 1. CREATE PROJECT
            // =====================================================

            const project = await projectService.createProject({
                name: generateProjectName(prompt),
                initialPrompt: prompt,
                framework: "React"
            });

            setGenerationStep("project");

            // =====================================================
            // 2. CREATE GENERATION
            // =====================================================

            const generation = await generationService.createGeneration(
                project.id,
                prompt
            );

            setGenerationStep("generation");

            // =====================================================
            // 3. START BACKGROUND GENERATION
            // =====================================================

            const startedGeneration =
                await generationService.startGeneration(
                    project.id,
                    generation.id
                );

            // Backend should now return IN_PROGRESS
            if (startedGeneration.status !== "IN_PROGRESS") {
                throw new Error(
                    "Generation could not be started."
                );
            }

            setGenerationStep("ai");

            // =====================================================
            // 4. POLL GENERATION STATUS
            // =====================================================

            let finalGeneration = startedGeneration;

            const maxAttempts = 120;
            const pollingInterval = 1000;

            for (let attempt = 0; attempt < maxAttempts; attempt++) {

                await new Promise((resolve) =>
                    setTimeout(resolve, pollingInterval)
                );

                finalGeneration =
                    await generationService.getGeneration(
                        project.id,
                        generation.id
                    );

                // -----------------------------------------------
                // GENERATION COMPLETED
                // -----------------------------------------------

                if (finalGeneration.status === "COMPLETED") {

                    setGenerationStep("completed");

                    navigate(`/workspace/${project.id}`);

                    return;
                }

                // -----------------------------------------------
                // GENERATION FAILED
                // -----------------------------------------------

                if (finalGeneration.status === "FAILED") {

                    setGenerationStep(null);

                    setError(
                        "Website generation failed. Please try again later."
                    );

                    return;
                }
            }

            // =====================================================
            // 5. TIMEOUT
            // =====================================================

            setGenerationStep(null);

            setError(
                "Website generation is taking too long. Please check the project status later."
            );

        } catch (error) {

            console.error(
                "Website generation failed:",
                error
            );

            setGenerationStep(null);

            setError(
                error.response?.data?.message ||
                error.message ||
                "Failed to generate website."
            );

        } finally {

            setLoading(false);
        }
    };

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    const handleRenameProject = async (project) => {
        const newName = window.prompt(
            "Enter new project name:",
            project.name
        );

        if (!newName || !newName.trim()) {
            return;
        }

        try {
            const updatedProject = await projectService.updateProject(
                project.id,
                {
                    name: newName.trim(),
                    initialPrompt: project.initialPrompt,
                    framework: project.framework,
                }
            );

            setProjects((currentProjects) =>
                currentProjects.map((currentProject) =>
                    currentProject.id === updatedProject.id
                        ? updatedProject
                        : currentProject
                )
            );

        } catch (error) {
            console.error(
                "Failed to rename project:",
                error
            );

            setError(
                error.response?.data?.message ||
                "Failed to rename project."
            );
        }
    };

    const handleDeleteProject = async (projectId) => {
        const confirmed = window.confirm(
            "Are you sure you want to delete this project?"
        );

        if (!confirmed) {
            return;
        }

        try {
            await projectService.deleteProject(projectId);

            setProjects((currentProjects) =>
                currentProjects.filter(
                    (project) => project.id !== projectId
                )
            );

            setProjectStatuses((currentStatuses) => {
                const updated = { ...currentStatuses };
                delete updated[projectId];
                return updated;
            });

        } catch (error) {
            console.error("Failed to delete project:", error);

            setError(
                error.response?.data?.message ||
                "Failed to delete project."
            );
        }
    };





    return (
        <div className="min-h-screen bg-slate-950 text-white">
            {generationStep && (
                <GenerationProgress
                    step={generationStep}
                />
            )}

            {/* Navbar */}
            <header className="border-b border-slate-800">

                <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">

                    <div>
                        <h1 className="text-xl font-bold">
                            Prompt2Web
                        </h1>

                        <p className="text-xs text-slate-500">
                            AI Website Builder
                        </p>
                    </div>

                    <button
                        onClick={handleLogout}
                        className="rounded-lg border border-slate-700 px-4 py-2 text-sm text-slate-300 transition hover:bg-slate-800"
                    >
                        Logout
                    </button>

                </div>

            </header>

            {/* Main */}
            <main className="mx-auto max-w-7xl px-6 py-12">

                {/* Hero */}
                <section className="mx-auto max-w-3xl text-center">

                    <div className="mb-4 inline-flex rounded-full border border-blue-500/20 bg-blue-500/10 px-4 py-2 text-sm text-blue-400">
                        AI Website Builder
                    </div>

                    <h2 className="text-4xl font-bold tracking-tight sm:text-5xl">
                        Build your website with AI
                    </h2>

                    <p className="mx-auto mt-5 max-w-2xl text-slate-400">
                        Describe the website you want to build.
                        Prompt2Web will generate the project for you.
                    </p>

                </section>

                {/* Create Project */}
                <section className="mx-auto mt-10 max-w-4xl">

                    <div className="rounded-2xl border border-slate-800 bg-slate-900 p-6 shadow-xl">

                        <div className="mb-4">

                            <h3 className="text-lg font-semibold">
                                Create a new website
                            </h3>

                            <p className="mt-1 text-sm text-slate-400">
                                Tell AI what you want to build.
                            </p>

                        </div>

                        {error && (
                            <div className="mb-4 rounded-lg border border-red-800 bg-red-950/40 px-4 py-3 text-sm text-red-400">
                                {error}
                            </div>
                        )}

                        <form onSubmit={handleCreateProject}>

                            <textarea
                                value={prompt}
                                onChange={(e) =>
                                    setPrompt(e.target.value)
                                }
                                rows={6}
                                placeholder="Example: Create a modern portfolio website for a Java Full Stack Developer with Home, About, Skills, Projects and Contact sections..."
                                className="w-full resize-none rounded-xl border border-slate-700 bg-slate-950 p-4 text-sm text-white outline-none placeholder:text-slate-600 focus:border-blue-500"
                            />

                            <div className="mt-4 flex justify-end">

                                <button
                                    type="submit"
                                    disabled={!prompt.trim() || loading}
                                    className="rounded-lg bg-blue-600 px-6 py-3 font-semibold transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
                                >
                                    {loading ? "Creating..." : "Create Website"}
                                </button>

                            </div>

                        </form>

                    </div>

                </section>

                {/* Projects */}
                <section className="mt-14">

                    <div className="mt-10">
                        <h2 className="mb-4 text-xl font-semibold text-white">
                            Your Projects
                        </h2>

                        {projectsLoading ? (
                            <p className="text-slate-400">Loading projects...</p>
                        ) : projects.length === 0 ? (
                            <p className="text-slate-400">
                                No projects created yet.
                            </p>
                        ) : (
                            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                                {projects.map((project) => (
                                    <div
                                        key={project.id}
                                        onClick={() => navigate(`/workspace/${project.id}`)}
                                        className="cursor-pointer rounded-xl border border-slate-800 bg-slate-900 p-5 transition hover:border-slate-600 hover:bg-slate-800"
                                    >
                                        <h3 className="font-semibold text-white">
                                            {project.name}
                                        </h3>

                                        <div className="mt-2">
                                            {projectStatuses[project.id] === "COMPLETED" && (
                                                <span className="text-xs font-medium text-green-400">
                                                    ● Ready
                                                </span>
                                            )}

                                            {projectStatuses[project.id] === "FAILED" && (
                                                <span className="text-xs font-medium text-red-400">
                                                    ● Generation Failed
                                                </span>
                                            )}

                                            {projectStatuses[project.id] === "IN_PROGRESS" && (
                                                <span className="text-xs font-medium text-yellow-400">
                                                    ● Generating...
                                                </span>
                                            )}

                                            {projectStatuses[project.id] === "PENDING" && (
                                                <span className="text-xs font-medium text-yellow-400">
                                                    ● Pending
                                                </span>
                                            )}

                                            {projectStatuses[project.id] === "NO_GENERATION" && (
                                                <span className="text-xs font-medium text-slate-500">
                                                    ● No Generation
                                                </span>
                                            )}
                                        </div>

                                        <p className="mt-2 text-sm text-slate-400">
                                            {project.framework || "React"}
                                        </p>

                                        <p className="mt-3 line-clamp-2 text-sm text-slate-500">
                                            {project.initialPrompt}
                                        </p>

                                        <button
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                handleRenameProject(project);
                                            }}
                                            className="mr-2 mt-4 rounded-md border border-slate-700 px-3 py-1.5 text-xs text-slate-300 transition hover:bg-slate-800"
                                        >
                                            Rename
                                        </button>

                                        <button
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                handleDeleteProject(project.id);
                                            }}
                                            className="mt-4 rounded-md border border-red-900 px-3 py-1.5 text-xs text-red-400 transition hover:bg-red-950"
                                        >
                                            Delete
                                        </button>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>

                </section>

            </main>

        </div>
    );
};

export default Dashboard;