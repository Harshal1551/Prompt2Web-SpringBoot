import api from "./api";

const projectService = {

    createProject: async (projectData) => {
        const response = await api.post("/api/projects", projectData);
        return response.data;
    },

    getProjects: async () => {
        const response = await api.get("/api/projects");
        return response.data;
    },

    updateProject: async (projectId, projectData) => {
        const response = await api.put(
            `/api/projects/${projectId}`,
            projectData
        );

        return response.data;
    },

    getProjectById: async (projectId) => {
        const response = await api.get(`/api/projects/${projectId}`);
        return response.data;
    },

    deleteProject: async (projectId) => {
        const response = await api.delete(`/api/projects/${projectId}`);
        return response.data;
    },

};

export default projectService;