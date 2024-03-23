/* (C) 2023 */
package org.example.controller;

import com.google.gson.Gson;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.example.annoation.Controller;
import org.example.annoation.Endpoint;
import org.example.annoation.authorization.PermitAll;
import org.example.common.CustomValidator;
import org.example.common.HTTPMethod;
import org.example.dto.*;
import org.example.service.*;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
@PermitAll
@Controller
public class DashboardController extends AbstractController {

    @NonNull private Gson gson;

    @NonNull private CustomValidator validator;

    @NonNull private UserService userService;

    @NonNull private LogService logService;

    @Endpoint(method = HTTPMethod.GET, path = "/")
    public ModelAndView serverIndex(Request request, Response response) {
        return makeView("home", Pair.of("title", "Home"));
    }

    @Endpoint(method = HTTPMethod.GET, path = "/dashboard")
    public ModelAndView serverDashboard(Request request, Response response) {
        return makeView("dashboard", Pair.of("title", "Dashboard"));
    }
}
