// global vars
let table;
let selectedItem;

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
    getString("admin.queryLogs.delete.content"),
    async function (choice) {
      if (choice) {
        try {
          let response = await deleteLogById(selectedItem.id);
          if (response.success) {
            table.ajax.reload();
          } else {
            $.toast({
              class: "error",
              message: getString("admin.queryLogs.deleteFailed"),
            });
          }
        } catch (e) {
          console.log(e);
        }
      }
    }
  );
};

$(document).ready(function () {
  table = $("#systemLogs").DataTable({
    serverSide: true,
    ajax: async function (data, callback) {
      try {
        let response = await getSystemLogsByTypesForDataTables(data, [
          "new_user",
          "login",
          "invalid_login",
          "role_changed",
        ]);
        //console.log(response);
        if (response.success) {
          callback(response.data);
        }
      } catch (error) {
        console.log(error);
      }
    },
    columns: [
      {
        data: "type",
        name: "type",
        searchable: false,
        orderable: true,
        render: function (data) {
          switch (data) {
            case "new_user":
              return getString("admin.systemLogs.type.new_user");
            case "login":
              return getString("admin.systemLogs.type.login");
            case "invalid_login":
              return getString("admin.systemLogs.type.invalid_login");
            case "role_changed":
              return getString("admin.systemLogs.type.role_changed");
            default:
              throw new Error("Unknown type: " + data);
          }
        },
      },
      {
        data: "created_at",
        name: "created_at",
        searchable: false,
        orderable: true,
      },
      {
        data: "data",
        name: "data",
        searchable: true,
        orderable: false,
        render: function (data) {
          return JSON.stringify(data);
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
      {
        text: getString("delete"),
        action: async function () {
          if (refreshSelectedItem()) {
            await deleteAction();
          }
        },
      },
      "copy",
      "csv",
      "excel",
    ],
  });

  table.buttons().container().appendTo($("#buttons"));
});
