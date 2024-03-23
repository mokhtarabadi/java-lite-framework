/* (C) 2023 */
package org.example.controller;

import com.google.gson.Gson;
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
import org.example.annoation.authorization.AnonymousAllowed;
import org.example.common.CustomValidator;
import org.example.common.HTTPMethod;
import org.example.dto.LoginDTO;
import org.example.dto.ResultDTO;
import org.example.dto.UserDTO;
import org.example.service.UserService;
import org.example.state.LoginState;
import org.example.state.UserState;
import org.example.util.JwtUtil;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Controller
@AnonymousAllowed
public class AuthController extends AbstractController {

    @NonNull private Gson gson;

    @NonNull private CustomValidator validator;

    @NonNull private UserService userService;

    @Endpoint(method = HTTPMethod.GET, path = "/signup", authentication = false)
    public ModelAndView serveSignup(Request request, Response response) {
        return makeView("signup", Pair.of("title", getLocalization().getString(request, "signup")));
    }

    @APIEndpoint(method = HTTPMethod.POST, path = "/api/v1/auth/signup", authentication = false)
    public ResultDTO<UUID> signup(Request request, Response response) throws SQLException {
        UserDTO userDTO = gson.fromJson(request.body(), UserDTO.class);

        // validate
        List<String> errors = validator.validate(request, userDTO);
        if (!errors.isEmpty()) {
            return failure(errors);
        }

        UserState userState = userService.addNewUser(userDTO);
        switch (userState.getState()) {
            case EMAIL_TAKEN:
                return failure(getLocalization().getString(request, "signup.email.taken"));
            case USERNAME_TAKEN:
                return failure(getLocalization().getString(request, "signup.username.taken"));
            case SUCCESS:
                return success(userState.getUuid());
            default:
                throw new IllegalArgumentException();
        }
    }

    @Endpoint(method = HTTPMethod.GET, path = "/login", authentication = false)
    public ModelAndView serveLogin(Request request, Response response) {
        boolean signup = request.queryParams("signup") != null;
        boolean logout = request.queryParams("logout") != null;
        boolean relogin = request.queryParams("relogin") != null;

        String username = request.queryParams("username");

        return makeView(
                "login",
                Pair.of("title", getLocalization().getString(request, "login")),
                Pair.of("signup", signup),
                Pair.of("logout", logout),
                Pair.of("relogin", relogin),
                Pair.of("username", username));
    }

    @APIEndpoint(method = HTTPMethod.POST, path = "/api/v1/auth/login", authentication = false)
    public ResultDTO<Void> login(Request request, Response response) throws SQLException {
        LoginDTO loginDTO = gson.fromJson(request.body(), LoginDTO.class);

        // validate
        List<String> errors = validator.validate(request, loginDTO);
        if (!errors.isEmpty()) {
            return failure(errors);
        }

        LoginState loginState = userService.validateUserCredentials(loginDTO);
        if (loginState.getState() == LoginState.State.INVALID_CREDENTIALS) {
            return failure(getLocalization().getString(request, "login.invalidCredentials"));
        } else if (loginState.getState() == LoginState.State.USER_NOT_FOUND) {
            return failure(getLocalization().getString(request, "login.userNotFound"));
        }

        String accessToken = JwtUtil.createAccessToken(loginState.getUuid().toString());
        request.session().attribute("accessToken", accessToken);

        String refreshToken = JwtUtil.createRefreshToken(loginState.getUuid().toString());
        if (loginDTO.isRememberMe()) {
            log.debug("cookie expire time is {} seconds", JwtUtil.REFRESH_TOKEN_EXPIRATION_SECONDS);
            response.cookie("/", "refreshToken", refreshToken, JwtUtil.REFRESH_TOKEN_EXPIRATION_SECONDS, false, true);
        } else {
            log.debug("cookie expire time is -1");
            response.cookie("/", "refreshToken", refreshToken, -1, false, true);
        }

        // return success
        return success();
    }
}
