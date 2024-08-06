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
import java.sql.SQLException;
import java.util.*;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.example.annoation.*;
import org.example.annoation.authorization.*;
import org.example.config.AppConfig;
import org.example.controller.*;
import org.example.dto.ResultDTO;
import org.example.entity.User;
import org.example.exception.*;
import org.example.filter.AuthenticationFilter;
import org.example.filter.authorization.*;
import org.example.service.UserService;
import spark.*;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class Router {

    @NonNull private GsonResponseTransformer gsonResponseTransformer;

    @NonNull private TemplateEngine templateEngine;

    @NonNull private Localization localization;

    @NonNull private Gson gson;

    @NonNull private AppConfig appConfig;

    @NonNull private UserService userService;

    @NonNull private AuthenticationFilter authenticationFilter;

    @NonNull private AnonymousAllowedFilter anonymousAllowedFilter;

    @NonNull private DenyAllFilter denyAllFilter;

    @NonNull private PermitAllFilter permitAllFilter;

    @NonNull @Named("controllers")
    private Map<Class<?>, Object> controllers;

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
        for (Map.Entry<Class<?>, Object> entry : controllers.entrySet()) {
            Class<?> controllerClass = entry.getKey();
            Object controllerInstance = entry.getValue();
            routeModels.addAll(createRouteModels(controllerClass, controllerInstance));
        }

        Spark.before(((request, response) -> request.session(true)));

        routeModels.forEach(this::configureRoute);

        handleExceptions();

        Spark.notFound(this::handleNotFound);
        Spark.internalServerError(this::handleInternalServerError);
    }

    private List<RouteModel> createRouteModels(Class<?> controllerClass, Object controllerInstance) {
        List<RouteModel> routeModels = new ArrayList<>();
        List<Filter> authorizationFilters = getAuthorizationFilters(controllerClass);

        for (java.lang.reflect.Method method : controllerClass.getDeclaredMethods()) {
            RouteModel routeModel = createRouteModel(method, controllerInstance, authorizationFilters);
            if (routeModel != null) {
                routeModels.add(routeModel);
            }
        }

        return routeModels;
    }

    private RouteModel createRouteModel(
            java.lang.reflect.Method method, Object controllerInstance, List<Filter> authorizationFilters) {
        String path;
        HTTPMethod httpMethod;
        boolean needAuth;
        boolean isRest;

        if (method.isAnnotationPresent(Endpoint.class)) {
            Endpoint endpoint = method.getAnnotation(Endpoint.class);
            path = endpoint.path();
            httpMethod = endpoint.method();
            needAuth = endpoint.authentication();
            isRest = false;
        } else if (method.isAnnotationPresent(APIEndpoint.class)) {
            APIEndpoint apiEndpoint = method.getAnnotation(APIEndpoint.class);
            path = apiEndpoint.path();
            httpMethod = apiEndpoint.method();
            needAuth = apiEndpoint.authentication();
            isRest = true;
        } else {
            return null;
        }

        authorizationFilters.addAll(getAuthorizationFilters(method));

        return new RouteModel(path, httpMethod) {
            @Override
            public ModelAndView handle(Request request, Response response) throws Exception {
                ModelAndView modelAndView = (ModelAndView) method.invoke(controllerInstance, request, response);
                Map<String, Object> model = (Map<String, Object>) modelAndView.getModel();
                addUserAndLocaleToModel(request, model);
                return modelAndView;
            }

            @Override
            public <T> ResultDTO<T> handleAPI(Request request, Response response) throws Exception {
                return (ResultDTO<T>) method.invoke(controllerInstance, request, response);
            }
        }.setRequireAuthentication(needAuth).setAPI(isRest).setRoleFilters(authorizationFilters);
    }

    private void addUserAndLocaleToModel(Request request, Map<String, Object> model) throws SQLException {
        if (request.attributes().contains("userId")) {
            UUID id = request.attribute("userId");
            User user = userService.getUser(id);
            model.put("user", user);
        }
        String lang = request.cookie("lang");
        if (lang == null) {
            lang = appConfig.getDefaultLocale();
        }
        model.put("lang", lang);
    }

    private List<Filter> getAuthorizationFilters(java.lang.reflect.AnnotatedElement element) {
        List<Filter> filters = new ArrayList<>();
        if (element.isAnnotationPresent(DenyAll.class)) {
            filters.add(denyAllFilter);
        } else if (element.isAnnotationPresent(PermitAll.class)) {
            filters.add(permitAllFilter);
        } else if (element.isAnnotationPresent(AnonymousAllowed.class)) {
            filters.add(anonymousAllowedFilter);
        } else if (element.isAnnotationPresent(RolesAllowed.class)) {
            RolesAllowed rolesAllowed = element.getAnnotation(RolesAllowed.class);
            filters.add(new RolesAllowedFilter(userService, Arrays.asList(rolesAllowed.value())));
        }
        return filters;
    }

    private void configureRoute(RouteModel routeModel) {
        Spark.path(routeModel.getPath(), () -> {
            if (routeModel.isRequireAuthentication()) {
                log.trace("Require authentication: {}", routeModel.getPath());
                Spark.before("", authenticationFilter);
            }

            if (routeModel.getRoleFilters().isEmpty()) {
                routeModel.getRoleFilters().add(denyAllFilter);
            }

            routeModel.getRoleFilters().forEach(filter -> {
                log.trace(
                        "Add authorization {} filter to {} path",
                        filter.getClass().getSimpleName(),
                        routeModel.getPath());
                Spark.before("", filter);
            });

            if (routeModel.isAPI()) {
                handleAPIRoutes(routeModel);
            } else {
                handleRoutes(routeModel);
            }

            Spark.after(
                    "", (request, response) -> response.type(routeModel.isAPI() ? "application/json" : "text/html"));
        });
    }

    private void handleExceptions() {
        Spark.exception(LoginRequired.class, this::handleLoginRequired);
        Spark.exception(AccessDenied.class, this::handleAccessDenied);
        Spark.exception(AccountDisabled.class, this::handleAccountDisabled);
        Spark.exception(SQLException.class, this::handleSQLException);
        Spark.exception(JsonParseException.class, this::handleJsonParseException);
    }

    private void handleLoginRequired(Exception exception, Request request, Response response) {
        log.debug("wow! authentication needed");
        handleError(
                response, Error.LOGIN_REQUIRED, request.cookie("lang"), isRequestJson(request), "/login?relogin=true");
    }

    private void handleAccessDenied(Exception exception, Request request, Response response) {
        log.debug("you are not welcome here.");
        handleError(response, Error.ACCESS_DENIED, request.cookie("lang"), isRequestJson(request), null);
    }

    private void handleAccountDisabled(Exception exception, Request request, Response response) {
        log.debug("your account is disabled.");
        handleError(response, Error.ACCOUNT_DISABLED, request.cookie("lang"), isRequestJson(request), null);
    }

    private void handleSQLException(Exception exception, Request request, Response response) {
        log.error("sql exception", exception);
        handleError(response, Error.INTERNAL_SERVER_ERROR, request.cookie("lang"), isRequestJson(request), null);
    }

    private void handleJsonParseException(Exception exception, Request request, Response response) {
        log.error("json parse exception", exception);
        if (isRequestJson(request)) {
            response.status(HttpStatus.BAD_REQUEST_400);
            response.body(gson.toJson(AbstractController.failure(
                    Collections.singletonList("payload is not valid json: " + exception.getMessage()))));
        } else {
            log.debug("must never be here! if it is, it's a bug :D");
        }
    }

    private void handleError(Response response, Error error, String locale, boolean isJson, String redirectUrl) {
        error.locale = locale;
        response.status(error.getHttpCode());
        if (isJson) {
            response.type("application/json");
            response.body(generateJsonError(error));
        } else if (redirectUrl != null) {
            response.redirect(redirectUrl);
        } else {
            response.body(serveError(error));
        }
    }

    private String handleNotFound(Request request, Response response) {
        log.debug("not found: {}", request.pathInfo());
        return handleErrorResponse(response, Error.RESOURCE_NOT_FOUND, request.cookie("lang"), isRequestJson(request));
    }

    private String handleInternalServerError(Request request, Response response) {
        return handleErrorResponse(
                response, Error.INTERNAL_SERVER_ERROR, request.cookie("lang"), isRequestJson(request));
    }

    private String handleErrorResponse(Response response, Error error, String locale, boolean isJson) {
        error.locale = locale;
        if (isJson) {
            response.type("application/json");
            return generateJsonError(error);
        } else {
            return serveError(error);
        }
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

        public abstract ModelAndView handle(Request request, Response response) throws Exception;

        public abstract <T> ResultDTO<T> handleAPI(Request request, Response response) throws Exception;

        public RouteModel setRequireAuthentication(boolean requireAuthentication) {
            this.requireAuthentication = requireAuthentication;
            return this;
        }

        public RouteModel setRoleFilters(List<Filter> roleFilters) {
            this.roleFilters = roleFilters;
            return this;
        }

        public RouteModel setAPI(boolean isAPI) {
            this.isAPI = isAPI;
            return this;
        }
    }
}
