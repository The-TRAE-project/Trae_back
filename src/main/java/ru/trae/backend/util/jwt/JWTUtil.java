package ru.trae.backend.util.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.trae.backend.entity.PayloadRandomPiece;
import ru.trae.backend.exceptionhandler.exception.CustomJWTVerificationException;
import ru.trae.backend.exceptionhandler.exception.PayloadPieceException;
import ru.trae.backend.repository.PayloadRandomPieceRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JWTUtil {
    @Value("${jwt.access.duration}")
    private int accessDuration;
    @Value("${jwt.refresh.duration}")
    private int refreshDuration;
    @Value("${jwt.access.secret}")
    private String secret;
    @Value("${jwt.refresh.secret}")
    private String refreshSecret;

    private final PayloadRandomPieceRepository payloadRandomPieceRepository;

    public String generateAccessToken(String username) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusMinutes(accessDuration).atZone(ZoneId.systemDefault()).toInstant();

        return JWT.create()
                .withSubject("User Details")
                .withClaim("username", username)
                .withExpiresAt(accessExpirationInstant)
                .withIssuer("Trae project")
                .sign(Algorithm.HMAC256(secret));
    }

    public String generateRefreshToken(String username) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(refreshDuration).atZone(ZoneId.systemDefault()).toInstant();
        String uuid = UUID.randomUUID().toString();

        if (payloadRandomPieceRepository.existsByUsernameIgnoreCase(username)) {
            payloadRandomPieceRepository.updateUuidByUsernameIgnoreCase(uuid, username);
        } else {
            payloadRandomPieceRepository.save(new PayloadRandomPiece(null, username, uuid));
        }

        return JWT.create()
                .withSubject("User Details")
                .withClaim("username", username)
                .withExpiresAt(refreshExpirationInstant)
                .withIssuer("Trae project")
                .withPayload(Collections.singletonMap("UUID", uuid))
                .sign(Algorithm.HMAC256(refreshSecret));
    }

    public String validateAccessTokenAndRetrieveSubject(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User Details")
                .withIssuer("Trae project")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("username").asString();
    }

    public String validateRefreshTokenAndRetrieveSubject(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(refreshSecret))
                .withSubject("User Details")
                .withIssuer("Trae project")
                .build();
        DecodedJWT jwt = verifier.verify(token);
        String username = jwt.getClaim("username").asString();

        Optional<PayloadRandomPiece> prp = payloadRandomPieceRepository.findByUsernameIgnoreCase(username);
        if (prp.isEmpty())
            throw new PayloadPieceException(HttpStatus.NOT_FOUND, "Payload piece not found!");

        String savedUuid = prp.get().getUuid();

        if (!savedUuid.equals(jwt.getClaim("UUID").asString())) {
            throw new CustomJWTVerificationException(HttpStatus.BAD_REQUEST, "Invalid token UUID");
        }
        return username;
    }

    public void deletePayloadRandomPieces(String username) {
        Optional<PayloadRandomPiece> prp = payloadRandomPieceRepository.findByUsernameIgnoreCase(username);
        if (prp.isPresent()) {
            payloadRandomPieceRepository.delete(prp.get());
        } else {
            throw new PayloadPieceException(HttpStatus.NOT_FOUND, "Payload piece not found!");
        }
    }

}