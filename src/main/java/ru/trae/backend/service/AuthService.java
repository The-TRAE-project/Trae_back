/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.service;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;
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

/**
 * Service class for JWT processing.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class AuthService {
  private final ManagerService managerService;
  private final JWTUtil jwtUtil;
  private final BCryptPasswordEncoder encoder;

  /**
   * Method provides user login.
   *
   * @param credentials a dto with login and password from the account
   * @return a dto with a pair of tokens - access and refresh
   */
  public JwtResponse login(LoginCredentials credentials) {
    final Manager manager = managerService.getManagerByUsername(credentials.username());
    if (encoder.matches(credentials.password(), manager.getPassword())) {
      final String accessToken = jwtUtil.generateAccessToken(manager.getUsername());
      final String refreshToken = jwtUtil.generateRefreshToken(manager.getUsername());

      return new JwtResponse(accessToken, refreshToken);
    } else {
      throw new LoginCredentialException(HttpStatus.BAD_REQUEST, "Invalid login credentials");
    }
  }

  /**
   * Method provides user logout.
   *
   * @param principal is the currently logged-in user
   * @return key "status" with string value
   */
  public ResponseEntity<Map<String, String>> logout(Principal principal) {
    jwtUtil.deletePayloadRandomPieces(principal.getName());
    return ResponseEntity.ok().body(Collections.singletonMap("status", "You successfully logout!"));
  }

  /**
   * Method to get a new access token.
   *
   * @param refreshToken token with a long lifetime
   * @return only access token
   */
  public JwtResponse getAccessToken(String refreshToken) {
    final String login = jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken);

    final Manager manager = managerService.getManagerByUsername(login);
    final String accessToken = jwtUtil.generateAccessToken(manager.getUsername());
    return new JwtResponse(accessToken, null);
  }

  /**
   * Method to get a new pair of tokens.
   *
   * @param refreshToken token with a long lifetime
   * @return a dto with a pair of tokens - access and refresh
   */
  public JwtResponse getRefreshToken(String refreshToken) {
    final String login = jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken);

    final Manager manager = managerService.getManagerByUsername(login);
    final String accessToken = jwtUtil.generateAccessToken(manager.getUsername());
    final String newRefreshToken = jwtUtil.generateRefreshToken(manager.getUsername());

    return new JwtResponse(accessToken, newRefreshToken);
  }

}
