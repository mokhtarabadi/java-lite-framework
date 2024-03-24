/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.sql.SQLException;
import java.util.*;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.example.annoation.APIEndpoint;
import org.example.annoation.Controller;
import org.example.annoation.Endpoint;
import org.example.annoation.authorization.RolesAllowed;
import org.example.common.AuthorizationRole;
import org.example.common.CustomValidator;
import org.example.common.HTTPMethod;
import org.example.config.AppConfig;
import org.example.dto.*;
import org.example.entity.Log;
import org.example.service.*;
import org.example.state.UserState;
import org.example.util.Utility;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@RolesAllowed(AuthorizationRole.ROLE_ADMIN)
@Controller
public class AdminController extends AbstractController {

    @NonNull private CustomValidator validator;

    @NonNull private Gson gson;

    @NonNull private AppConfig appConfig;

    @NonNull private UserService userService;

    @NonNull private LogService logService;

    @Endpoint(method = HTTPMethod.GET, path = "/admin/users")
    public ModelAndView serveUsers(Request request, Response response) {
        return makeView("admin_users", Pair.of("title", getLocalization().getString(request, "admin.users.title")));
    }

    @APIEndpoint(method = HTTPMethod.POST, path = "/api/v1/admin/users")
    public ResultDTO<DataTableDTO<UserDTO>> fetchUsers(Request request, Response response) throws SQLException {
        // log.debug("get users body: {}", request.body());

        DataTableRequestDTO dataTableRequestDTO = gson.fromJson(request.body(), DataTableRequestDTO.class);
        DataTableDTO<UserDTO> dataTableDTO = userService.fetchUsersForDataTable(dataTableRequestDTO);

        return success(dataTableDTO);
    }

    @APIEndpoint(method = HTTPMethod.DELETE, path = "/api/v1/admin/users")
    public ResultDTO<Void> deleteUserById(Request request, Response response) throws SQLException {
        JsonObject map = gson.fromJson(request.body(), JsonObject.class);
        UUID id = UUID.fromString(map.get("id").getAsString());

        UserState userState = userService.deleteUser(id);
        if (userState.getState() == UserState.State.SUCCESS) {
            return success();
        }
        return failure();
    }

    @APIEndpoint(method = HTTPMethod.PUT, path = "/api/v1/admin/users/:id")
    public ResultDTO<UUID> editUserById(Request request, Response response) throws SQLException {
        UUID id = UUID.fromString(request.params(":id"));
        UpdateUserDTO updateUserDTO = gson.fromJson(request.body(), UpdateUserDTO.class);

        // validate
        List<String> errors = validator.validate(request, updateUserDTO);
        if (!errors.isEmpty()) {
            return failure(errors);
        }

        UserState userState = userService.updateUser(id, updateUserDTO);
        switch (userState.getState()) {
            case EMAIL_TAKEN:
                return failure(getLocalization().getString(request, "signup.email.taken"));
            case USERNAME_TAKEN:
                return failure(getLocalization().getString(request, "signup.username.taken"));
            case INVALID_ROLE:
                return failure(getLocalization().getString(request, "admin.users.roles.invalid"));
            case SUCCESS:
                return success(userState.getUuid());
            default:
                throw new IllegalArgumentException();
        }
    }

    @Endpoint(method = HTTPMethod.GET, path = "/admin/logs/system")
    public ModelAndView serverSystemLogs(Request request, Response response) {
        return makeView(
                "admin_system_logs", Pair.of("title", getLocalization().getString(request, "admin.systemLogs.title")));
    }

    @APIEndpoint(method = HTTPMethod.POST, path = "/api/v1/admin/logs/system")
    public ResultDTO<DataTableDTO<LogDTO>> fetchSystemLogsForDataTable(Request request, Response response)
            throws SQLException {
        // get types from url params as then split by comma
        String typesParam = request.queryParams("types");
        Log.Type[] types =
                Arrays.stream(typesParam.split(",")).map(Log.Type::fromValue).toArray(Log.Type[]::new);
        DataTableRequestDTO dataTableRequestDTO = gson.fromJson(request.body(), DataTableRequestDTO.class);

        return success(logService.fetchLogsForDataTable(dataTableRequestDTO, types));
    }

    @APIEndpoint(method = HTTPMethod.DELETE, path = "/api/v1/admin/logs")
    public ResultDTO<Void> deleteLogById(Request request, Response response) throws SQLException {
        Map<String, Object> map = Utility.getInstance().toMap(request.body());
        UUID id = UUID.fromString(map.get("id").toString());

        logService.deleteLog(id);

        return success();
    }
}
