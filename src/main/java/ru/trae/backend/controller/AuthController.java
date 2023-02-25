package ru.trae.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.LoginCredentials;
import ru.trae.backend.dto.jwt.JwtResponse;
import ru.trae.backend.dto.jwt.RefreshJwtRequest;
import ru.trae.backend.service.AuthService;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginCredentials credentials) {
        final JwtResponse token = authService.login(credentials);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(Principal principal) {
        return authService.logout(principal);
    }

    @PostMapping("/token")
    public ResponseEntity<JwtResponse> newAccessToken(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authService.getAccessToken(request.refreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> newRefreshToken(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authService.getRefreshToken(request.refreshToken());
        return ResponseEntity.ok(token);
    }

}
