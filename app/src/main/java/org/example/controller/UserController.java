/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.controller;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.example.annoation.APIEndpoint;
import org.example.annoation.Controller;
import org.example.annoation.Endpoint;
import org.example.annoation.authorization.PermitAll;
import org.example.common.HTTPMethod;
import org.example.dto.ResultDTO;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.service.UserService;
import org.example.util.Utility;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
@PermitAll
@Controller
public class UserController extends AbstractController {

    @NonNull private UserService userService;

    @APIEndpoint(method = HTTPMethod.GET, path = "/api/v1/me")
    public ResultDTO<UserDTO> getMe(Request request, Response response) throws SQLException {
        UUID userId = request.attribute("userId");
        User user = userService.getUser(userId);

        UserDTO userDTO = UserMapper.INSTANCE.mapFromEntity(user);
        return success(userDTO);
    }

    @APIEndpoint(method = HTTPMethod.POST, path = "/api/v1/auth/logout")
    public ResultDTO<Void> logout(Request request, Response response) {
        response.removeCookie("/", "refreshToken");
        request.session().removeAttribute("accessToken");
        return success();
    }

    @APIEndpoint(method = HTTPMethod.GET, path = "/api/v1/user/supported-languages")
    public ResultDTO<List<String>> getSupportedLanguages(Request request, Response response) {
        return success(getLocalization().getSupportedLocales());
    }

    @APIEndpoint(method = HTTPMethod.PUT, path = "/api/v1/user/change-language")
    public ResultDTO<Void> changeLanguage(Request request, Response response) {
        Map<String, Object> map = Utility.getInstance().toMap(request.body());
        String language = map.get("language").toString();

        if (!getLocalization().getSupportedLocales().contains(language)) {
            return failure(getLocalization().getString(request, "user.invalidLanguage"));
        }

        // set cookie
        response.cookie("/", "lang", language, (int) TimeUnit.DAYS.toSeconds(28), false, false);
        return success();
    }

    @Endpoint(method = HTTPMethod.GET, path = "/profile")
    public ModelAndView serveProfile(Request request, Response response) {
        return makeView("profile", Pair.of("title", getLocalization().getString(request, "profile")));
    }

}
