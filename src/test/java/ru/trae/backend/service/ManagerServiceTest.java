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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.trae.backend.dto.Credentials;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.manager.ChangePassReq;
import ru.trae.backend.dto.manager.ChangeRoleAndStatusReq;
import ru.trae.backend.dto.manager.ChangeRoleAndStatusResp;
import ru.trae.backend.dto.manager.ChangingManagerDataReq;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.dto.manager.ManagerShortDto;
import ru.trae.backend.dto.manager.ResetPassResp;
import ru.trae.backend.dto.mapper.ManagerDtoMapper;
import ru.trae.backend.dto.mapper.PageToPageDtoMapper;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.exceptionhandler.exception.ManagerException;
import ru.trae.backend.repository.ManagerRepository;
import ru.trae.backend.util.Role;
import ru.trae.backend.util.jwt.JwtUtil;

@ExtendWith(MockitoExtension.class)
class ManagerServiceTest {
  @Mock
  private ManagerDtoMapper managerDtoMapper;
  @Mock
  private PageToPageDtoMapper pageToPageDtoMapper;
  @Mock
  private ManagerRepository managerRepository;
  @Mock
  private BCryptPasswordEncoder encoder;
  @Mock
  private JwtUtil jwtUtil;
  @InjectMocks
  private ManagerService managerService;

  @Test
  void saveNewManagerTest() {
    //given
    ManagerRegisterDto dto = new ManagerRegisterDto(
        "Ivan", "Ivanovich", "Ivanov",
        "ivanov@email.com", "ivan",
        LocalDate.now());

    //when
    when(encoder.encode(anyString()))
        .thenReturn("$2y$10$Dq3vh.fEJ8yiRK1z9Xpj6OuE7VpUyTfH7jzO6QrrT6V8U6fA/9VjK");

    Credentials credentials = managerService.saveNewManager(dto);

    //then
    assertNotNull(credentials);
    assertEquals(dto.username(), credentials.username());
    assertNotNull(credentials.password());
  }

  @Test
  void getManagerById_whenManagerExists_shouldReturnManager() {
    // given
    long managerId = 1L;
    Manager expectedManager = new Manager();
    expectedManager.setId(managerId);

    when(managerRepository.findById(managerId))
        .thenReturn(Optional.of(expectedManager));

    // when
    Manager actualManager = managerService.getManagerById(managerId);

    // then
    assertThat(actualManager).isEqualTo(expectedManager);
  }

  @Test
  void getManagerById_whenManagerDoesNotExist_shouldThrowException() {
    // given
    long managerId = 1L;
    Mockito.when(managerRepository.findById(managerId))
        .thenReturn(Optional.empty());

    // when
    // then
    assertThrows(ManagerException.class, () -> managerService.getManagerById(managerId));
  }

  @Test
  void whenGetManagerByUsername_thenReturnManager() {
    // given
    String username = "test";
    Manager manager = new Manager();
    manager.setUsername(username);
    when(managerRepository.findByUsername(username)).thenReturn(Optional.of(manager));

    // when
    Manager result = managerService.getManagerByUsername(username);

    // then
    assertEquals(manager, result);
  }

  @Test
  void getManagerByUsername_whenManagerDoesNotExist_shouldThrowException() {
    // given
    String username = "test";
    when(managerRepository.findByUsername(username))
        .thenReturn(Optional.empty());

    // when
    // then
    assertThrows(ManagerException.class, () -> managerService.getManagerByUsername(username));
  }

  @Test
  void getManagerPage_shouldReturnManagerPage() {
    // given
    Pageable pageable = PageRequest.of(0, 10);
    boolean status = true;

    Page<Manager> expectedPage = Page.empty();
    when(managerRepository.findByAccountNonLockedAndRole(pageable, status, Role.ROLE_ADMINISTRATOR))
        .thenReturn(expectedPage);

    // when
    Page<Manager> actualPage =
        managerService.getManagerPage(pageable, Role.ROLE_ADMINISTRATOR.value, status);

    // then
    assertEquals(expectedPage, actualPage);
    verify(managerRepository)
        .findByAccountNonLockedAndRole(pageable, status, Role.ROLE_ADMINISTRATOR);
  }

  @Test
  void testGetManagerDtoPage() {
    // given
    Pageable managerPage = PageRequest.of(0, 10);
    String role = Role.ROLE_ADMINISTRATOR.value;
    boolean status = true;
    Page<Manager> page = mock(Page.class);
    PageDto<ManagerShortDto> expectedPageDto = mock(PageDto.class);

    // when
    when(managerRepository.findByAccountNonLockedAndRole(
        managerPage, status, Role.ROLE_ADMINISTRATOR)).thenReturn(page);
    when(pageToPageDtoMapper.managerPageToPageDto(page)).thenReturn(expectedPageDto);

    PageDto<ManagerShortDto> pageDto = managerService.getManagerDtoPage(managerPage, role, status);

    // then
    assertThat(pageDto).isEqualTo(expectedPageDto);
  }

  @Test
  void shouldResetPassword() {
    // given
    String username = "username";
    String lastName = "lastNameTest";
    String firstName = "firstNameTest";
    String lastAndFirstName = lastName + "," + firstName;
    when(managerRepository.existsByUsernameIgnoreCase(username)).thenReturn(true);
    when(managerRepository.getLastAndFirstNameByUsername(username)).thenReturn(lastAndFirstName);
    when(encoder.encode(anyString()))
        .thenReturn("$2y$10$Dq3vh.fEJ8yiRK1z9Xpj6OuE7VpUyTfH7jzO6QrrT6V8U6fA/9VjK");

    // when
    ResetPassResp resetPassResp =
        managerService.resetPassword(username);

    // then
    assertEquals(lastName, resetPassResp.lastName());
    assertEquals(firstName, resetPassResp.firstName());
  }

  @Test
  void shouldChangePassword() {
    // Given
    ChangePassReq request = new ChangePassReq("oldPass", "newPass");
    String username = "testUser";
    String encodedPassword = "newEncodedPassword";

    when(encoder.encode(request.newPassword())).thenReturn(encodedPassword);

    // When
    managerService.changePassword(request, username);

    // Then
    verify(managerRepository).updatePasswordByUsername(encodedPassword, username);
  }

  @Test
  void testConvertFromManager() {
    //given
    long managerId = 1L;
    String lastName = "testLastName";
    String middleName = "testMiddleName";
    String firstName = "testFirstName";
    String phone = "+7 (000) 000 0000";
    String role = "admin";
    String username = "username";
    boolean status = true;
    LocalDate dateOfEmp = LocalDate.now();
    Manager m = new Manager();
    m.setId(managerId);
    m.setLastName(lastName);
    m.setMiddleName(middleName);
    m.setFirstName(firstName);
    m.setPhone(phone);
    m.setUsername(username);
    m.setAccountNonLocked(status);
    m.setDateOfEmployment(dateOfEmp);

    ManagerDto expectedManagerDto = new ManagerDto(managerId, firstName, middleName, lastName, phone,
        role, username, status, dateOfEmp.toString(), null);

    //when
    when(managerDtoMapper.apply(m)).thenReturn(expectedManagerDto);
    ManagerDto actualManagerDto = managerService.convertFromManager(m);

    //then
    assertEquals(expectedManagerDto, actualManagerDto);
  }

  @Test
  void testChangeRoleAndStatus() {
    // given
    ChangeRoleAndStatusReq request = new ChangeRoleAndStatusReq(
        1L, Role.ROLE_ADMINISTRATOR.value, true, LocalDate.now());
    Manager manager = new Manager();
    manager.setRole(Role.ROLE_MANAGER);
    manager.setDateOfDismissal(LocalDate.now());
    manager.setId(1L);
    manager.setAccountNonLocked(true);

    when(managerRepository.getRoleById(1L)).thenReturn(Role.ROLE_MANAGER);
    when(managerRepository.existsById(1L)).thenReturn(true);

    // when
    managerService.changeRoleAndStatus(request);

    //then
    assertEquals(manager.isAccountNonLocked(), request.accountStatus());
  }

  @Test
  void getChangeRoleAndStatusResp_shouldReturnChangeRoleAndStatusResp_whenManagerIdIsGiven() {
    //given
    long managerId = 1;
    Manager m = new Manager();
    m.setLastName("lastName");
    m.setFirstName("firstName");
    m.setRole(Role.ROLE_ADMINISTRATOR);
    m.setAccountNonLocked(true);
    m.setDateOfDismissal(null);

    //when
    when(managerRepository.findById(managerId)).thenReturn(Optional.of(m));

    ChangeRoleAndStatusResp result = managerService.getChangeRoleAndStatusResp(managerId);

    //then
    assertEquals("lastName", result.lastName());
    assertEquals("firstName", result.firstName());
    assertEquals(Role.ROLE_ADMINISTRATOR.value, result.role());
    Assertions.assertTrue(result.accountStatus());
    assertNull(result.dateOfDismissal());
  }

  @Test
  void getRoleList_ShouldReturnListOfRoles() {
    //given
    List<String> expected = List.of(
        Role.ROLE_ADMINISTRATOR.value,
        Role.ROLE_MANAGER.value,
        Role.ROLE_EMPLOYEE.value);

    //when
    List<String> actual = managerService.getRoleList();

    //then
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void getRoleAuthUser_shouldReturnRole_whenUsernameIsValid() {
    //given
    Principal principal = Mockito.mock(Principal.class);
    when(principal.getName()).thenReturn("username");
    when(managerRepository.getRoleByUsername(principal.getName())).thenReturn(Role.ROLE_ADMINISTRATOR);

    //when
    String role = managerService.getRoleAuthUser(principal);

    //then
    assertEquals(Role.ROLE_ADMINISTRATOR.value, role);
  }

  @Test
  void getRoleAuthUser_shouldReturnAnonymous_whenUsernameIsNull() {
    //when
    String role = managerService.getRoleAuthUser(null);

    //then
    assertEquals("Anonymous", role);
  }

  @Test
  void checkAvailableUsername_WhenUsernameIsAvailable_ShouldNotThrowException() {
    //given
    String username = "test";
    when(managerRepository.existsByUsernameIgnoreCase(username)).thenReturn(false);

    //when
    managerService.checkAvailableUsername(username);

    //then
    verify(managerRepository, times(1)).existsByUsernameIgnoreCase(username);
  }

  @Test
  void checkAvailableUsername_WhenUsernameIsNotAvailable_ShouldThrowException() {
    //given
    String username = "test";
    when(managerRepository.existsByUsernameIgnoreCase(username)).thenReturn(true);

    //when
    assertThrows(
        ManagerException.class,
        () -> managerService.checkAvailableUsername(username),
        "Username: test already in use");
  }

  @Test
  void whenUpdateData_thenReturnUpdatedManager() {
    //given
    ChangingManagerDataReq request =
        new ChangingManagerDataReq("newFirstName", "newMiddleName",
            "newLastName", "+7 (000) 000 0000");

    Manager m = new Manager();
    m.setPhone("+7 (111) 111 1111");
    m.setUsername("username");
    m.setFirstName("testFirstName");
    m.setMiddleName("testMiddleName");
    m.setLastName("testLastName");

    //when
    when(managerRepository.findByUsername("username")).thenReturn(Optional.of(m));

    managerService.updateData(request, "username");

    //then
    verify(managerRepository).save(m);
    assertEquals(request.firstName(), m.getFirstName());
    assertEquals(request.middleName(), m.getMiddleName());
    assertEquals(request.lastName(), m.getLastName());
    assertEquals(request.phone(), m.getPhone());
  }

}