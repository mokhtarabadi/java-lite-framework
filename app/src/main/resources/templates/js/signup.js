// submit signup
$("form").on("submit", async function (event) {
  event.preventDefault();

  $("#errors").hide();
  $("#errors .list").empty();
  $("#button .loading").show();

  const username = $("#username").val();
  const email = $("#email").val();
  const firstName = $("#firstName").val();
  const lastName = $("#lastName").val();
  const password = $("#password").val();
  const confirmPassword = $("#confirmPassword").val();

  try {
    let result = await signup(
      username,
      email,
      firstName,
      lastName,
      password,
      confirmPassword
    );
    $("#button .loading").hide();

    if (result.success) {
      window.location.href = "/login?signup&username=" + username;
    } else {
      if (result.errors) {
        result.errors.forEach(function (error) {
          $("#errors .list").append("<li>" + error + "</li>");
        });
        $("#errors").show();
      }
    }
  } catch (error) {
    console.log(`failed to signup: ${error}`);
  }
});
