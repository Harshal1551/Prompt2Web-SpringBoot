import { useEffect, useState } from "react";

import previewService from "../../services/previewService";


const Preview = ({ projectId }) => {

    const [previewUrl, setPreviewUrl] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");


    useEffect(() => {

        let active = true;

        const startPreview = async () => {

            try {

                setLoading(true);
                setError("");
                setPreviewUrl("");

                const response =
                    await previewService.startPreview(
                        projectId
                    );

                if (!active) {
                    return;
                }

                setPreviewUrl(response.url);

            } catch (error) {

                console.error(
                    "Failed to start project preview:",
                    error
                );

                if (active) {

                    setError(
                        error.response?.data?.message ||
                        error.message ||
                        "Failed to start project preview."
                    );
                }

            } finally {

                if (active) {
                    setLoading(false);
                }
            }
        };

        startPreview();


        /*
         * IMPORTANT:
         *
         * Do NOT stop the backend preview here.
         *
         * React StrictMode can mount/unmount this component
         * during development. Stopping the preview here can
         * kill the Vite server while the iframe is loading.
         *
         * Workspace handles the actual preview shutdown when
         * the user leaves the project or switches back to Editor.
         */

        return () => {
            active = false;
        };

    }, [projectId]);


    // ============================================================
    // LOADING
    // ============================================================

    if (loading) {

        return (
            <div className="flex h-full items-center justify-center bg-slate-950">

                <div className="text-center">

                    <div className="mx-auto mb-4 h-8 w-8 animate-spin rounded-full border-2 border-slate-700 border-t-blue-500" />

                    <p className="text-sm text-slate-400">
                        Starting live preview...
                    </p>

                    <p className="mt-2 text-xs text-slate-600">
                        Installing dependencies and starting Vite
                    </p>

                </div>

            </div>
        );
    }


    // ============================================================
    // ERROR
    // ============================================================

    if (error) {

        return (
            <div className="flex h-full items-center justify-center bg-slate-950 p-6">

                <div className="max-w-lg rounded-xl border border-red-900 bg-red-950/30 p-6">

                    <h3 className="font-semibold text-red-400">
                        Preview failed
                    </h3>

                    <p className="mt-3 whitespace-pre-wrap text-sm text-red-200">
                        {error}
                    </p>

                </div>

            </div>
        );
    }


    // ============================================================
    // LIVE PREVIEW
    // ============================================================

    return (
        <div className="h-full w-full overflow-hidden bg-white">

            {previewUrl && (
                <iframe
                    key={previewUrl}
                    src={previewUrl}
                    title="Prompt2Web Live Preview"
                    className="block h-full w-full border-0"
                />
            )}

        </div>
    );
};


export default Preview;