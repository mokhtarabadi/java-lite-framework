/* (C) 2023 */
package org.example.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class JwtUtil {

    private static final String ISSUER = "your-issuer";
    private static final String CLIENT_SECRET = "your-client-secret";

    // Token expiration time in seconds
    public final int EXPIRATION_TIME_SECONDS = (int) TimeUnit.MINUTES.toSeconds(5);

    // Refresh token expiration time in seconds
    public final int REFRESH_TOKEN_EXPIRATION_SECONDS = (int) TimeUnit.DAYS.toSeconds(28);

    public String createAccessToken(String userId) {
        Algorithm algorithm = Algorithm.HMAC256(CLIENT_SECRET);

        Calendar expirationTime = Calendar.getInstance();
        expirationTime.add(Calendar.SECOND, EXPIRATION_TIME_SECONDS);

        String accessToken = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(userId)
                .withExpiresAt(expirationTime.getTime())
                .sign(algorithm);

        log.trace("Access Token: {}", accessToken);
        return accessToken;
    }

    /**
     * Validates the given access token.
     *
     * @param accessToken
     *            the access token to validate
     * @return the subject of the decoded JWT (in case username) or null if token is
     *         invalid
     */
    public String validateAccessToken(String accessToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(CLIENT_SECRET);
            DecodedJWT decodedJWT =
                    JWT.require(algorithm).withIssuer(ISSUER).build().verify(accessToken);

            log.trace("Token validation successful");
            return decodedJWT.getSubject();
        } catch (JWTVerificationException e) {
            log.error("Token validation failed", e);
            return null;
        }
    }

    public String createRefreshToken(String userId) {
        Algorithm algorithm = Algorithm.HMAC256(CLIENT_SECRET);

        Calendar expirationTime = Calendar.getInstance();
        expirationTime.add(Calendar.SECOND, REFRESH_TOKEN_EXPIRATION_SECONDS);

        String refreshToken = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(userId)
                .withExpiresAt(expirationTime.getTime())
                .sign(algorithm);

        log.trace("Refresh Token: {}", refreshToken);
        return refreshToken;
    }

    /**
     * Refreshes the access token using the provided refresh token.
     *
     * @param refreshToken
     *            the refresh token to be used for token refresh
     * @return the new access token after refresh or null if refreshToken invalid
     */
    public String refreshAccessToken(String refreshToken) {
        // In a real-world scenario, you would validate the refresh token against your
        // database or Auth0 server
        // Here; we're simply decoding the refresh token to get the user ID
        try {
            Algorithm algorithm = Algorithm.HMAC256(CLIENT_SECRET);
            DecodedJWT decodedJWT =
                    JWT.require(algorithm).withIssuer(ISSUER).build().verify(refreshToken);

            String newAccessToken = createAccessToken(decodedJWT.getSubject());

            log.trace("Access Token Refreshed: {}", newAccessToken);
            return newAccessToken;
        } catch (JWTVerificationException e) {
            log.error("Refresh token verification failed", e);
            return null;
        }
    }
}
