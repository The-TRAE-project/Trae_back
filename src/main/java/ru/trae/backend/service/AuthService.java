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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.LoginCredentials;
import ru.trae.backend.dto.jwt.JwtResponse;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.exceptionhandler.exception.LoginCredentialException;
import ru.trae.backend.util.jwt.JwtUtil;

/**
 * Service class for JWT processing.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class AuthService {
  private final ManagerService managerService;
  private final JwtUtil jwtUtil;
  private final BCryptPasswordEncoder encoder;

  /**
   * Logs in a manager with the given {@code credentials}.
   *
   * @param credentials the credentials used to log in the manager
   * @return a {@link JwtResponse} containing the access token and refresh token
   * @throws LoginCredentialException if the credentials are invalid
   */
  public JwtResponse login(LoginCredentials credentials) {
    final Manager manager = managerService.getManagerByUsername(credentials.username());

    checkNonLockedAccount(manager);

    if (encoder.matches(credentials.password(), manager.getPassword())) {
      final String accessToken = jwtUtil.generateAccessToken(manager.getUsername());
      final String refreshToken = jwtUtil.generateRefreshToken(manager.getUsername());

      return new JwtResponse(accessToken, refreshToken);
    } else {
      throw new LoginCredentialException(HttpStatus.BAD_REQUEST, "Invalid login credentials");
    }
  }

  /**
   * Logout a user.
   *
   * @param principal the user information
   */
  public void logout(Principal principal) {
    jwtUtil.deletePayloadRandomPieces(principal.getName());
  }

  /**
   * This method generates a new {@code accessToken} and returns a {@link JwtResponse}
   * containing the new {@code accessToken}.
   *
   * @param refreshToken the refresh token to validate and retrieve the user's username
   * @return a {@link JwtResponse} containing the new {@code accessToken}
   */
  public JwtResponse getAccessToken(String refreshToken) {
    final String login = jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken);

    final Manager manager = managerService.getManagerByUsername(login);

    checkNonLockedAccount(manager);

    final String accessToken = jwtUtil.generateAccessToken(manager.getUsername());
    return new JwtResponse(accessToken, null);
  }

  /**
   * Get new JwtResponse with new access token and refresh token.
   *
   * @param refreshToken refresh token which will be validated
   * @return new {@link JwtResponse} with new access token and refresh token
   */
  public JwtResponse getRefreshToken(String refreshToken) {
    final String login = jwtUtil.validateRefreshTokenAndRetrieveSubject(refreshToken);
    final Manager manager = managerService.getManagerByUsername(login);

    checkNonLockedAccount(manager);

    final String accessToken = jwtUtil.generateAccessToken(manager.getUsername());
    final String newRefreshToken = jwtUtil.generateRefreshToken(manager.getUsername());

    return new JwtResponse(accessToken, newRefreshToken);
  }

  private void checkNonLockedAccount(Manager m) {
    if (!m.isAccountNonLocked()) {
      throw new LoginCredentialException(HttpStatus.LOCKED, "This account is locked");
    }
  }
}
