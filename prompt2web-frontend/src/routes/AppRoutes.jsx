import { BrowserRouter, Routes, Route } from "react-router-dom";
import ProtectedRoute from "./ProtectedRoute";
import Login from "../pages/auth/Login";
import Register from "../pages/auth/Register";
import Dashboard from "../pages/dashboard/Dashboard";
import Workspace from "../pages/workspace/Workspace";
import Landing from "../pages/Landing";

const AppRoutes = () => {
    return (
        <BrowserRouter>

            <Routes>

                {/* Public routes */}
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />

                {/* Protected routes */}
                <Route element={<ProtectedRoute />}>

                    <Route
                        path="/dashboard"
                        element={<Dashboard />}
                    />

                    <Route
                        path="/workspace/:projectId"
                        element={<Workspace />}
                    />

                </Route>

                {/* Default route */}
                <Route
                    path="*"
                    element={<Landing/>}
                />

            </Routes>

        </BrowserRouter>
    );
};

export default AppRoutes;