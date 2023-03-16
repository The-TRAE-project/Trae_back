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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.Credentials;
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

  @Operation(summary = "Логин в систему",
      description = "Доступен всем. Возвращает аксесс и рефреш токены,"
          + " создает и сохраняет пейлоад в бд")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Аксесс и рефреш токены",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = JwtResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильные учетные данные",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/login")
  public ResponseEntity<JwtResponse> login(@Valid @RequestBody Credentials credentials) {
    final JwtResponse token = authService.login(credentials);
    return ResponseEntity.ok(token);
  }

  @Operation(summary = "Логаут",
      description = "Доступен аутентифицированным пользователям. "
          + "Обнуляет текущий рефреш токен пользователя, путем удаления пейлоада из базы данных")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description =
          "Логаут успешно совершен, пейлоад удален, рефреш токен более недействителен",
          content = {@Content}),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content)})
  @DeleteMapping("/logout")
  public ResponseEntity<HttpStatus> logout(Principal principal) {
    authService.logout(principal);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Получение свежего аксесс токена",
      description = "Доступен всем. Возвращает аксесс токен")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Аксесс токен, рефреш токен = null",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = JwtResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат рефреш токена",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/token")
  public ResponseEntity<JwtResponse> newAccessToken(
      @Valid @RequestBody RefreshJwtRequest request) {
    final JwtResponse token = authService.getAccessToken(request.refreshToken());
    return ResponseEntity.ok(token);
  }

  @Operation(summary = "Получение акесесс и рефреш токена",
      description = "Доступен аутентифицированным пользователям. "
          + "Возвращает аксесс и рефреш токены в обмен на рефреш токен")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Аксесс и рефреш токены",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = JwtResponse.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат рефреш токена",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/refresh")
  public ResponseEntity<JwtResponse> newRefreshToken(
      @Valid @RequestBody RefreshJwtRequest request) {
    final JwtResponse token = authService.getRefreshToken(request.refreshToken());
    return ResponseEntity.ok(token);
  }
}
