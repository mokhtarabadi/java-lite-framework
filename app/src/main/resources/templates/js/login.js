// submit login
$("form").on("submit", async function (event) {
  event.preventDefault();

  $("#errors").hide();
  $("#errors .list").empty();
  $("#button .loading").show();

  const username = $("#username").val();
  const password = $("#password").val();
  const rememberMe = $("#rememberMe").is(":checked");

  try {
    const result = await login(username, password, rememberMe);
    $("#button .loading").hide();

    if (result.success) {
      $("#success").toast({
        displayTime: 1000,
      });
      setTimeout(function () {
        window.location.href = "/";
      }, 1000);
    } else {
      result.errors.forEach((error) => {
        $("#errors .list").append("<li>" + error + "</li>");
      });
      $("#errors").show();
    }
  } catch (error) {
    console.log(`failed to login: ${error}`);
  }
});
