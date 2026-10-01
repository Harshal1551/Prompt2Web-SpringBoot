const GenerationProgress = ({ step }) => {

    const steps = [
        {
            key: "project",
            label: "Creating project",
        },
        {
            key: "generation",
            label: "Preparing AI generation",
        },
        {
            key: "ai",
            label: "Generating website with AI",
        },
        {
            key: "files",
            label: "Saving generated files",
        },
        {
            key: "completed",
            label: "Website generated",
        },
    ];

    const currentIndex = steps.findIndex(
        (item) => item.key === step
    );

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/95 px-6">

            <div className="w-full max-w-lg rounded-2xl border border-slate-800 bg-slate-900 p-8 shadow-2xl">

                <div className="text-center">

                    <h2 className="text-2xl font-semibold text-white">
                        Building your website
                    </h2>

                    <p className="mt-2 text-sm text-slate-400">
                        AI is generating your project. Please wait...
                    </p>

                </div>

                <div className="mt-8 space-y-4">

                    {steps.map((item, index) => {

                        const completed =
                            index < currentIndex;

                        const active =
                            index === currentIndex;

                        return (
                            <div
                                key={item.key}
                                className="flex items-center gap-4"
                            >

                                <div
                                    className={`flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-sm font-semibold ${
                                        completed
                                            ? "bg-green-500/20 text-green-400"
                                            : active
                                                ? "bg-blue-500/20 text-blue-400"
                                                : "bg-slate-800 text-slate-500"
                                    }`}
                                >
                                    {completed
                                        ? "✓"
                                        : index + 1}
                                </div>

                                <span
                                    className={`text-sm ${
                                        completed
                                            ? "text-green-400"
                                            : active
                                                ? "text-white"
                                                : "text-slate-500"
                                    }`}
                                >
                                    {item.label}
                                </span>

                                {active && (
                                    <div className="ml-auto h-4 w-4 animate-spin rounded-full border-2 border-slate-700 border-t-blue-500" />
                                )}

                            </div>
                        );
                    })}

                </div>

            </div>

        </div>
    );
};

export default GenerationProgress;