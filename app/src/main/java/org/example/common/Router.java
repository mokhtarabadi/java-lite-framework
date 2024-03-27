/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.common;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.example.annoation.*;
import org.example.annoation.Endpoint;
import org.example.annoation.authorization.AnonymousAllowed;
import org.example.annoation.authorization.DenyAll;
import org.example.annoation.authorization.PermitAll;
import org.example.annoation.authorization.RolesAllowed;
import org.example.config.AppConfig;
import org.example.controller.*;
import org.example.dto.ResultDTO;
import org.example.entity.User;
import org.example.exception.AccessDenied;
import org.example.exception.AccountDisabled;
import org.example.exception.LoginRequired;
import org.example.filter.AuthenticationFilter;
import org.example.filter.authorization.AnonymousAllowedFilter;
import org.example.filter.authorization.DenyAllFilter;
import org.example.filter.authorization.PermitAllFilter;
import org.example.filter.authorization.RolesAllowedFilter;
import org.example.service.UserService;
import spark.*;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class Router {

    // things needed for rendering
    @NonNull private GsonResponseTransformer gsonResponseTransformer;

    @NonNull private TemplateEngine templateEngine;

    @NonNull private Localization localization;

    @NonNull private Gson gson;

    @NonNull private AppConfig appConfig;

    // needed services
    @NonNull private UserService userService;

    // authentication filter
    @NonNull private AuthenticationFilter authenticationFilter;

    // Authorization filters
    @NonNull private AnonymousAllowedFilter anonymousAllowedFilter;

    @NonNull private DenyAllFilter denyAllFilter;

    @NonNull private PermitAllFilter permitAllFilter;

    // controllers
    @NonNull private AuthController authController;

    @NonNull private UserController userController;

    @NonNull private AdminController adminController;

    @NonNull private DashboardController dashboardController;

    @NonNull private CustomerController customerController;

    @NonNull private ProviderController providerController;

    private Map<Class<?>, Object> getControllers() {
        return Arrays.stream(new Object[] {authController, userController, adminController, dashboardController, customerController, providerController})
                .collect(Collectors.toMap(Object::getClass, o -> o));
    }

    @Getter
    public enum Error {
        @SerializedName("resource_not_found")
        RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND_404, "error.resource_not_found"),

        @SerializedName("internal_server_error")
        INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR_500, "error.internal_server_error"),

        @SerializedName("access_denied")
        ACCESS_DENIED(HttpStatus.FORBIDDEN_403, "error.access_denied"),

        @SerializedName("account_disabled")
        ACCOUNT_DISABLED(HttpStatus.FORBIDDEN_403, "error.account_disabled"),

        @SerializedName("login_required")
        LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED_401, "error.login_required");

        private final int httpCode;
        private final String errorMessage;
        private String locale;

        Error(int httpCode, String errorMessage) {
            this.httpCode = httpCode;
            this.errorMessage = errorMessage;
        }
    }

    public void registerRoutes() {
        List<RouteModel> routeModels = new ArrayList<>();
        Map<Class<?>, Object> controllers = getControllers();

        for (Class<?> controllerClass : controllers.keySet()) {
            // handle roles
            List<Filter> authorizationFilters = new ArrayList<>();
            if (controllerClass.isAnnotationPresent(DenyAll.class)) {
                authorizationFilters.add(denyAllFilter);
            } else if (controllerClass.isAnnotationPresent(PermitAll.class)) {
                authorizationFilters.add(permitAllFilter);
            } else if (controllerClass.isAnnotationPresent(AnonymousAllowed.class)) {
                authorizationFilters.add(anonymousAllowedFilter);
            } else if (controllerClass.isAnnotationPresent(RolesAllowed.class)) {
                RolesAllowed rolesAllowed = controllerClass.getAnnotation(RolesAllowed.class);
                RolesAllowedFilter rolesAllowedFilter =
                        new RolesAllowedFilter(userService, Arrays.asList(rolesAllowed.value()));
                authorizationFilters.add(rolesAllowedFilter);
            }

            Object controllerInstance = controllers.get(controllerClass);
            for (java.lang.reflect.Method declaredMethod : controllerClass.getDeclaredMethods()) {
                String path;
                HTTPMethod HTTPMethod;
                boolean needAuth;
                boolean isRest;

                if (declaredMethod.isAnnotationPresent(Endpoint.class)) {
                    Endpoint routeModel = declaredMethod.getAnnotation(Endpoint.class);
                    path = routeModel.path();
                    HTTPMethod = routeModel.method();
                    needAuth = routeModel.authentication();
                    isRest = false;
                } else if (declaredMethod.isAnnotationPresent(APIEndpoint.class)) {
                    APIEndpoint route = declaredMethod.getAnnotation(APIEndpoint.class);
                    path = route.path();
                    HTTPMethod = route.method();
                    needAuth = route.authentication();
                    isRest = true;
                } else continue; // not a route

                if (declaredMethod.isAnnotationPresent(DenyAll.class)) {
                    authorizationFilters.add(denyAllFilter);
                } else if (declaredMethod.isAnnotationPresent(PermitAll.class)) {
                    authorizationFilters.add(permitAllFilter);
                } else if (declaredMethod.isAnnotationPresent(AnonymousAllowed.class)) {
                    authorizationFilters.add(anonymousAllowedFilter);
                } else if (declaredMethod.isAnnotationPresent(RolesAllowed.class)) {
                    RolesAllowed rolesAllowed = declaredMethod.getAnnotation(RolesAllowed.class);
                    RolesAllowedFilter rolesAllowedFilter =
                            new RolesAllowedFilter(userService, Arrays.asList(rolesAllowed.value()));
                    authorizationFilters.add(rolesAllowedFilter);
                }

                RouteModel finalRouteModel = new RouteModel(path, HTTPMethod) {
                    @Override
                    public ModelAndView handle(Request request, Response response)
                            throws SQLException, InvocationTargetException, IllegalAccessException {
                        ModelAndView modelAndView =
                                (ModelAndView) declaredMethod.invoke(controllerInstance, request, response);
                        Map<String, Object> model = (Map<String, Object>) modelAndView.getModel();

                        if (request.attributes().contains("userId")) {
                            UUID id = request.attribute("userId");
                            User user = userService.getUser(id);
                            model.put("user", user);
                        }

                        // put lang
                        String lang = request.cookie("lang");
                        if (lang == null) {
                            lang = appConfig.getDefaultLocale();
                        }
                        model.put("lang", lang);

                        return modelAndView;
                    }

                    @Override
                    public <T> ResultDTO<T> handleAPI(Request request, Response response)
                            throws SQLException, LoginRequired, InvocationTargetException, IllegalAccessException {
                        return (ResultDTO<T>) declaredMethod.invoke(controllerInstance, request, response);
                    }
                };
                finalRouteModel.setRequireAuthentication(needAuth);
                finalRouteModel.setAPI(isRest);
                finalRouteModel.setRoleFilters(authorizationFilters);

                routeModels.add(finalRouteModel);
            }
        }

        // start session
        Spark.before(((request, response) -> request.session(true)));

        routeModels.forEach(routeModel -> Spark.path(routeModel.getPath(), () -> {
            // TODO: 12/30/23 check if need write a filter to trim params and queryParams

            // handle authentication
            if (routeModel.isRequireAuthentication()) {
                log.trace("Require authentication: {}", routeModel.getPath());
                Spark.before("", authenticationFilter);
            }

            // add default denyAll if no one defined
            if (routeModel.getRoleFilters().isEmpty()) {
                routeModel.getRoleFilters().add(denyAllFilter);
            }

            // handle authorization
            for (Filter roleFilter : routeModel.getRoleFilters()) {
                log.trace(
                        "Add authorization {} filter to {} path",
                        roleFilter.getClass().getSimpleName(),
                        routeModel.getPath());
                Spark.before("", roleFilter);
            }

            // handle requests
            if (routeModel.isAPI()) {
                handleAPIRoutes(routeModel);
            } else {
                handleRoutes(routeModel);
            }

            if (routeModel.isAPI()) {
                Spark.after("", (request, response) -> response.type("application/json"));
            } else {
                Spark.after("", (request, response) -> response.type("text/html"));
            }
        }));

        handleExceptions();

        // error 404
        Spark.notFound((request, response) -> {
            log.debug("not found: {}", request.pathInfo());

            Error error = Error.RESOURCE_NOT_FOUND;
            error.locale = request.cookie("lang");
            if (isRequestJson(request)) {
                response.type("application/json");
                return generateJsonError(error);
            } else {
                return serveError(error);
            }
        });

        // error 500
        Spark.internalServerError((request, response) -> {
            Error error = Error.INTERNAL_SERVER_ERROR;
            error.locale = request.cookie("lang");
            if (isRequestJson(request)) {
                response.type("application/json");
                return generateJsonError(error);
            } else {
                return serveError(error);
            }
        });
    }

    private void handleExceptions() {
        // login required 401
        Spark.exception(LoginRequired.class, (exception, request, response) -> {
            log.debug("wow! authentication needed");

            Error error = Error.LOGIN_REQUIRED;
            error.locale = request.cookie("lang");
            if (isRequestJson(request)) {
                response.type("application/json");
                response.status(error.getHttpCode());
                response.body(generateJsonError(error));
            } else {
                response.redirect("/login?relogin=true");
            }
        });

        // access denied 403
        Spark.exception(AccessDenied.class, (exception, request, response) -> {
            log.debug("you are not welcome here.");

            Error error = Error.ACCESS_DENIED;
            error.locale = request.cookie("lang");
            response.status(error.getHttpCode());
            if (isRequestJson(request)) {
                response.type("application/json");
                response.body(generateJsonError(error));
            } else {
                response.body(serveError(error));
            }
        });

        // account disabled 403
        Spark.exception(AccountDisabled.class, (exception, request, response) -> {
            log.debug("your account is disabled.");

            Error error = Error.ACCOUNT_DISABLED;
            error.locale = request.cookie("lang");
            response.status(error.getHttpCode());
            if (isRequestJson(request)) {
                response.type("application/json");
                response.body(generateJsonError(error));
            } else {
                response.body(serveError(error));
            }
        });

        // sql exception
        Spark.exception(SQLException.class, (exception, request, response) -> {
            log.error("sql exception", exception);

            Error error = Error.INTERNAL_SERVER_ERROR;
            error.locale = request.cookie("lang");
            response.status(error.getHttpCode());
            if (isRequestJson(request)) {
                response.type("application/json");
                response.body(generateJsonError(error));
            } else {
                response.body(serveError(error));
            }
        });

        // can't parse json
        Spark.exception(JsonParseException.class, (exception, request, response) -> {
            log.error("json parse exception", exception);

            if (isRequestJson(request)) {
                response.status(HttpStatus.BAD_REQUEST_400);
                response.body(gson.toJson(AbstractController.failure(
                        Collections.singletonList("payload is not valid json: " + exception.getMessage()))));
            } else {
                log.debug("must never be here! if it is, it's a bug :D");
            }
        });
    }

    private boolean isRequestJson(Request request) {
        String contentType = request.contentType();
        return StringUtils.isNotEmpty(contentType) && contentType.startsWith("application/json");
    }

    private String generateJsonError(Error error) {
        String errorMessage = localization.getString(error.getLocale(), error.getErrorMessage());
        ResultDTO<Map<String, Object>> resultDTO = AbstractController.failure();
        resultDTO.setData(Map.of("reason", error));
        resultDTO.setErrors(Collections.singletonList(errorMessage));
        return gson.toJson(resultDTO);
    }

    private String serveError(Error error) {
        Map<String, Object> map = new HashMap<>();
        String title = localization.getString(error.getLocale(), "error" + error.getHttpCode() + ".title");
        String message = localization.getString(error.getLocale(), error.getErrorMessage());

        map.put("title", title);
        map.put("errorTitle", title);
        map.put("errorDescription", message);
        map.put("go.home", localization.getString(error.getLocale(), "error.go.home"));

        return templateEngine.render(new ModelAndView(map, "error"));
    }

    private void handleAPIRoutes(RouteModel routeModel) {
        switch (routeModel.getMethod()) {
            case GET:
                Spark.get("", "application/json", routeModel::handleAPI, gsonResponseTransformer);
                break;
            case POST:
                Spark.post("", "application/json", routeModel::handleAPI, gsonResponseTransformer);
                break;
            case PUT:
                Spark.put("", "application/json", routeModel::handleAPI, gsonResponseTransformer);
                break;
            case DELETE:
                Spark.delete("", "application/json", routeModel::handleAPI, gsonResponseTransformer);
                break;
            case OPTIONS:
                Spark.options("", "application/json", routeModel::handleAPI, gsonResponseTransformer);
                break;
        }
    }

    private void handleRoutes(RouteModel routeModel) {
        switch (routeModel.getMethod()) {
            case GET:
                Spark.get("", routeModel::handle, templateEngine);
                break;
            case POST:
                Spark.post("", routeModel::handle, templateEngine);
                break;
            case PUT:
                Spark.put("", routeModel::handle, templateEngine);
                break;
            case DELETE:
                Spark.delete("", routeModel::handle, templateEngine);
                break;
            case OPTIONS:
                Spark.options("", routeModel::handle, templateEngine);
                break;
        }
    }

    @Data
    private abstract static class RouteModel {

        @NonNull private String path;

        @NonNull private HTTPMethod method;

        private boolean requireAuthentication;
        private List<Filter> roleFilters;
        private boolean isAPI;

        public abstract ModelAndView handle(Request request, Response response)
                throws SQLException, InvocationTargetException, IllegalAccessException;

        public abstract <T> ResultDTO<T> handleAPI(Request request, Response response)
                throws SQLException, LoginRequired, InvocationTargetException, IllegalAccessException;
    }
}
