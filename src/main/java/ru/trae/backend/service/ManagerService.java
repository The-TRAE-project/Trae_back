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

import java.time.LocalDateTime;
import java.util.List;
import liquibase.repackaged.org.apache.commons.lang3.RandomStringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.manager.ManagerCredentials;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.dto.mapper.ManagerDtoMapper;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.exceptionhandler.exception.ManagerException;
import ru.trae.backend.repository.ManagerRepository;
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
  public ManagerCredentials saveNewManager(ManagerRegisterDto dto) {
    Manager m = new Manager();

    String temporaryRandomPass = RandomStringUtils.randomAlphanumeric(6);
    String encodedPass = encoder.encode(temporaryRandomPass);

    m.setFirstName(dto.firstName());
    m.setMiddleName(dto.middleName());
    m.setLastName(dto.lastName());
    m.setPhone(dto.phone());
    m.setUsername(dto.username());
    m.setPassword(encodedPass);
    m.setRole(Role.ROLE_MANAGER);
    m.setDateOfRegister(LocalDateTime.now());

    m.setEnabled(true);
    m.setAccountNonExpired(true);
    m.setAccountNonLocked(true);
    m.setCredentialsNonExpired(true);

    managerRepository.save(m);

    return new ManagerCredentials(m.getUsername(), temporaryRandomPass);
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
   * Retrieves all managers from the repository and returns them as a list of ManagerDto objects.
   *
   * @return List of ManagerDto objects
   */
  public List<ManagerDto> getAllManagers() {
    return managerRepository.findAll()
            .stream()
            .map(managerDtoMapper)
            .toList();
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
   * @param credentials This is a ManagerCredentials object that contains the manager's username.
   * @return A ManagerCredentials object containing the manager's username and the new, temporary
   *         password.
   */
  public ManagerCredentials resetPassword(ManagerCredentials credentials) {
    String username = credentials.username();
    checkExistsUsername(username);

    String temporaryRandomPass = RandomStringUtils.randomAlphanumeric(6);
    String encodedPass = encoder.encode(temporaryRandomPass);

    managerRepository.updatePasswordByUsername(encodedPass, username);

    jwtUtil.deletePayloadRandomPieces(username);

    return new ManagerCredentials(username, temporaryRandomPass);
  }

  public ManagerDto convertFromManager(Manager manager) {
    return managerDtoMapper.apply(manager);
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
}
