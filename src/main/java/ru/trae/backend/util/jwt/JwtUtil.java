/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.util.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.trae.backend.entity.PayloadRandomPiece;
import ru.trae.backend.exceptionhandler.exception.CustomJwtVerificationException;
import ru.trae.backend.exceptionhandler.exception.PayloadPieceException;
import ru.trae.backend.repository.PayloadRandomPieceRepository;

/**
 * This class provides methods to generate access token, refresh token, validate access token and
 * refresh token and delete payload random pieces.
 *
 * @author Vladimir Olennikov
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {
  @Value("${jwt.access.duration}")
  private int accessDuration;
  @Value("${jwt.refresh.duration}")
  private int refreshDuration;
  @Value("${jwt.access.secret}")
  private String secret;
  @Value("${jwt.refresh.secret}")
  private String refreshSecret;
  private static final String SUBJECT = "User Details";
  private static final String CLAIM_FOR_TOKEN = "username";
  private static final String ISSUER_FOR_TOKEN = "Trae project";

  private final PayloadRandomPieceRepository payloadRandomPieceRepository;

  /**
   * Generates an access token for the given username.
   *
   * @param username the username
   * @return the generated access token
   */
  public String generateAccessToken(String username) {
    final LocalDateTime now = LocalDateTime.now();
    final Instant accessExpirationInstant =
        now.plusMinutes(accessDuration).atZone(ZoneId.systemDefault()).toInstant();

    return JWT.create()
        .withSubject(SUBJECT)
        .withClaim(CLAIM_FOR_TOKEN, username)
        .withExpiresAt(accessExpirationInstant)
        .withIssuer(ISSUER_FOR_TOKEN)
        .sign(Algorithm.HMAC256(secret));
  }

  /**
   * Generates a refresh token for the given username.
   *
   * @param username the username
   * @return the generated refresh token
   */
  public String generateRefreshToken(String username) {
    final LocalDateTime now = LocalDateTime.now();
    final Instant refreshExpirationInstant =
        now.plusDays(refreshDuration).atZone(ZoneId.systemDefault()).toInstant();
    String uuid = UUID.randomUUID().toString();

    if (payloadRandomPieceRepository.existsByUsernameIgnoreCase(username)) {
      payloadRandomPieceRepository.updateUuidByUsernameIgnoreCase(uuid, username);
    } else {
      payloadRandomPieceRepository.save(new PayloadRandomPiece(null, username, uuid));
    }

    return JWT.create()
        .withSubject(SUBJECT)
        .withClaim(CLAIM_FOR_TOKEN, username)
        .withExpiresAt(refreshExpirationInstant)
        .withIssuer(ISSUER_FOR_TOKEN)
        .withPayload(Collections.singletonMap("UUID", uuid))
        .sign(Algorithm.HMAC256(refreshSecret));
  }

  /**
   * Validates the access token and retrieves the username from it.
   *
   * @param token the access token
   * @return the username
   */
  public String validateAccessTokenAndRetrieveSubject(String token) {
    JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
        .withSubject(SUBJECT)
        .withIssuer(ISSUER_FOR_TOKEN)
        .build();
    DecodedJWT jwt = verifier.verify(token);
    return jwt.getClaim(CLAIM_FOR_TOKEN).asString();
  }

  /**
   * Validates the refresh token and retrieves the username from it.
   *
   * @param token the refresh token
   * @return the username
   */
  public String validateRefreshTokenAndRetrieveSubject(String token) {
    JWTVerifier verifier = JWT.require(Algorithm.HMAC256(refreshSecret))
        .withSubject(SUBJECT)
        .withIssuer(ISSUER_FOR_TOKEN)
        .build();
    DecodedJWT jwt = verifier.verify(token);
    String username = jwt.getClaim(CLAIM_FOR_TOKEN).asString();

    Optional<PayloadRandomPiece> prp =
        payloadRandomPieceRepository.findByUsernameIgnoreCase(username);
    if (prp.isEmpty()) {
      throw new PayloadPieceException(HttpStatus.NOT_FOUND, "Payload piece not found!");
    }

    String savedUuid = prp.get().getUuid();

    if (!savedUuid.equals(jwt.getClaim("UUID").asString())) {
      throw new CustomJwtVerificationException(HttpStatus.BAD_REQUEST, "Invalid token UUID");
    }
    return username;
  }

  /**
   * Deletes the payload random pieces of the given username.
   *
   * @param username the username
   */
  public void deletePayloadRandomPieces(String username) {
    Optional<PayloadRandomPiece> prp =
        payloadRandomPieceRepository.findByUsernameIgnoreCase(username);
    prp.ifPresent(payloadRandomPieceRepository::delete);
  }

}