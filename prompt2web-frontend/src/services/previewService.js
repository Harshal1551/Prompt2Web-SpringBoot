import api from "./api";


/*
 * Store only requests that are currently starting.
 *
 * This prevents React StrictMode from sending two
 * POST /preview requests at the same time.
 */
const pendingPreviewRequests = new Map();


const previewService = {

    // ============================================================
    // START PREVIEW
    // ============================================================

    startPreview: async (projectId) => {

        const existingRequest =
            pendingPreviewRequests.get(projectId);

        if (existingRequest) {
            return existingRequest;
        }


        const request =
            api.post(
                `/api/projects/${projectId}/preview`
            )
                .then((response) => {
                    return response.data;
                })
                .finally(() => {

                    /*
                     * Remove only after the request finishes.
                     *
                     * This means future Preview openings can
                     * request a fresh backend preview.
                     */
                    pendingPreviewRequests.delete(
                        projectId
                    );
                });


        pendingPreviewRequests.set(
            projectId,
            request
        );


        return request;
    },


    // ============================================================
    // STOP PREVIEW
    // ============================================================

    stopPreview: async (projectId) => {

        /*
         * If preview startup is still running, wait for it
         * before asking backend to stop the preview.
         *
         * This prevents:
         *
         * POST /preview
         * DELETE /preview
         *
         * from racing each other.
         */

        const pendingRequest =
            pendingPreviewRequests.get(projectId);


        if (pendingRequest) {

            try {

                await pendingRequest;

            } catch (error) {

                /*
                 * Startup failure is already handled by
                 * startPreview().
                 *
                 * We still continue to DELETE.
                 */
            }
        }


        pendingPreviewRequests.delete(
            projectId
        );


        const response =
            await api.delete(
                `/api/projects/${projectId}/preview`
            );


        return response.data;
    },
};


export default previewService;