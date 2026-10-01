import { createContext, useContext, useState } from "react";
import authService from "../services/authService";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {

    const [user, setUser] = useState(null);
    const [token, setToken] = useState(
        localStorage.getItem("token")
    );

    const login = async (loginData) => {

        const response = await authService.login(loginData);

        const receivedToken = response.token;

        localStorage.setItem("token", receivedToken);

        setToken(receivedToken);

        if (response.user) {
            setUser(response.user);
        }

        return response;
    };

    const logout = () => {

        localStorage.removeItem("token");

        setToken(null);
        setUser(null);
    };

    const isAuthenticated = !!token;

    return (
        <AuthContext.Provider
            value={{
                user,
                token,
                isAuthenticated,
                login,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};