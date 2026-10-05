import { Link } from "react-router-dom";
import {
  Sparkles,
  Code2,
  Zap,
  Eye,
  ArrowRight,
  CheckCircle2,
} from "lucide-react";

const Landing = () => {
  return (
    <div className="min-h-screen bg-slate-950 text-white">
      {/* Navbar */}
      <nav className="border-b border-white/10 bg-slate-950/80 backdrop-blur">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-5">
          <Link to="/" className="flex items-center gap-2">
            <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-br from-violet-500 to-fuchsia-500">
              <Sparkles className="h-5 w-5" />
            </div>

            <span className="text-xl font-bold tracking-tight">
              Prompt2Web
            </span>
          </Link>

          <div className="flex items-center gap-3">
            <Link
              to="/login"
              className="rounded-lg px-4 py-2 text-sm font-medium text-slate-300 transition hover:bg-white/10 hover:text-white"
            >
              Login
            </Link>

            <Link
              to="/register"
              className="rounded-lg bg-violet-600 px-4 py-2 text-sm font-semibold transition hover:bg-violet-500"
            >
              Get Started
            </Link>
          </div>
        </div>
      </nav>

      {/* Hero */}
      <main>
        <section className="relative overflow-hidden">
          {/* Background glow */}
          <div className="absolute left-1/2 top-0 h-[500px] w-[700px] -translate-x-1/2 rounded-full bg-violet-600/20 blur-3xl" />

          <div className="relative mx-auto max-w-7xl px-6 pb-24 pt-24 text-center lg:pb-32 lg:pt-32">
            <div className="mx-auto mb-6 flex w-fit items-center gap-2 rounded-full border border-violet-400/20 bg-violet-500/10 px-4 py-2 text-sm text-violet-300">
              <Sparkles className="h-4 w-4" />
              AI-powered website builder
            </div>

            <h1 className="mx-auto max-w-5xl text-5xl font-extrabold leading-tight tracking-tight sm:text-6xl lg:text-7xl">
              Turn your ideas into
              <span className="block bg-gradient-to-r from-violet-400 via-fuchsia-400 to-pink-400 bg-clip-text text-transparent">
                websites with AI
              </span>
            </h1>

            <p className="mx-auto mt-6 max-w-2xl text-lg leading-8 text-slate-400 sm:text-xl">
              Describe the website you want in plain English. Prompt2Web
              creates the project, generates the code, and gives you a live
              preview.
            </p>

            <div className="mt-10 flex flex-col justify-center gap-4 sm:flex-row">
              <Link
                to="/register"
                className="inline-flex items-center justify-center gap-2 rounded-xl bg-violet-600 px-7 py-4 font-semibold shadow-lg shadow-violet-600/20 transition hover:bg-violet-500"
              >
                Start Building
                <ArrowRight className="h-5 w-5" />
              </Link>

              <Link
                to="/login"
                className="inline-flex items-center justify-center rounded-xl border border-white/15 bg-white/5 px-7 py-4 font-semibold text-slate-200 transition hover:bg-white/10"
              >
                Sign In
              </Link>
            </div>

            {/* Product preview */}
            <div className="mx-auto mt-20 max-w-5xl">
              <div className="overflow-hidden rounded-2xl border border-white/10 bg-slate-900 shadow-2xl shadow-violet-900/20">
                {/* Browser header */}
                <div className="flex items-center gap-2 border-b border-white/10 bg-slate-900 px-5 py-3">
                  <span className="h-3 w-3 rounded-full bg-red-400" />
                  <span className="h-3 w-3 rounded-full bg-yellow-400" />
                  <span className="h-3 w-3 rounded-full bg-green-400" />

                  <div className="ml-4 flex-1 rounded-lg bg-slate-800 px-4 py-2 text-left text-xs text-slate-500">
                    prompt2web.local/preview
                  </div>
                </div>

                {/* Fake website preview */}
                <div className="grid min-h-[380px] md:grid-cols-3">
                  <div className="border-r border-white/10 bg-slate-950 p-5 text-left">
                    <p className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                      AI Workspace
                    </p>

                    <div className="mt-5 space-y-3">
                      <div className="rounded-lg bg-violet-600/20 px-3 py-3 text-sm text-violet-300">
                        Project Files
                      </div>

                      <div className="rounded-lg px-3 py-3 text-sm text-slate-500">
                        src/
                      </div>

                      <div className="rounded-lg px-3 py-3 text-sm text-slate-500">
                        App.jsx
                      </div>

                      <div className="rounded-lg px-3 py-3 text-sm text-slate-500">
                        package.json
                      </div>
                    </div>
                  </div>

                  <div className="col-span-2 bg-white p-8 text-left text-slate-900">
                    <div className="mx-auto max-w-lg">
                      <div className="text-sm font-semibold text-violet-600">
                        AI Generated Website
                      </div>

                      <h3 className="mt-4 text-3xl font-bold">
                        Build something amazing.
                      </h3>

                      <p className="mt-3 text-slate-500">
                        Your idea transformed into a complete web application.
                      </p>

                      <div className="mt-8 grid grid-cols-2 gap-4">
                        <div className="h-24 rounded-xl bg-violet-100" />
                        <div className="h-24 rounded-xl bg-slate-100" />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* Features */}
        <section className="border-t border-white/10 bg-slate-900/50">
          <div className="mx-auto max-w-7xl px-6 py-24">
            <div className="mx-auto max-w-2xl text-center">
              <p className="text-sm font-semibold uppercase tracking-widest text-violet-400">
                How it works
              </p>

              <h2 className="mt-3 text-3xl font-bold sm:text-4xl">
                From prompt to website
              </h2>

              <p className="mt-4 text-slate-400">
                Everything you need to create and customize an AI-generated
                website.
              </p>
            </div>

            <div className="mt-14 grid gap-6 md:grid-cols-3">
              <Feature
                icon={<Sparkles />}
                title="Describe your idea"
                description="Tell Prompt2Web what kind of website or application you want to build."
              />

              <Feature
                icon={<Code2 />}
                title="AI generates code"
                description="AI creates the project structure and generates the required frontend files."
              />

              <Feature
                icon={<Eye />}
                title="Preview instantly"
                description="Build your project and view the generated website directly inside the workspace."
              />
            </div>
          </div>
        </section>

        {/* Benefits */}
        <section className="border-t border-white/10">
          <div className="mx-auto grid max-w-7xl gap-12 px-6 py-24 lg:grid-cols-2 lg:items-center">
            <div>
              <p className="text-sm font-semibold uppercase tracking-widest text-violet-400">
                Built for developers
              </p>

              <h2 className="mt-3 text-3xl font-bold sm:text-4xl">
                Build faster with an AI development workspace
              </h2>

              <p className="mt-5 leading-7 text-slate-400">
                Prompt2Web combines AI generation, project files, code editing,
                live preview and AI-assisted modifications into one workspace.
              </p>

              <div className="mt-8 space-y-4">
                <Benefit text="AI-powered project generation" />
                <Benefit text="File explorer and code editing" />
                <Benefit text="Live project preview" />
                <Benefit text="AI-assisted code modifications" />
              </div>
            </div>

            <div className="rounded-3xl border border-white/10 bg-gradient-to-br from-violet-600/20 to-fuchsia-600/10 p-8">
              <div className="rounded-2xl border border-white/10 bg-slate-950 p-6">
                <div className="flex items-center gap-3">
                  <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-violet-600/20 text-violet-400">
                    <Zap className="h-5 w-5" />
                  </div>

                  <div>
                    <p className="font-semibold">AI Generation</p>
                    <p className="text-sm text-slate-500">
                      Creating your project...
                    </p>
                  </div>
                </div>

                <div className="mt-6 space-y-4">
                  <ProgressItem text="Analyzing prompt" />
                  <ProgressItem text="Creating project structure" />
                  <ProgressItem text="Generating files" />
                  <ProgressItem text="Preparing preview" />
                </div>
              </div>
            </div>
          </div>
        </section>

        {/* CTA */}
        <section className="border-t border-white/10 bg-gradient-to-b from-slate-900 to-slate-950">
          <div className="mx-auto max-w-4xl px-6 py-24 text-center">
            <Sparkles className="mx-auto h-10 w-10 text-violet-400" />

            <h2 className="mt-6 text-4xl font-bold sm:text-5xl">
              Your next website starts with a prompt.
            </h2>

            <p className="mx-auto mt-5 max-w-xl text-slate-400">
              Start building with Prompt2Web and turn your ideas into working
              web applications.
            </p>

            <Link
              to="/register"
              className="mt-8 inline-flex items-center gap-2 rounded-xl bg-violet-600 px-7 py-4 font-semibold transition hover:bg-violet-500"
            >
              Create Your Account
              <ArrowRight className="h-5 w-5" />
            </Link>
          </div>
        </section>
      </main>

      {/* Footer */}
      <footer className="border-t border-white/10 px-6 py-8">
        <div className="mx-auto flex max-w-7xl flex-col justify-between gap-4 text-sm text-slate-500 sm:flex-row">
          <p>© 2026 Prompt2Web. All rights reserved.</p>
          <p>AI-powered website generation platform</p>
        </div>
      </footer>
    </div>
  );
};

const Feature = ({ icon, title, description }) => {
  return (
    <div className="rounded-2xl border border-white/10 bg-white/[0.03] p-7 transition hover:border-violet-500/30 hover:bg-white/[0.05]">
      <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-violet-600/15 text-violet-400">
        {icon}
      </div>

      <h3 className="mt-5 text-xl font-semibold">{title}</h3>

      <p className="mt-3 leading-7 text-slate-400">{description}</p>
    </div>
  );
};

const Benefit = ({ text }) => {
  return (
    <div className="flex items-center gap-3 text-slate-300">
      <CheckCircle2 className="h-5 w-5 text-violet-400" />
      <span>{text}</span>
    </div>
  );
};

const ProgressItem = ({ text }) => {
  return (
    <div className="flex items-center gap-3 rounded-lg bg-slate-900 px-4 py-3">
      <CheckCircle2 className="h-4 w-4 text-violet-400" />
      <span className="text-sm text-slate-300">{text}</span>
    </div>
  );
};

export default Landing;