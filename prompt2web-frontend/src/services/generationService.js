import api from "./api";

const generationService = {

    createGeneration: async (projectId, prompt) => {

        const response = await api.post(
            `/api/projects/${projectId}/generations`,
            {
                prompt,
            }
        );

        return response.data;
    },

    startGeneration: async (projectId, generationId) => {

        const response = await api.post(
            `/api/projects/${projectId}/generations/${generationId}/start`
        );

        return response.data;
    },

    getGeneration: async (projectId, generationId) => {

        const response = await api.get(
            `/api/projects/${projectId}/generations/${generationId}`
        );

        return response.data;
    },

    getProjectGenerations: async (projectId) => {

        const response = await api.get(
            `/api/projects/${projectId}/generations`
        );

        return response.data;
    },

};

export default generationService;