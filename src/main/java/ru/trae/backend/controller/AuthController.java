/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.controller;

import java.security.Principal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.LoginCredentials;
import ru.trae.backend.dto.jwt.JwtResponse;
import ru.trae.backend.dto.jwt.RefreshJwtRequest;
import ru.trae.backend.service.AuthService;

/**
 * AuthController is a controller class which provides various APIs for authentication and
 * authorization.
 * It provides APIs to login, logout, generate new access token and refresh token.
 *
 * @author Vladimir Olennikov
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(@RequestBody LoginCredentials credentials) {
    final JwtResponse token = authService.login(credentials);
    return ResponseEntity.ok(token);
  }

  @DeleteMapping("/logout")
  public ResponseEntity<HttpStatus> logout(Principal principal) {
    authService.logout(principal);
    return ResponseEntity.ok().build();
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
