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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import ru.trae.backend.entity.PayloadRandomPiece;
import ru.trae.backend.exceptionhandler.exception.CustomJwtVerificationException;
import ru.trae.backend.exceptionhandler.exception.PayloadPieceException;
import ru.trae.backend.repository.PayloadRandomPieceRepository;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {
  @Mock
  private PayloadRandomPieceRepository payloadRandomPieceRepository;
  @InjectMocks
  private JwtUtil jwtUtil;
  private int accessDuration = 10;
  private int refreshDuration = 1;
  private String secret = "Super_test_secret";
  private String refreshSecret = "Super_test_refresh_secret";
  private static final String SUBJECT = "User Details";
  private static final String CLAIM_FOR_TOKEN = "username";
  private static final String ISSUER_FOR_TOKEN = "Trae project";

  @BeforeEach
  public void setUp() {
    ReflectionTestUtils.setField(jwtUtil, "secret", secret);
    ReflectionTestUtils.setField(jwtUtil, "refreshSecret", refreshSecret);
    ReflectionTestUtils.setField(jwtUtil, "accessDuration", accessDuration);
    ReflectionTestUtils.setField(jwtUtil, "refreshDuration", refreshDuration);
    ReflectionTestUtils.setField(jwtUtil, "refreshDuration", refreshDuration);
  }

  @Test
  void generateAccessTokenTest() {
    String username = "test_user";

    String token = jwtUtil.generateAccessToken(username);

    assertNotNull(token);
    assertTrue(token.startsWith("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"));
    assertTrue(token.contains("."));
  }

  @Test
  void generateRefreshTokenTest_whenUsernameExists() {
    final String username = "username";

    when(payloadRandomPieceRepository.existsByUsernameIgnoreCase(username)).thenReturn(true);

    final String refreshToken = jwtUtil.generateRefreshToken(username);
    assertNotNull(refreshToken);
    final DecodedJWT decodedJWT = JWT.decode(refreshToken);
    assertEquals(username, decodedJWT.getClaim(CLAIM_FOR_TOKEN).asString());
    assertEquals(ISSUER_FOR_TOKEN, decodedJWT.getIssuer());
    assertEquals(SUBJECT, decodedJWT.getSubject());
    assertEquals(username, decodedJWT.getClaim(CLAIM_FOR_TOKEN).asString());
  }

  @Test
  void generateRefreshTokenTest_whenUsernameNotExists() {
    final String username = "username";

    when(payloadRandomPieceRepository.existsByUsernameIgnoreCase(username)).thenReturn(false);

    final String refreshToken = jwtUtil.generateRefreshToken(username);
    assertNotNull(refreshToken);
    final DecodedJWT decodedJWT = JWT.decode(refreshToken);
    assertEquals(username, decodedJWT.getClaim(CLAIM_FOR_TOKEN).asString());
    assertEquals(ISSUER_FOR_TOKEN, decodedJWT.getIssuer());
    assertEquals(SUBJECT, decodedJWT.getSubject());
    assertEquals(username, decodedJWT.getClaim(CLAIM_FOR_TOKEN).asString());
  }

  @Test
  void validateAccessTokenAndRetrieveSubjectTest() {
    String token = createToken();

    String subject = jwtUtil.validateAccessTokenAndRetrieveSubject(token);

    assertEquals("username", subject);
  }

  private String createToken() {
    return JWT.create()
        .withSubject(SUBJECT)
        .withIssuer(ISSUER_FOR_TOKEN)
        .withClaim(CLAIM_FOR_TOKEN, "username")
        .sign(Algorithm.HMAC256(secret));
  }

  @Test
  void validateRefreshTokenAndRetrieveSubject_whenUsernameInDatabase_shouldReturnSubject() {
    String username = "username";
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVc2VyIERldGFpbHMiLCJpc3MiOiJUcmFlIHByb2plY3QiLCJleHAiOjE2Nzk2NTUyODEsIlVVSUQiOiI3NmMwZWE3ZC1iMmRjLTRjMzItYWNkYy0yNjcyNGMxZWJiZGYiLCJ1c2VybmFtZSI6InVzZXJuYW1lIn0.r369T9sFyaJo-dbrzD6DY0U9Y1fcXTCJ7vqf6L490-8";

    PayloadRandomPiece payloadRandomPiece = new PayloadRandomPiece();
    payloadRandomPiece.setUsername(username);
    payloadRandomPiece.setUuid("76c0ea7d-b2dc-4c32-acdc-26724c1ebbdf");
    when(payloadRandomPieceRepository
        .findByUsernameIgnoreCase(anyString()))
        .thenReturn(Optional.of(payloadRandomPiece));

    String result = jwtUtil.validateRefreshTokenAndRetrieveSubject(token);

    assertThat(result).isEqualTo(username);
  }

  @Test
  void validateRefreshTokenAndRetrieveSubject_whenUsernameNotInDatabase_shouldThrowException() {
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVc2VyIERldGFpbHMiLCJpc3MiOiJUcmFlIHByb2plY3QiLCJleHAiOjE2Nzk2NTUyODEsIlVVSUQiOiI3NmMwZWE3ZC1iMmRjLTRjMzItYWNkYy0yNjcyNGMxZWJiZGYiLCJ1c2VybmFtZSI6InVzZXJuYW1lIn0.r369T9sFyaJo-dbrzD6DY0U9Y1fcXTCJ7vqf6L490-8";
    when(payloadRandomPieceRepository
        .findByUsernameIgnoreCase(anyString()))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> jwtUtil.validateRefreshTokenAndRetrieveSubject(token))
        .isInstanceOf(PayloadPieceException.class)
        .hasMessage("Payload piece not found!")
        .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
  }

  @Test
  void validateRefreshTokenAndRetrieveSubject_whenUuidNotMatching_shouldThrowException() {
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJVc2VyIERldGFpbHMiLCJpc3MiOiJUcmFlIHByb2plY3QiLCJleHAiOjE2Nzk2NTUyODEsIlVVSUQiOiI3NmMwZWE3ZC1iMmRjLTRjMzItYWNkYy0yNjcyNGMxZWJiZGYiLCJ1c2VybmFtZSI6InVzZXJuYW1lIn0.r369T9sFyaJo-dbrzD6DY0U9Y1fcXTCJ7vqf6L490-8";
    String username = "username";
    PayloadRandomPiece payloadRandomPiece = new PayloadRandomPiece();
    payloadRandomPiece.setUsername(username);
    payloadRandomPiece.setUuid("3b2633bf-2293-42dc-aa38-d5eeb63d7157");

    when(payloadRandomPieceRepository
        .findByUsernameIgnoreCase(anyString()))
        .thenReturn(Optional.of(payloadRandomPiece));

    assertThatThrownBy(() -> jwtUtil.validateRefreshTokenAndRetrieveSubject(token))
        .isInstanceOf(CustomJwtVerificationException.class)
        .hasMessage("Invalid token UUID")
        .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
  }

  @Test
  void deletePayloadRandomPieces() {
    String username = "test";

    when(payloadRandomPieceRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(new PayloadRandomPiece()));

    jwtUtil.deletePayloadRandomPieces(username);

    assertFalse(payloadRandomPieceRepository.findByUsernameIgnoreCase(username).isEmpty());
  }

  @Test
  void deletePayloadRandomPieces_NotFound() {
    String username = "test";

    when(payloadRandomPieceRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());

    jwtUtil.deletePayloadRandomPieces(username);

    assertTrue(payloadRandomPieceRepository.findByUsernameIgnoreCase(username).isEmpty());
  }
}