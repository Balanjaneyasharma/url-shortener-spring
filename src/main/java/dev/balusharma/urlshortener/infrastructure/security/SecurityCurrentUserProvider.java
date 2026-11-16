package dev.balusharma.urlshortener.infrastructure.security;

import dev.balusharma.urlshortener.application.context.CurrentUserContext;
import dev.balusharma.urlshortener.application.service.CurrentUserProvider;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class SecurityCurrentUserProvider implements CurrentUserProvider {

    private static final String USER_ID_CLAIM =
            "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier";

    private static final String EMAIL_CLAIM =
            "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress";

    private static final String NAME_CLAIM =
            "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name";

    @Override
    public CurrentUserContext getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)
                || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException(
                    "Authenticated JWT user not found"
            );
        }

        var jwt = jwtAuth.getToken();

        String userId = jwt.getClaimAsString(USER_ID_CLAIM);

        if (userId == null) {
            throw new AuthenticationCredentialsNotFoundException(
                    "User ID claim is missing"
            );
        }

        return new CurrentUserContext(
                Integer.valueOf(userId),
                jwt.getClaimAsString(EMAIL_CLAIM),
                jwt.getClaimAsString(NAME_CLAIM)
        );
    }
}