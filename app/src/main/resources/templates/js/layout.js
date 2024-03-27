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
