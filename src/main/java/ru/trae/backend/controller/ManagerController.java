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
import javax.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.Credentials;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.manager.AccountInfo;
import ru.trae.backend.dto.manager.ChangeRoleAndStatusReq;
import ru.trae.backend.dto.manager.ChangeRoleAndStatusResp;
import ru.trae.backend.dto.manager.ChangingManagerDataReq;
import ru.trae.backend.dto.manager.ChangingManagerDataResp;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.dto.manager.ManagerShortDto;
import ru.trae.backend.dto.manager.ResetPassResp;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.service.ManagerService;
import ru.trae.backend.util.PageSettings;
import ru.trae.backend.util.RegExpression;
import springfox.documentation.annotations.ApiIgnore;

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
   * Returns role of the authorized user.
   *
   * @param principal Principal
   * @return Role of the authorized user
   */
  @Operation(summary = "Получение роли аутентифицированного пользователя",
      description = "Доступен аутентифицированным пользователям. "
          + "Возвращает роль аутентифицированного пользователя")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Роль пользователя",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = String.class))}),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping(path = "/role", produces = {"application/json; charset=UTF-8"})
  public ResponseEntity<String> roleAuthUser(@ApiIgnore Principal principal) {
    return ResponseEntity.ok(managerService.getRoleAuthUser(principal));
  }

  @Operation(summary = "Информация для личного кабинета аутентифицированного пользователя",
      description = "Доступен аутентифицированным пользователям. "
          + "Возвращает информацию для личного кабиента аутентифицированного пользователя")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "ФИО, номер телефона аутентифицированного пользователя",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = AccountInfo.class))}),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping(path = "/account-info", produces = {"application/json; charset=UTF-8"})
  public ResponseEntity<AccountInfo> accountInfo(@ApiIgnore Principal principal) {
    return ResponseEntity.ok(managerService.getAccountInfoAuthUser(principal));
  }

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
    managerService.checkAvailableCredentials(dto.firstName(), dto.middleName(), dto.lastName());
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
      @ApiResponse(responseCode = "400", description = "Неправильный формат идентификатора",
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

  /**
   * Get manager list.
   *
   * @param role        filter by role
   * @param status      filter by status
   * @param pageSetting page settings with parameters
   * @return {@link ResponseEntity} with {@link PageDto} of {@link ManagerDto}
   */
  @Operation(summary = "Список пользователей",
      description = "Доступен администратору. Возвращает список ДТО пользователей")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список ДТО пользователей. "
          + "В примере указан единичный объект из списка",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ManagerShortDto.class))}),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/managers")
  public ResponseEntity<PageDto<ManagerShortDto>> managers(
      @Valid PageSettings pageSetting,
      @RequestParam(required = false) @Parameter(description = "Фильтрация по роли") String role,
      @RequestParam(required = false) @Parameter(description = "Фильтрация по статусу")
      Boolean status) {

    Sort managerSort = pageSetting.buildManagerOrEmpSort();
    Pageable managerPage = PageRequest.of(
        pageSetting.getPage(), pageSetting.getElementPerPage(), managerSort);
    return ResponseEntity.ok(managerService.getManagerDtoPage(managerPage, role, status));
  }

  @Operation(summary = "Сброс пароля указанного пользователя",
      description = "Доступен администратору. Возвращает фамилию, имя и новый пароль пользователя")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Фамилия, имя и новый пароль пользователя",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ResetPassResp.class))}),
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
  public ResponseEntity<ResetPassResp> resetPassword(
      @Valid @RequestParam(name = "username")
      @Pattern(regexp = RegExpression.USERNAME, message = "Неправильный формат логина(юзернейма)")
      String username) {
    return ResponseEntity.ok(managerService.resetPassword(username));
  }

  /**
   * Method for updating manager data.
   *
   * @param changeManagerData {@link ChangingManagerDataReq}
   * @param principal         {@link Principal}
   * @return {@link ChangingManagerDataResp}
   */
  @Operation(summary = "Изменение данных учетной записи",
      description = "Доступен аутентифицированным пользователям. "
          + "Позволяет пользователям изменять собственные данные, включая пароль."
          + " Поля, не требующие изменения должны быть NULL")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Возвращает ФИО и номер телефона, за исключением пароля.",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ChangingManagerDataResp.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат новых данных",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "409", description = "Пользователь уже имеет такие данные,"
          + " новый пароль совпадает с текущим.",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/update-data")
  public ResponseEntity<ChangingManagerDataResp> updateData(
      @Valid @RequestBody ChangingManagerDataReq changeManagerData, Principal principal) {
    managerService.updateData(changeManagerData, principal.getName());
    return ResponseEntity.ok(
        managerService.getResultOfChangingData(principal.getName(),
            changeManagerData.newPassword() != null ? changeManagerData.newPassword() : null));
  }

  @Operation(summary = "Список ролей",
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
   */
  @Operation(summary = "Изменение роли пользователя, отключение/включение учетной записи",
      description =
          "Доступен администратору. Изменяет роль и статус учетной записи выбранного пользователя")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Возвращает ДТО с фамилией, именем, "
          + "ролью, статусом, датой увольнения (если ее нет, то NULL)",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ChangeRoleAndStatusResp.class))}),
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
  public ResponseEntity<ChangeRoleAndStatusResp> changeRoleAndStatus(
      @Valid @RequestBody ChangeRoleAndStatusReq request) {
    managerService.changeRoleAndStatus(request);
    return ResponseEntity.ok(managerService.getChangeRoleAndStatusResp(request.managerId()));
  }
}
