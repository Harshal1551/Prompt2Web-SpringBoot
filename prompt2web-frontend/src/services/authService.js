import api from "./api";

const register = async (userData) => {
    const response = await api.post(
        "/api/auth/register",
        userData
    );

    return response.data;
};

const login = async (loginData) => {
    const response = await api.post(
        "/api/auth/login",
        loginData
    );

    return response.data;
};

const authService = {
    register,
    login,
};

export default authService;