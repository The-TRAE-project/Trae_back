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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.trae.backend.dto.Credentials;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.manager.ChangePassReq;
import ru.trae.backend.dto.manager.ChangeRoleAndStatusReq;
import ru.trae.backend.dto.manager.ChangeRoleAndStatusResp;
import ru.trae.backend.dto.manager.ChangingManagerDataReq;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.manager.ManagerDtoShort;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.dto.manager.ResetPassResp;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.service.ManagerService;
import ru.trae.backend.util.PageSettings;

@ExtendWith(MockitoExtension.class)
class ManagerControllerTest {
  @Mock
  ManagerService managerService;
  @Mock
  Principal principal;
  @InjectMocks
  ManagerController managerController;

  @Test
  void roleAuthUserTest() {
    when(managerService.getRoleAuthUser(principal)).thenReturn("ADMIN");

    ResponseEntity<String> responseEntity = managerController.roleAuthUser(principal);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody()).isEqualTo("ADMIN");
  }

  @Test
  void rolesTest() {
    List<String> expectedRoles = List.of("USER", "ADMIN");
    when(managerService.getRoleList()).thenReturn(expectedRoles);

    ResponseEntity<List<String>> responseEntity = managerController.roles();

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody()).isEqualTo(expectedRoles);
  }

  @Test
  void registerTest() {
    ManagerRegisterDto dto = new ManagerRegisterDto(
        "name", null, "last name",
        "+7 (000) 000 0000", "username1", LocalDate.now());
    Credentials credentials = new Credentials("username1", "QwertY123");
    when(managerService.saveNewManager(dto)).thenReturn(credentials);

    ResponseEntity<Credentials> responseEntity = managerController.register(dto);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseEntity.getBody()).isEqualTo(credentials);
  }

  @Test
  void managerTest() {
    long managerId = 1L;
    Manager manager = mock(Manager.class);
    when(managerService.getManagerById(managerId)).thenReturn(manager);
    ManagerDto managerDto = mock(ManagerDto.class);
    when(managerService.convertFromManager(manager)).thenReturn(managerDto);

    ResponseEntity<ManagerDto> managerResponse = managerController.manager(managerId);

    assertThat(managerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(managerResponse.getBody()).isEqualTo(managerDto);
  }

  @Test
  void managersTest() {
    PageSettings pageSettings = new PageSettings();
    pageSettings.setPage(1);
    pageSettings.setElementPerPage(2);

    ManagerDtoShort managerDtoShort = new ManagerDtoShort(1L, "managerLastName",
        "managerFirstName");
    PageDto<ManagerDtoShort> pageDto = new PageDto<>(Collections.singletonList(managerDtoShort), 1L);
    when(managerService.getManagerDtoPage(Mockito.any(), Mockito.anyString(), Mockito.anyBoolean()))
        .thenReturn(pageDto);

    ResponseEntity<PageDto<ManagerDtoShort>> responseEntity =
        managerController.managers(pageSettings, "Конструктор", true);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(1L, Objects.requireNonNull(responseEntity.getBody()).totalElements());
    assertEquals(1L, responseEntity.getBody().content().get(0).managerId());
    assertEquals("managerLastName", responseEntity.getBody().content().get(0).lastName());
  }

  @Test
  void resetPasswordWhenUsernameIsValidTest() {
    String username = "username";
    ResetPassResp expectedResponseBody = new ResetPassResp("lastName", "firstName", "newPassword");
    when(managerService.resetPassword(username)).thenReturn(expectedResponseBody);

    ResponseEntity<ResetPassResp> actual = managerController.resetPassword(username);

    assertEquals(HttpStatus.OK, actual.getStatusCode());
    assertEquals(expectedResponseBody, actual.getBody());
  }

  @Test
  void resetPasswordWhenUsernameIsInvalidTest() {
    ResponseEntity<ResetPassResp> actual = ResponseEntity.badRequest().build();

    assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
  }

  @Test
  void changePasswordTest() {
    ChangePassReq request = new ChangePassReq("test-password", "test-password1");
    Principal principal = () -> "test-user";

    ResponseEntity<HttpStatus> response = managerController.changePassword(request, principal);

    verify(managerService, times(1))
        .changePassword(request, principal.getName());
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void updateDataTest() {
    ChangingManagerDataReq changeManagerData =
        new ChangingManagerDataReq("firstName", "middleName", "lastName", "phone");
    Principal principal = Mockito.mock(Principal.class);
    when(principal.getName()).thenReturn("userName");

    managerController.updateData(changeManagerData, principal);

    assertDoesNotThrow(
        () ->
            verify(managerService, times(1))
                .updateData(changeManagerData, principal.getName()));
  }

  @Test
  void changeRoleAndStatusTest() {
    ChangeRoleAndStatusReq request =
        new ChangeRoleAndStatusReq(1L, "role", true, LocalDate.now());
    ChangeRoleAndStatusResp response =
        new ChangeRoleAndStatusResp("lastName", "firstName", "role", true, LocalDate.now());

    when(managerService.getChangeRoleAndStatusResp(1L)).thenReturn(response);

    ResponseEntity<ChangeRoleAndStatusResp> result =
        managerController.changeRoleAndStatus(request);

    verify(managerService).changeRoleAndStatus(request);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(response, result.getBody());
  }
}