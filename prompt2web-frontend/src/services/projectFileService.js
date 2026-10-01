import api from "./api";

const projectFileService = {

    getProjectFiles: async (projectId) => {
        const response = await api.get(
            `/api/projects/${projectId}/files`
        );

        return response.data;
    },

    getFile: async (projectId, fileId) => {
        const response = await api.get(
            `/api/projects/${projectId}/files/${fileId}`
        );

        return response.data;
    },

    updateFile: async (projectId, fileId, fileData) => {
        const response = await api.put(
            `/api/projects/${projectId}/files/${fileId}`,
            fileData
        );

        return response.data;
    },

};

export default projectFileService;