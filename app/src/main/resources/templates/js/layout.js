// close message
$(".message .close.icon").on("click", function () {
  $(this).closest(".message").transition("fade");
});

const showToastError = (message) => {
  $.toast({
    class: "error",
    title: "Error",
    message: message,
    showProgress: "bottom",
  });
};

const showToastSuccess = (message) => {
  $.toast({
    class: "success",
    title: "Success",
    message: message,
    showProgress: "bottom",
  });
};

const redirectToLogin = () => {
  window.location.href = "/login?relogin=true";
};

const request = (url, method, data) => {
  return new Promise((resolve, reject) => {
    $.ajax(url, {
      method: method,
      data: JSON.stringify(data),
      dataType: "json",
      contentType: "application/json",
      success: (result) => {
        resolve(result);
      },
      error: async (xhr, status, error) => {
        // check if server returns a response
        if (!xhr.responseJSON) {
          showToastError(getFormattedString("api.error", status));
        } else {
          xhr.responseJSON.errors.forEach((error) => {
            showToastError(error);
          });

          // check if server returns a 401
          if (
            xhr.status === 401 &&
            xhr.responseJSON.data.reason === "login_required"
          ) {
            redirectToLogin();
          }
        }
        reject(error);
      },
    });
  });
};

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

const getMe = () => {
  return request("/api/v1/me", "GET");
};

const changeLanguage = (lang) => {
  return request("/api/v1/user/change-language", "PUT", { language: lang });
};

const getAllLogs = () => {
  return request("/api/v1/logs", "GET");
};

const getAllLogsByType = (type) => {
  return request(`/api/v1/logs?type=${type}`, "GET");
};

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

const deleteLogById = (id) => {
  return request("/api/v1/admin/logs", "DELETE", { id });
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
