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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.trae.backend.dto.Credentials;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.manager.ChangePassReq;
import ru.trae.backend.dto.manager.ChangeRoleAndStatusReq;
import ru.trae.backend.dto.manager.ChangingManagerDataReq;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.dto.mapper.ManagerDtoMapper;
import ru.trae.backend.dto.mapper.PageToPageDtoMapper;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.exceptionhandler.exception.ManagerException;
import ru.trae.backend.repository.ManagerRepository;
import ru.trae.backend.util.PasswordGenerator;
import ru.trae.backend.util.Role;
import ru.trae.backend.util.jwt.JwtUtil;

/**
 * Service class for working with manager data.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class ManagerService {
  private final ManagerRepository managerRepository;
  private final ManagerDtoMapper managerDtoMapper;
  private final PageToPageDtoMapper pageToPageDtoMapper;
  private final BCryptPasswordEncoder encoder;
  private final JwtUtil jwtUtil;

  /**
   * This method allows to save a new manager in the database. It takes an object of type
   * ManagerRegisterDto as an argument, encodes the password, sets all the fields and saves the
   * object into the database. The method returns an object of type ManagerDto.
   *
   * @param dto contains data for creating a new manager
   * @return manager dto
   */
  public Credentials saveNewManager(ManagerRegisterDto dto) {
    Manager m = new Manager();

    String temporaryRandomPass = new PasswordGenerator.Builder()
        .digits(1)
        .lower(1)
        .upper(1)
        .punctuation().custom("-._")
        .generate(8);

    String encodedPass = encoder.encode(temporaryRandomPass);

    m.setFirstName(dto.firstName());
    m.setMiddleName(dto.middleName());
    m.setLastName(dto.lastName());
    m.setPhone(dto.phone());
    m.setUsername(dto.username());
    m.setPassword(encodedPass);
    m.setRole(Role.ROLE_MANAGER);
    m.setDateOfRegister(LocalDate.now());
    m.setDateOfEmployment(dto.dateOfEmployment());
    m.setDateOfDismissal(null);

    m.setEnabled(true);
    m.setAccountNonExpired(true);
    m.setAccountNonLocked(true);
    m.setCredentialsNonExpired(true);

    managerRepository.save(m);

    return new Credentials(m.getUsername(), temporaryRandomPass);
  }

  /**
   * Retrieves a manager by their id.
   *
   * @param managerId the id of the manager
   * @return the manager
   * @throws ManagerException if the manager is not found
   */
  public Manager getManagerById(long managerId) {
    return managerRepository.findById(managerId).orElseThrow(
        () -> new ManagerException(HttpStatus.NOT_FOUND,
            "Manager with ID: " + managerId + " not found"));
  }

  /**
   * Retrieves a Manager from the repository based on the given username.
   *
   * @param username The username of the Manager to be retrieved.
   * @return The Manager with the given username.
   * @throws ManagerException If no Manager with the given username is found.
   */
  public Manager getManagerByUsername(String username) {
    return managerRepository.findByUsername(username).orElseThrow(
        () -> new ManagerException(HttpStatus.NOT_FOUND,
            "Manager with username: " + username + " not found"));
  }

  /**
   * Gets a page of managers.
   *
   * @param managerPage the pageable object with page settings
   * @param role        the role of the manager
   * @param status      the status of the manager
   * @return a page of managers
   */
  public Page<Manager> getManagerPage(Pageable managerPage, String role, Boolean status) {
    Page<Manager> page;

    if (status != null && role != null) {
      page = managerRepository.findByAccountNonLockedAndRole(
          managerPage, status, Role.getRoleByValue(role));
    } else if (status != null) {
      page = managerRepository.findByAccountNonLocked(managerPage, status);
    } else if (role != null) {
      page = managerRepository.findByRole(managerPage, Role.getRoleByValue(role));
    } else {
      page = managerRepository.findAll(managerPage);
    }
    return page;
  }

  public PageDto<ManagerDto> getManagerDtoPage(Pageable managerPage, String role, Boolean status) {
    return pageToPageDtoMapper.managerPageToPageDto(getManagerPage(managerPage, role, status));
  }

  /**
   * This method is used to reset a manager's password using their username.
   * It randomly generates a 6 character alphanumeric password and then encodes the password using
   * an encoder.
   * The encoded password is then used to update the password of the manager with the given
   * username.
   * Finally, it returns a ManagerCredentials object containing the manager's username and the new,
   * temporary password.
   *
   * @param username contains the manager's username.
   * @return A Credentials object containing the manager's username and the new, temporary
   *     password.
   */
  @Transactional
  public Credentials resetPassword(String username) {
    checkExistsUsername(username);
    checkManagerRole(username, Role.ROLE_ADMINISTRATOR);

    String temporaryRandomPass = new PasswordGenerator.Builder()
        .digits(1)
        .lower(1)
        .upper(1)
        .punctuation().custom("-._")
        .generate(8);

    String encodedPass = encoder.encode(temporaryRandomPass);

    managerRepository.updatePasswordByUsername(encodedPass, username);

    jwtUtil.deletePayloadRandomPieces(username);

    return new Credentials(username, temporaryRandomPass);
  }

  /**
   * Allows to change the password of a manager.
   *
   * @param request the request for change the password.
   */
  public void changePassword(ChangePassReq request, String username) {
    checkDifferencePasswords(request.newPassword(), request.oldPassword());

    String encodedPass = encoder.encode(request.newPassword());
    managerRepository.updatePasswordByUsername(encodedPass, username);
  }

  public ManagerDto convertFromManager(Manager manager) {
    return managerDtoMapper.apply(manager);
  }

  /**
   * Change role and status of the manager.
   *
   * @param request The change role and status request.
   * @throws ManagerException when either new role or status is not specified.
   */
  @Transactional
  public void changeRoleAndStatus(ChangeRoleAndStatusReq request) {
    if (request.accountStatus() == null
        && request.dateOfDismissal() == null && request.newRole() == null) {
      throw new ManagerException(HttpStatus.BAD_REQUEST,
          "Не указаны статус учетной записи или новоя роль");
    }

    if (request.newRole() != null) {
      changeRole(request.managerId(), request.newRole());
    }

    if (Boolean.TRUE.equals(request.accountStatus())) {
      activateAccount(request.managerId());
    } else if (request.dateOfDismissal() != null) {
      deactivateAccount(request.managerId(), request.dateOfDismissal());
    } else {
      throw new ManagerException(HttpStatus.BAD_REQUEST,
          "Не указана дата увольнения пользователя");
    }
  }

  /**
   * This method activate account.
   *
   * @param managerId the manager id
   * @throws ManagerException in case if manager not found or already activated
   */
  public void activateAccount(long managerId) {
    if (!managerRepository.existsById(managerId)) {
      throw new ManagerException(HttpStatus.NOT_FOUND,
          "The manager with id: " + managerId + " not found");
    }
    if (managerRepository.existsByIdAndAccountNonLocked(managerId, true)) {
      throw new ManagerException(HttpStatus.CONFLICT, "This manager already activated");
    }

    managerRepository.updateAccountNonLockedAndDateOfDismissalById(
        true, null, managerId);
  }

  /**
   * This method deactivate account.
   *
   * @param managerId the manager id
   * @throws ManagerException in case if manager not found or already deactivated
   */
  public void deactivateAccount(long managerId, LocalDate dateOfDismissal) {
    if (!managerRepository.existsById(managerId)) {
      throw new ManagerException(HttpStatus.NOT_FOUND,
          "The manager with id: " + managerId + " not found");
    }
    if (managerRepository.existsByIdAndAccountNonLocked(managerId, false)) {
      throw new ManagerException(HttpStatus.CONFLICT, "This manager already deactivated");
    }

    managerRepository.updateAccountNonLockedAndDateOfDismissalById(
        false, dateOfDismissal, managerId);
  }

  /**
   * This is the method to update the data of a manager in the system. It takes in
   * ChangingManagerDataReq request and the username of the manager to update their
   * data in the system. It will check the request and update the data accordingly.
   * The data that can be updated are the firstName, middleName, lastName, and phone
   * number of the manager. Then it will save the changes to the repository.
   *
   * @param request  the changing manager data request which contains all the
   *                 data to be updated
   * @param username the username of the manager whose data needs to be updated
   */
  public void updateData(ChangingManagerDataReq request, String username) {
    Manager m = getManagerByUsername(username);
    if (request.firstName() != null && !request.firstName().equals(m.getFirstName())) {
      m.setFirstName(request.firstName());
    }
    if (request.middleName() != null && !request.middleName().equals(m.getMiddleName())) {
      m.setFirstName(request.middleName());
    }
    if (request.lastName() != null && !request.lastName().equals(m.getLastName())) {
      m.setFirstName(request.lastName());
    }
    if (request.phone() != null && !request.phone().equals(m.getPhone())) {
      m.setFirstName(request.phone());
    }

    managerRepository.save(m);
  }

  /**
   * Get a list of roles.
   *
   * @return A list of roles
   */
  public List<String> getRoleList() {
    return List.of(Role.ROLE_MANAGER.value,
        Role.ROLE_ADMINISTRATOR.value,
        Role.ROLE_EMPLOYEE.value);
  }

  /**
   * Method changes a role of a certain manager.
   *
   * @param managerId the id of the manager
   * @param newRole   new role for the manager
   * @throws ManagerException in case the role is not found in the system or the account
   *                          already has such a role.
   */
  public void changeRole(long managerId, final String newRole) {
    Manager m = getManagerById(managerId);

    if (m.getRole().value.equals(newRole)) {
      throw new ManagerException(HttpStatus.CONFLICT, "The account already has such a role");
    }

    if (Arrays.stream(Role.values()).anyMatch(r -> r.value.equals(newRole))) {
      Role role = Role.getRoleByValue(newRole);

      managerRepository.updateRoleById(role, managerId);
    } else {
      throw new ManagerException(HttpStatus.BAD_REQUEST,
          "The role: " + newRole + " not found");
    }
  }

  public boolean existsManagerByUsername(String username) {
    return managerRepository.existsByUsernameIgnoreCase(username);
  }

  /**
   * Checks if a username is available or not.
   *
   * @param username the username to be checked
   * @throws ManagerException if username is already in use
   */
  public void checkAvailableUsername(String username) {
    if (existsManagerByUsername(username)) {
      throw new ManagerException(HttpStatus.CONFLICT, "Username: " + username + " already in use");
    }
  }

  private void checkExistsUsername(String username) {
    if (!existsManagerByUsername(username)) {
      throw new ManagerException(HttpStatus.NOT_FOUND, "Username: " + username + " not found");
    }
  }

  private void checkManagerRole(String username, Role role) {
    if (managerRepository.existsByUsernameAndRole(username, role)) {
      throw new ManagerException(HttpStatus.BAD_REQUEST, "This is " + role.value + " account");
    }
  }

  private void checkDifferencePasswords(String newPassword, String oldPassword) {
    if (encoder.matches(newPassword, oldPassword)) {
      throw new ManagerException(HttpStatus.CONFLICT, "The password must not match the old one");
    }
  }
}
