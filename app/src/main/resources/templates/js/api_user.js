// user apis
const getMe = () => {
    return request("/api/v1/me", "GET");
};

const getSupportedLanguages = () => {
    return request("/api/v1/user/supported-languages", "GET");
};

const changeLanguage = (lang) => {
    return request("/api/v1/user/change-language", "PUT", { language: lang });
};
