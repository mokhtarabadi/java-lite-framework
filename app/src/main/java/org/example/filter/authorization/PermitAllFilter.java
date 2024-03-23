/* (C) 2023 */
package org.example.filter.authorization;

import java.util.UUID;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.common.AuthorizationRole;
import org.example.exception.AccessDenied;
import org.example.service.UserService;
import spark.Filter;
import spark.Request;
import spark.Response;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PermitAllFilter implements Filter {

    @NonNull private UserService userService;

    @Override
    public void handle(Request request, Response response) throws Exception {
        // find user from jwt token from cookie.
        UUID id = request.attribute("userId");

        if (!userService.doesUserHaveRole(id, AuthorizationRole.ROLE_USER)
                && !userService.doesUserHaveRole(id, AuthorizationRole.ROLE_ADMIN)) {
            throw new AccessDenied();
        }
    }
}
