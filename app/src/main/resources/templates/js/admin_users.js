// global vars
let table;
let selectedItem;
let makingNew = false;

// fields
let username = $("#username");
let email = $("#email");
let firstName = $("#firstName");
let lastName = $("#lastName");
let newPassword = $("#newPassword");
let confirmNewPassword = $("#confirmNewPassword");
let clazz = $("#clazz");
let status = $("#status");
let roles = $("#roles");

// toggle loading
const toggleLoadingButton = (show) => {
  if (show) {
    $(".ui.green.button i").removeClass("checkmark icon");
    $(".ui.green.button i").addClass("loading spinner icon");
  } else {
    $(".ui.green.button i").removeClass("loading spinner icon");
    $(".ui.green.button i").addClass("checkmark icon");
  }
};

// get selected item or toast
const refreshSelectedItem = () => {
  selectedItem = table.rows({ selected: true }).data()[0];
  if (!selectedItem) {
    $.toast({
      class: "warning",
      message: getString("noItemMessage"),
    });
    return false;
  }

  return true;
};

// delete action
const deleteAction = async () => {
  $.modal(
    "confirm",
    getString("dialogTitle"),
    getString("admin.users.delete.content"),
    async function (choice) {
      if (choice) {
        try {
          let response = await deleteUserById(selectedItem.id);
          if (response.success) {
            table.ajax.reload();
          } else {
            $.toast({
              class: "error",
              message: getString("admin.users.deleteFailed"),
            });
          }
        } catch (e) {
          console.log(e);
        }
      }
    }
  );
};

// edit action
const editAction = async () => {
  let data = {
    username: username.val(),
    email: email.val(),
    first_name: firstName.val(),
    last_name: lastName.val(),
    is_active: status.prop("checked"),
    roles: roles.dropdown("get values"),
  };

  if (newPassword.val()) {
    data.new_password = newPassword.val();
  }

  if (confirmNewPassword.val()) {
    data.confirm_new_password = confirmNewPassword.val();
  }

  data["class"] = clazz.val();

  $("#errors").hide();
  $("#errors .list").empty();

  toggleLoadingButton(true);

  try {
    let response = await editUserById(selectedItem.id, data);
    if (response.success) {
      $(".ui.flyout").flyout("hide");
      table.ajax.reload();
    } else {
      response.errors.forEach((error) => {
        $("#errors .list").append("<li>" + error + "</li>");
      });
      $("#errors").show();
    }
  } catch (e) {
    console.log(e);
  }

  toggleLoadingButton(false);
};

// new action
const newAction = async () => {};

$(document).ready(async function () {
  try {
    const result = await getAllRoles();
    let option = {};
    result.data.forEach(item => {
      const key = Object.keys(item)[0];
      const value = item[key];

      option.value = key;
      option.text = value;
      roles.append($("<option>", option));
    });
    roles.dropdown();
  } catch (error) {
    showToastError(error);
    console.log(error);
  }

  table = $("#users").DataTable({
    serverSide: true,
    ajax: async function (data, callback) {
      try {
        let response = await queryUsers(data);
        if (response.success) {
          callback(response.data);
        }
      } catch (error) {
        console.log(error);
      }
    },
    columns: [
      {
        data: "username",
        name: "username",
        searchable: true,
        orderable: true,
      },
      {
        data: "email",
        name: "email",
        searchable: true,
        orderable: true,
      },
      {
        data: "first_name",
        name: "first_name",
        searchable: true,
        orderable: true,
      },
      {
        data: "last_name",
        name: "last_name",
        searchable: true,
        orderable: true,
      },
      {
        data: "updated_at",
        name: "updated_at",
        searchable: false,
        orderable: true,
      },
      {
        data: "class",
        name: null,
        searchable: false,
        orderable: false,
      },
      {
        data: "is_active",
        name: null,
        searchable: false,
        orderable: false,
        render: function (data) {
          return data
            ? getString("status.active")
            : getString("status.inactive");
        },
      },
      {
        data: "roles",
        name: null,
        searchable: false,
        orderable: false,
        render: function (data) {
          // seperated by comma and convert ROLE_ADMIN to Admin and ROLE_USER to User
          let roles = [];
          for (let i = 0; i < data.length; i++) {
            if (data[i] === "admin") {
              roles.push(getString("admin.users.roles.admin"));
            } else if (data[i] === "user") {
              roles.push(getString("admin.users.roles.user"));
            } else if (data[i] === "provider") {
              roles.push(getString("admin.users.roles.provider"));
            } else if (data[i] === "customer") {
              roles.push(getString("admin.users.roles.customer"));
            }
          }
          return roles.join(", ");
        },
      },
    ],
    select: {
      style: "single",
    },
    responsive: true,
    paging: true,
    processing: true,
    pagingType: "full_numbers",
    stateSave: true,
  });

  new $.fn.dataTable.Buttons(table, {
    buttons: [
      // {
      //     text: getString("add"),
      //     action: function () {
      //         // clear current data
      //         username.val('');
      //         password.val('');
      //         clazz.val(0);
      //         status.prop("checked", false);
      //
      //         // show form
      //         $(".ui.flyout").flyout("show");
      //
      //         makingNew = true;
      //     }
      // },
      {
        text: getString("delete"),
        action: async function () {
          if (refreshSelectedItem()) {
            await deleteAction();
          }
        },
      },
      {
        text: getString("edit"),
        action: function () {
          if (refreshSelectedItem()) {
            // set current data
            username.val(selectedItem.username);
            email.val(selectedItem.email);
            firstName.val(selectedItem.first_name);
            lastName.val(selectedItem.last_name);
            clazz.val(selectedItem.class);
            newPassword.val("");
            confirmNewPassword.val("");

            status.prop("checked", selectedItem.is_active);

            roles.dropdown("clear");
            roles.dropdown("set selected", selectedItem.roles);

            // show form
            $(".ui.flyout").flyout("show");
          }
        },
      },
      "copy",
      "csv",
      "excel",
    ],
  });

  table.buttons().container().appendTo($("#buttons"));

  $(".ui.green.button").click(async function () {
    if (makingNew) {
      await newAction();
    } else {
      await editAction();
    }
  });

  $(".ui.red.button").click(function () {
    $(".ui.flyout").flyout("hide");
  });
});
