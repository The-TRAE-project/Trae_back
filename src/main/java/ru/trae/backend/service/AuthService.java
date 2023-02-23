package ru.trae.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.LoginCredentials;
import ru.trae.backend.dto.jwt.JwtResponse;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.exceptionhandler.exception.LoginCredentialException;
import ru.trae.backend.util.jwt.JWTUtil;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final ManagerService managerService;
    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public JwtResponse login(LoginCredentials credentials) {
        final Manager manager = managerService.getManagerByUsername(credentials.username());
        if (bCryptPasswordEncoder.matches(credentials.password(), manager.getPassword())) {
            final String accessToken = jwtUtil.generateAccessToken(manager.getUsername());
            final String refreshToken = jwtUtil.generateRefreshToken(manager.getUsername());

            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new LoginCredentialException(HttpStatus.BAD_REQUEST, "Invalid login credentials");
        }
    }

    public ResponseEntity<Map<String, String>> logout(Principal principal) {
        jwtUtil.deletePayloadRandomPieces(principal.getName());
        return ResponseEntity.ok().body(Collections.singletonMap("status", "You successfully logout!"));
    }

    public JwtResponse getAccessToken(String refreshToken) {
        final String login = jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken);

        final Manager manager = managerService.getManagerByUsername(login);
        final String accessToken = jwtUtil.generateAccessToken(manager.getUsername());
        return new JwtResponse(accessToken, null);
    }

    public JwtResponse getRefreshToken(String refreshToken) {
        final String login = jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken);

        final Manager manager = managerService.getManagerByUsername(login);
        final String accessToken = jwtUtil.generateAccessToken(manager.getUsername());
        final String newRefreshToken = jwtUtil.generateRefreshToken(manager.getUsername());

        return new JwtResponse(accessToken, newRefreshToken);
    }

}
