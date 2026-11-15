package dev.balusharma.urlshortener.infrastructure.security;

import dev.balusharma.urlshortener.application.dto.TokenResponse;
import dev.balusharma.urlshortener.application.service.JwtService;
import dev.balusharma.urlshortener.domain.model.User;
import dev.balusharma.urlshortener.infrastructure.configuration.JwtProperties;
import dev.balusharma.urlshortener.infrastructure.persistence.repository.RefreshTokenRepository;
import dev.balusharma.urlshortener.infrastructure.persistence.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Instant;

@Component
public class OAuth2SuccessHandler
        implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;

    public OAuth2SuccessHandler(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            JwtProperties jwtProperties,
            ObjectMapper objectMapper
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OidcUser googleUser = (OidcUser) authentication.getPrincipal();

        String googleId = googleUser.getSubject();
        String email = googleUser.getEmail();
        String name = googleUser.getFullName();

        User user = userRepository.findOrCreate(
                email,
                googleId,
                name
        );

        String accessToken = jwtService.generateAccessToken(
                user.id(),
                user.email(),
                user.name()
        );

        String refreshToken = jwtService.generateRefreshToken();

        String hashedToken = jwtService.hashToken(refreshToken);

        Instant expiresAt = Instant.now().plusSeconds(
                jwtProperties.refreshTokenExpiryDays() * 86400
        );

        refreshTokenRepository.create(
                user.id(),
                hashedToken,
                expiresAt
        );

        TokenResponse tokenResponse = new TokenResponse(
                accessToken,
                refreshToken
        );

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(
                response.getOutputStream(),
                tokenResponse
        );
    }
}