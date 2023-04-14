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

import static ru.trae.backend.util.Constant.NOT_FOUND_CONST;

import java.security.Principal;
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
import ru.trae.backend.dto.manager.AccountInfo;
import ru.trae.backend.dto.manager.ChangeRoleAndStatusReq;
import ru.trae.backend.dto.manager.ChangeRoleAndStatusResp;
import ru.trae.backend.dto.manager.ChangingManagerDataReq;
import ru.trae.backend.dto.manager.ChangingManagerDataResp;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.dto.manager.ManagerShortDto;
import ru.trae.backend.dto.manager.ResetPassResp;
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
            "Manager with ID: " + managerId + NOT_FOUND_CONST.value));
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
            "Manager with username: " + username + NOT_FOUND_CONST.value));
  }

  /**
   * Get the account information of the authenticated user.
   *
   * @param principal The {@link Principal} of the authenticated user.
   * @return The account info of the authenticated user.
   */
  public AccountInfo getAccountInfoAuthUser(Principal principal) {
    Manager m = getManagerByUsername(principal.getName());
    return new AccountInfo(
        m.getId(),
        m.getFirstName(),
        m.getMiddleName() != null ? m.getMiddleName() : null,
        m.getLastName(),
        m.getPhone());
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

  public PageDto<ManagerShortDto> getManagerDtoPage(Pageable managerPage,
                                                    String role, Boolean status) {
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
  public ResetPassResp resetPassword(String username) {
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

    String lastAndFirstName = managerRepository.getLastAndFirstNameByUsername(username);
    return new ResetPassResp(
        lastAndFirstName.substring(0, lastAndFirstName.indexOf(",")),
        lastAndFirstName.substring(lastAndFirstName.indexOf(",") + 1), temporaryRandomPass);
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
          "Account status or new role are not specified");
    }

    if (request.newRole() != null) {
      changeRole(request.managerId(), request.newRole());
    }

    if (request.accountStatus() != null || request.dateOfDismissal() != null) {
      if (Boolean.TRUE.equals(request.accountStatus())) {
        activateAccount(request.managerId());
      } else if (request.dateOfDismissal() != null) {
        deactivateAccount(request.managerId(), request.dateOfDismissal());
      } else {
        throw new ManagerException(HttpStatus.BAD_REQUEST,
            "The date of the user's dismissal is not specified");
      }
    }
  }

  /**
   * Gets the ChangeRoleAndStatusResp object which contains the manager's last name, first name,
   * role, account status, and date of dismissal (if applicable).
   *
   * @param managerId The id of the manager whose information is requested
   * @return The ChangeRoleAndStatusResp object which contains the manager's last name, first name,
   *     role, account status, and date of dismissal (if applicable)
   */
  public ChangeRoleAndStatusResp getChangeRoleAndStatusResp(long managerId) {
    Manager m = getManagerById(managerId);

    return new ChangeRoleAndStatusResp(
        m.getLastName(),
        m.getFirstName(),
        m.getRole().value,
        m.isAccountNonLocked(),
        m.getDateOfDismissal() != null ? m.getDateOfDismissal() : null);
  }

  /**
   * This method activate account.
   *
   * @param managerId the manager id
   * @throws ManagerException in case if manager not found or already activated
   */
  private void activateAccount(long managerId) {
    if (!managerRepository.existsById(managerId)) {
      throw new ManagerException(HttpStatus.NOT_FOUND,
          "The manager with id: " + managerId + NOT_FOUND_CONST.value);
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
  private void deactivateAccount(long managerId, LocalDate dateOfDismissal) {
    if (!managerRepository.existsById(managerId)) {
      throw new ManagerException(HttpStatus.NOT_FOUND,
          "The manager with id: " + managerId + NOT_FOUND_CONST.value);
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
    if (request.firstName() == null && request.middleName() == null
        && request.lastName() == null && request.phone() == null
        && request.oldPassword() == null && request.newPassword() == null) {
      throw new ManagerException(HttpStatus.BAD_REQUEST, "There is no data to update the account");
    }

    Manager m = getManagerByUsername(username);
    updateFirstName(request, m);
    updateMiddleName(request, m);
    updateLastName(request, m);
    updatePhone(request, m);
    changePassword(request, m);

    managerRepository.save(m);
  }

  private void updateFirstName(ChangingManagerDataReq request, Manager m) {
    if (request.firstName() != null) {
      if (!request.firstName().equals(m.getFirstName())) {
        m.setFirstName(request.firstName());
      } else {
        throw new ManagerException(HttpStatus.BAD_REQUEST,
            "The first name must not match an existing one");
      }
    }
  }

  private void updateMiddleName(ChangingManagerDataReq request, Manager m) {
    if (request.middleName() != null) {
      if (!request.middleName().equals(m.getMiddleName())) {
        m.setMiddleName(request.middleName());
      } else {
        throw new ManagerException(HttpStatus.BAD_REQUEST,
            "The middle name must not match an existing one");
      }
    }
  }

  private void updateLastName(ChangingManagerDataReq request, Manager m) {
    if (request.lastName() != null) {
      if (!request.lastName().equals(m.getLastName())) {
        m.setLastName(request.lastName());
      } else {
        throw new ManagerException(HttpStatus.BAD_REQUEST,
            "The last name must not match an existing one");
      }
    }
  }

  private void updatePhone(ChangingManagerDataReq request, Manager m) {
    if (request.phone() != null) {
      if (!request.phone().equals(m.getPhone())) {
        m.setPhone(request.phone());
      } else {
        throw new ManagerException(HttpStatus.BAD_REQUEST,
            "The phone must not match an existing one");
      }
    }
  }

  private void changePassword(ChangingManagerDataReq request, Manager m) {
    if (request.newPassword() != null && request.oldPassword() != null) {
      checkEqualsCurrentPassword(request.oldPassword(), m);
      checkDifferencePasswords(request.newPassword(), m);

      String encodedPass = encoder.encode(request.newPassword());
      m.setPassword(encodedPass);
    } else if (request.oldPassword() != null || request.newPassword() != null) {
      throw new ManagerException(HttpStatus.BAD_REQUEST,
          "To change the password, the old and new password must be entered");
    }
  }

  /**
   * This method is used to get the result of changing data for a manager with a given username.
   *
   * @param username The username of the manager to get the data from.
   * @return A ChangingManagerDataResp object containing the first name, middle name, last name,
   *     and phone number of the given manager.
   */
  public ChangingManagerDataResp getResultOfChangingData(String username) {
    Manager m = getManagerByUsername(username);
    return new ChangingManagerDataResp(
        m.getFirstName(),
        m.getMiddleName() != null ? m.getMiddleName() : null,
        m.getLastName(),
        m.getPhone());
  }

  /**
   * Get a list of roles.
   *
   * @return A list of roles
   */
  public List<String> getRoleList() {
    return List.of(
        Role.ROLE_ADMINISTRATOR.value,
        Role.ROLE_MANAGER.value,
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
  private void changeRole(long managerId, final String newRole) {
    Role currentRole = managerRepository.getRoleById(managerId);

    if (currentRole.value.equals(newRole)) {
      throw new ManagerException(HttpStatus.CONFLICT, "The account already has such a role");
    }

    if (Arrays.stream(Role.values()).anyMatch(r -> r.value.equals(newRole))) {
      Role role = Role.getRoleByValue(newRole);

      managerRepository.updateRoleById(role, managerId);
    } else {
      throw new ManagerException(HttpStatus.BAD_REQUEST,
          "The role: " + newRole + NOT_FOUND_CONST.value);
    }
  }

  /**
   * Gets the role authority of the user.
   *
   * @param principal The principal of the user.
   * @return The role authority of the user. If the user is anonymous, returns "Anonymous".
   */
  public String getRoleAuthUser(Principal principal) {
    if (principal != null) {
      return managerRepository.getRoleByUsername(principal.getName()).value;
    }
    return "Anonymous";
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
      throw new ManagerException(HttpStatus.NOT_FOUND,
          "Username: " + username + NOT_FOUND_CONST.value);
    }
  }

  private void checkManagerRole(String username, Role role) {
    if (managerRepository.existsByUsernameAndRole(username, role)) {
      throw new ManagerException(HttpStatus.BAD_REQUEST, "This is " + role.value + " account");
    }
  }

  private void checkDifferencePasswords(String newPassword, Manager m) {
    if (encoder.matches(newPassword, m.getPassword())) {
      throw new ManagerException(HttpStatus.CONFLICT, "The password must not match the old one");
    }
  }

  private void checkEqualsCurrentPassword(String oldPassword, Manager m) {
    if (!encoder.matches(oldPassword, m.getPassword())) {
      throw new ManagerException(HttpStatus.BAD_REQUEST,
          "The old password must match the stored one");
    }
  }
}
