
// admin apis
const queryUsers = (params) => {
    return request("/api/v1/admin/users", "POST", params);
};

const deleteUserById = (id) => {
    return request("/api/v1/admin/users", "DELETE", { id });
};

const editUserById = (id, params) => {
    return request(`/api/v1/admin/users/${id}`, "PUT", params);
};

const getSystemLogsByTypesForDataTables = (params, types) => {
    // types is an array of string
    // join it by comma
    return request(
        `/api/v1/admin/logs/system?types=${types.join(",")}`,
        "POST",
        params
    );
};

const deleteLogById = (id) => {
    return request("/api/v1/admin/logs", "DELETE", { id });
};

const getAllRoles = () => {
    return request("/api/v1/admin/roles", "GET");
};
