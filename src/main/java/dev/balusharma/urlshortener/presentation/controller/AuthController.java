package dev.balusharma.urlshortener.presentation.controller;

import dev.balusharma.urlshortener.application.dto.TokenResponse;
import dev.balusharma.urlshortener.application.service.JwtService;
import dev.balusharma.urlshortener.domain.model.User;
import dev.balusharma.urlshortener.infrastructure.configuration.JwtProperties;
import dev.balusharma.urlshortener.infrastructure.persistence.repository.RefreshTokenRepository;
import dev.balusharma.urlshortener.infrastructure.persistence.repository.UserRepository;
import dev.balusharma.urlshortener.presentation.dto.request.LogoutRequest;
import dev.balusharma.urlshortener.presentation.dto.request.RefreshRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private JwtProperties jwtProperties;

    public AuthController(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            JwtProperties jwtProperties
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @Valid @RequestBody RefreshRequest request
    ) {
        String hashedToken = jwtService.hashToken(
                request.refreshToken()
        );

        Integer userId = refreshTokenRepository
                .consume(hashedToken)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid or expired refresh token"
                ));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "User not found"
                ));

        String newRefreshToken = jwtService.generateRefreshToken();

        refreshTokenRepository.create(
                user.id(),
                jwtService.hashToken(newRefreshToken),
                Instant.now().plusSeconds(
                        jwtProperties.refreshTokenExpiryDays() * 86400
                )
        );

        String accessToken = jwtService.generateAccessToken(
                user.id(),
                user.email(),
                user.name()
        );

        return ResponseEntity.ok(
                new TokenResponse(accessToken, newRefreshToken)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        refreshTokenRepository.revoke(
                jwtService.hashToken(request.refreshToken())
        );

        return ResponseEntity.noContent().build();
    }

}