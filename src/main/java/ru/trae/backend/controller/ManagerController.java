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
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.Credentials;
import ru.trae.backend.dto.manager.ChangePassReq;
import ru.trae.backend.dto.manager.ChangeRoleReq;
import ru.trae.backend.dto.manager.ChangingManagerDataReq;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.service.ManagerService;

/**
 * Controller class for operations related to managers.
 *
 * @author Vladimir Olennikov
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/manager")
public class ManagerController {
  private final ManagerService managerService;

  /**
   * Register a new manager.
   *
   * @param dto the data of the new manager
   * @return the credentials of the registered manager
   */
  @Operation(summary = "Регистрация учетной записи менеджера",
      description = "Доступен администратору. Возвращает логин и пароль созданного пользователя")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Логин и пароль созданного пользователя",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = Credentials.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильные данные пользователя",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "409", description = "Такой юзернейм уже используется",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/register")
  public ResponseEntity<Credentials> register(@Valid @RequestBody ManagerRegisterDto dto) {
    managerService.checkAvailableUsername(dto.username());
    return new ResponseEntity<>(managerService.saveNewManager(dto), HttpStatus.CREATED);
  }

  /**
   * Gets a manager by its id.
   *
   * @param managerId the id of the manager
   * @return the response entity with the manager data
   */
  @GetMapping("/{managerId}")
  public ResponseEntity<ManagerDto> manager(@PathVariable long managerId) {
    Manager m = managerService.getManagerById(managerId);
    return ResponseEntity.ok(managerService.convertFromManager(m));
  }

  @GetMapping("/managers")
  public ResponseEntity<List<ManagerDto>> managers() {
    return ResponseEntity.ok(managerService.getAllManagers());
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Credentials> resetPassword(
      @Valid @RequestBody Credentials credentials) {
    return ResponseEntity.ok(managerService.resetPassword(credentials));
  }

  @PostMapping("/change-password")
  public ResponseEntity<HttpStatus> changePassword(
      @Valid @RequestBody ChangePassReq request, Principal principal) {
    managerService.changePassword(request, principal.getName());
    return ResponseEntity.ok().build();
  }

  @PostMapping("update-data")
  public ResponseEntity<HttpStatus> updateData(
      @Valid @RequestBody ChangingManagerDataReq changeManagerData, Principal principal) {
    managerService.updateData(changeManagerData, principal.getName());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/activate-account/{managerId}")
  public ResponseEntity<HttpStatus> activateAccount(@PathVariable long managerId) {
    managerService.activateAccount(managerId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/deactivate-account/{managerId}")
  public ResponseEntity<HttpStatus> deactivateAccount(@PathVariable long managerId) {
    managerService.deactivateAccount(managerId);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/roles")
  public ResponseEntity<List<String>> roles() {
    return ResponseEntity.ok(managerService.getRoleList());
  }

  @PostMapping("/change-role")
  public ResponseEntity<HttpStatus> changeRole(@Valid @RequestBody ChangeRoleReq request) {
    managerService.changeRole(request);
    return ResponseEntity.ok().build();
  }
}
