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
      window.location.reload();
    }
  } catch (error) {
    console.log(error);
  }
}

$("#settings").on("click", async function () {});
