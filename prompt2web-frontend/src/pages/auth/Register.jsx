import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

const Register = () => {

    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        name: "",
        email: "",
        password: "",
    });

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e) => {

        e.preventDefault();

        setError("");
        setLoading(true);

        try {

            await axios.post(
                "http://localhost:8080/api/auth/register",
                formData
            );

            navigate("/login");

        } catch (error) {

            setError(
                error.response?.data?.message ||
                "Registration failed. Please try again."
            );

        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-950 flex items-center justify-center px-4">

            <div className="w-full max-w-md">

                <div className="mb-8 text-center">

                    <h1 className="text-4xl font-bold text-white">
                        Prompt2Web
                    </h1>

                    <p className="mt-2 text-slate-400">
                        Build websites with AI
                    </p>

                </div>

                <div className="rounded-2xl border border-slate-800 bg-slate-900 p-8 shadow-xl">

                    <h2 className="text-2xl font-semibold text-white">
                        Create your account
                    </h2>

                    <p className="mt-2 text-sm text-slate-400">
                        Start building websites with Prompt2Web.
                    </p>

                    {error && (
                        <div className="mt-5 rounded-lg border border-red-800 bg-red-950/40 px-4 py-3 text-sm text-red-400">
                            {error}
                        </div>
                    )}

                    <form
                        onSubmit={handleSubmit}
                        className="mt-6 space-y-5"
                    >

                        <div>

                            <label className="mb-2 block text-sm font-medium text-slate-300">
                                Name
                            </label>

                            <input
                                type="text"
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                placeholder="Your name"
                                required
                                className="w-full rounded-lg border border-slate-700 bg-slate-950 px-4 py-3 text-white outline-none transition focus:border-blue-500"
                            />

                        </div>

                        <div>

                            <label className="mb-2 block text-sm font-medium text-slate-300">
                                Email
                            </label>

                            <input
                                type="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                placeholder="you@example.com"
                                required
                                className="w-full rounded-lg border border-slate-700 bg-slate-950 px-4 py-3 text-white outline-none transition focus:border-blue-500"
                            />

                        </div>

                        <div>

                            <label className="mb-2 block text-sm font-medium text-slate-300">
                                Password
                            </label>

                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                placeholder="••••••••"
                                required
                                className="w-full rounded-lg border border-slate-700 bg-slate-950 px-4 py-3 text-white outline-none transition focus:border-blue-500"
                            />

                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full rounded-lg bg-blue-600 px-4 py-3 font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
                        >
                            {loading ? "Creating account..." : "Create Account"}
                        </button>

                    </form>

                    <p className="mt-6 text-center text-sm text-slate-400">

                        Already have an account?{" "}

                        <Link
                            to="/login"
                            className="font-medium text-blue-400 hover:text-blue-300"
                        >
                            Sign in
                        </Link>

                    </p>

                </div>

            </div>

        </div>
    );
};

export default Register;