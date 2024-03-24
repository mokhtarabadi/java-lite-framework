$(document).ready(function () {
  $(".ui.dropdown").dropdown();
  $(".sidebar-menu-toggler").on("click", function () {
    var target = $(this).data("target");
    $(target)
      .sidebar({
        dinPage: true,
        transition: "overlay",
        mobileTransition: "overlay",
      })
      .sidebar("toggle");
  });

  $("#languages button").on("click", async function (event) {
    event.preventDefault();
    let lang = $(this).data("lang");
    await setLang(lang);
  });
});

$("#logout").on("click", async function () {
  try {
    let result = await logout();
    if (result.success) {
      window.location.href = "/login?logout";
    }
  } catch (error) {
    console.log(error);
  }
});

async function setLang(lang) {
  try {
    let result = await changeLanguage(lang);
    if (result.success) {
      localStorage.setItem("lang", lang);

      // show a confirmation to refresh page
      $.modal(
        "confirm",
        getString("changeLanguage.dialogTitle"),
        getString("changeLanguage.dialogContent"),
        function (choice) {
          if (choice) {
            $.toast({
              class: "success",
              message: getString("changeLanguage.toastSuccess"),
            });
            setTimeout(function () {
              window.location.reload();
            }, 1000);
          }
        }
      );
    }
  } catch (error) {
    console.log(error);
  }
}

$("#settings").on("click", async function () {
  try {
    let result = await getSupportedLanguages();
    if (result.success) {
      console.log(result.data);
    }
  } catch (error) {
    console.log(error);
  }
});
