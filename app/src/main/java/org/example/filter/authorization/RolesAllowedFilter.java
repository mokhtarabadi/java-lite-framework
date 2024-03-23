/* (C) 2023 */
package org.example.filter.authorization;

import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.common.AuthorizationRole;
import org.example.exception.AccessDenied;
import org.example.service.UserService;
import spark.Filter;
import spark.Request;
import spark.Response;

@RequiredArgsConstructor
public class RolesAllowedFilter implements Filter {

    @NonNull private UserService userService;

    @NonNull private List<AuthorizationRole> roles;

    @Override
    public void handle(Request request, Response response) throws Exception {
        // find user from jwt token from cookie.
        UUID id = request.attribute("userId");

        for (AuthorizationRole role : roles) {
            if (userService.doesUserHaveRole(id, role)) {
                return;
            }
        }

        throw new AccessDenied();
    }
}
