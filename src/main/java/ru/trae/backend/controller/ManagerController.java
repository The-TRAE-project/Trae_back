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
import io.swagger.v3.oas.annotations.Parameter;
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
import ru.trae.backend.dto.manager.ChangeRoleAndStatusReq;
import ru.trae.backend.dto.manager.ChangingManagerDataReq;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.exceptionhandler.exception.ManagerException;
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
  @Operation(summary = "Данные учетной записи пользователя",
      description = "Доступен администратору. Возвращает ДТО пользователя")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "ДТО пользователя",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ManagerDto.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильные формат идентификатора",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Пользователь с таким идентификатором не найден", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/{managerId}")
  public ResponseEntity<ManagerDto> manager(
      @PathVariable @Parameter(description = "Идентификатор пользователя") long managerId) {
    Manager m = managerService.getManagerById(managerId);
    return ResponseEntity.ok(managerService.convertFromManager(m));
  }

  @Operation(summary = "Список всех пользователей",
      description = "Доступен администратору. Возвращает список ДТО пользователей")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список ДТО пользователей",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = List.class))}),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/managers")
  public ResponseEntity<List<ManagerDto>> managers() {
    return ResponseEntity.ok(managerService.getAllManagers());
  }

  @Operation(summary = "Сброс пароля указанного пользователя",
      description = "Доступен администратору. В теле запроса поле пароля не требует заполнения."
          + " Возвращает логин и новый пароль пользователя")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Логин и пароль пользователя",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = Credentials.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат логина(юзернейма)",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Пользователь с таким логином(юзернеймом) не найден", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/reset-password")
  public ResponseEntity<Credentials> resetPassword(
      @Valid @RequestBody Credentials credentials) {
    return ResponseEntity.ok(managerService.resetPassword(credentials));
  }

  @Operation(summary = "Изменение пароля",
      description = "Доступен аутентифицированным пользователям. "
          + "Позволяет пользователям изменять собственные пароли")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ничего не возвращает, только статус",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Неправильный формат нового пароля,"
          + " не совпадение старого пароля",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "409", description = "Новый пароль должен отличаться от старого",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/change-password")
  public ResponseEntity<HttpStatus> changePassword(
      @Valid @RequestBody ChangePassReq request, Principal principal) {
    managerService.changePassword(request, principal.getName());
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Изменение данных учетной записи",
      description = "Доступен аутентифицированным пользователям. "
          + "Позволяет пользователям изменять собственные данные."
          + " Поля, не требующие изменения должны быть NULL")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ничего не возвращает, только статус",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Неправильный формат новых данных",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("update-data")
  public ResponseEntity<HttpStatus> updateData(
      @Valid @RequestBody ChangingManagerDataReq changeManagerData, Principal principal) {
    managerService.updateData(changeManagerData, principal.getName());
    return ResponseEntity.ok().build();
  }

//  @Operation(summary = "Включение учетной записи пользователя",
//      description = "Доступен администратору. "
//          + "Включает, ранее отключенные, учетные записи пользователей по идентификатору")
//  @ApiResponses(value = {
//      @ApiResponse(responseCode = "200", description = "Ничего не возвращает, только статус",
//          content = @Content),
//      @ApiResponse(responseCode = "400", description = "Неправильные формат идентификатора",
//          content = @Content),
//      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
//          content = @Content),
//      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
//          content = @Content),
//      @ApiResponse(responseCode = "404",
//          description = "Пользователь с таким идентификатором не найден", content = @Content),
//      @ApiResponse(responseCode = "409", description = "Учетная запись уже включена",
//          content = @Content),
//      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
//          content = @Content)})
//  @PostMapping("/activate-account/{managerId}")
//  public ResponseEntity<HttpStatus> activateAccount(
//      @Valid @PathVariable @Parameter(description = "Идентификатор пользователя") long managerId) {
//    managerService.activateAccount(managerId);
//    return ResponseEntity.ok().build();
//  }
//
//  @Operation(summary = "Отключение учетной записи пользователя",
//      description = "Доступен администратору. "
//          + "Отключает учетные записи пользователей по идентификатору")
//  @ApiResponses(value = {
//      @ApiResponse(responseCode = "200", description = "Ничего не возвращает, только статус",
//          content = @Content),
//      @ApiResponse(responseCode = "400", description = "Неправильные формат идентификатора",
//          content = @Content),
//      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
//          content = @Content),
//      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
//          content = @Content),
//      @ApiResponse(responseCode = "404",
//          description = "Пользователь с таким идентификатором не найден", content = @Content),
//      @ApiResponse(responseCode = "409", description = "Учетная запись уже отключена",
//          content = @Content),
//      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
//          content = @Content)})
//  @PostMapping("/deactivate-account/{managerId}")
//  public ResponseEntity<HttpStatus> deactivateAccount(
//      @Valid @PathVariable @Parameter(description = "Идентификатор пользователя") long managerId) {
//    managerService.deactivateAccount(managerId);
//    return ResponseEntity.ok().build();
//  }

  @Operation(summary = "Список всех ролей",
      description = "Доступен администратору. Возвращает список ролей")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список ролей",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = List.class))}),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/roles")
  public ResponseEntity<List<String>> roles() {
    return ResponseEntity.ok(managerService.getRoleList());
  }

  /**
   * This controller is used to change manager role and account status.
   *
   * @author Vladimir Olennikov
   */
  @Operation(summary = "Изменение роли пользователя, отключение/включение учетной записи",
      description =
          "Доступен администратору. Изменяет роль и статус учетной записи выбранного пользователя")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ничего не возвращает, только статус",
          content = @Content),
      @ApiResponse(responseCode = "400", description = "Неправильный формат новой роли,"
          + " неправильный формат даты увольнения",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Пользователь с таким идентификатором не найден", content = @Content),
      @ApiResponse(responseCode = "409", description = "Пользователь уже имеет такую роль,"
          + " учетная запись уже отключена/включена",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/change-role-status")
  public ResponseEntity<HttpStatus> changeRoleAndStatus(
      @Valid @RequestBody ChangeRoleAndStatusReq request) {
    managerService.changeRoleAndStatus(request);
    return ResponseEntity.ok().build();
  }
}
