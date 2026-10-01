import api from "./api";

const aiChatService = {

    sendMessage: async (projectId, message, fileId = null) => {

        const response = await api.post(
            `/api/projects/${projectId}/ai/chat`,
            {
                message,
                fileId,
            }
        );

        return response.data;
    },

    getChatHistory: async (projectId) => {

        const response = await api.get(
            `/api/projects/${projectId}/ai/chat`
        );

        return response.data;
    },

};

export default aiChatService;