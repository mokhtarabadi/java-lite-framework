/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2023] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
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

        for (AuthorizationRole role : AuthorizationRole.all()) {
            if (userService.doesUserHaveRole(id, role)) {
                return;
            }
        }

        throw new AccessDenied();
    }
}
