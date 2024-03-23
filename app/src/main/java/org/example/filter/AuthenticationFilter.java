/* (C) 2023 */
package org.example.filter;

import java.util.UUID;
import javax.inject.Inject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.entity.User;
import org.example.exception.AccountDisabled;
import org.example.exception.LoginRequired;
import org.example.service.UserService;
import org.example.util.JwtUtil;
import spark.Filter;
import spark.Request;
import spark.Response;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AuthenticationFilter implements Filter {

    @NonNull private UserService userService;

    @Override
    public void handle(Request request, Response response) throws Exception {
        String refreshToken = request.cookie("refreshToken");
        String accessToken = request.session().attribute("accessToken");
        String userId = null;

        // the security flow currently as below. but I know not very good.
        // user login > save refresh token to cookie
        // auth required endpoint's called > check access token from session

        // there is no refresh token (not exists or expired)
        if (StringUtils.isEmpty(refreshToken)) {
            throw new LoginRequired();
        }

        // no access token or not valid
        if (StringUtils.isEmpty(accessToken)
                || StringUtils.isEmpty((userId = JwtUtil.validateAccessToken(accessToken)))) {
            // no access token find, mean need refresh access token
            accessToken = JwtUtil.refreshAccessToken(refreshToken);
            if (StringUtils.isEmpty(accessToken)) {
                // invalid refresh token
                request.session().removeAttribute("accessToken");
                response.removeCookie("/", "refreshToken");
                throw new LoginRequired();
            }
            request.session().attribute("accessToken", accessToken);

            log.debug("new access token: {}", accessToken);
        }

        // access token valid
        if (StringUtils.isEmpty(userId)) {
            userId = JwtUtil.validateAccessToken(accessToken);
        }

        // check user exists in the database and user is active
        User user = userService.getUser(UUID.fromString(userId));
        if (user == null) {
            throw new LoginRequired();
        }

        if (!user.isActive()) {
            throw new AccountDisabled();
        }

        request.attribute("userId", user.getId());
    }
}
