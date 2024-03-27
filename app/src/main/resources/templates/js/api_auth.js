// auth apis
const signup = (
    username,
    email,
    firstName,
    lastName,
    password,
    confirmPassword
) => {
    return request("/api/v1/auth/signup", "POST", {
        username,
        email,
        first_name: firstName,
        last_name: lastName,
        password,
        confirm_password: confirmPassword,
    });
};

const login = (username, password, rememberMe) => {
    return request("/api/v1/auth/login", "POST", {
        username,
        password,
        remember_me: rememberMe,
    });
};

const logout = () => {
    return request("/api/v1/auth/logout", "POST");
};