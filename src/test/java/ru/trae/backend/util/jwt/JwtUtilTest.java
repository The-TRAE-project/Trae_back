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
    //given
    String username = "test_user";
    String token = jwtUtil.generateAccessToken(username);

    //then
    assertNotNull(token);
    assertTrue(token.startsWith("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"));
    assertTrue(token.contains("."));
  }

  @Test
  void generateRefreshTokenTest_whenUsernameExists() {
    //given
    final String username = "username";

    //when
    when(payloadRandomPieceRepository.existsByUsernameIgnoreCase(username)).thenReturn(true);

    //then
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
    //given
    final String username = "username";

    //when
    when(payloadRandomPieceRepository.existsByUsernameIgnoreCase(username)).thenReturn(false);

    //then
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
    //given
    String token = createToken();

    //when
    String subject = jwtUtil.validateAccessTokenAndRetrieveSubject(token);

    //then
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
    //given
    String username = "username";
    final String refreshToken = jwtUtil.generateRefreshToken(username);
    final DecodedJWT decodedJWT = JWT.decode(refreshToken);
    PayloadRandomPiece payloadRandomPiece = new PayloadRandomPiece();
    payloadRandomPiece.setUsername(username);
    payloadRandomPiece.setUuid(decodedJWT.getClaim("UUID").asString());

    //when
    when(payloadRandomPieceRepository
        .findByUsernameIgnoreCase(anyString()))
        .thenReturn(Optional.of(payloadRandomPiece));

    String result = jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken);

    //then
    assertThat(result).isEqualTo(username);
  }

  @Test
  void validateRefreshTokenAndRetrieveSubject_whenUsernameNotInDatabase_shouldThrowException() {
    //given
    final String username = "username";
    final String refreshToken = jwtUtil.generateRefreshToken(username);

    //when
    when(payloadRandomPieceRepository
        .findByUsernameIgnoreCase(anyString()))
        .thenReturn(Optional.empty());

    //then
    assertThatThrownBy(() -> jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken))
        .isInstanceOf(PayloadPieceException.class)
        .hasMessage("Payload piece not found!")
        .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
  }

  @Test
  void validateRefreshTokenAndRetrieveSubject_whenUuidNotMatching_shouldThrowException() {
    //given
    String username = "username";
    final String refreshToken = jwtUtil.generateRefreshToken(username);

    PayloadRandomPiece payloadRandomPiece = new PayloadRandomPiece();
    payloadRandomPiece.setUsername(username);
    payloadRandomPiece.setUuid("3b2633bf-2293-42dc-aa38-d5eeb63d7157");

    //when
    when(payloadRandomPieceRepository
        .findByUsernameIgnoreCase(anyString()))
        .thenReturn(Optional.of(payloadRandomPiece));

    //then
    assertThatThrownBy(() -> jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken))
        .isInstanceOf(CustomJwtVerificationException.class)
        .hasMessage("Invalid token UUID")
        .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
  }

  @Test
  void deletePayloadRandomPieces() {
    //given
    String username = "test";

    //when
    when(payloadRandomPieceRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(new PayloadRandomPiece()));

    jwtUtil.deletePayloadRandomPieces(username);

    //then
    assertFalse(payloadRandomPieceRepository.findByUsernameIgnoreCase(username).isEmpty());
  }

  @Test
  void deletePayloadRandomPieces_NotFound() {
    //given
    String username = "test";

    //when
    when(payloadRandomPieceRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());

    jwtUtil.deletePayloadRandomPieces(username);

    //then
    assertTrue(payloadRandomPieceRepository.findByUsernameIgnoreCase(username).isEmpty());
  }
}
