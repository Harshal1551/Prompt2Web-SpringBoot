import api from "./api";

const projectExportService = {
    exportProject: async (projectId) => {
        const response = await api.get(
            `/api/projects/${projectId}/export`,
            {
                responseType: "blob",
            }
        );

        return response;
    },
};

export default projectExportService;