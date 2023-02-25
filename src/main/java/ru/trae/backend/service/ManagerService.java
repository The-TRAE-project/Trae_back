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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.jwt.JwtResponse;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.dto.mapper.ManagerDtoMapper;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.exceptionhandler.exception.ManagerException;
import ru.trae.backend.repository.ManagerRepository;
import ru.trae.backend.util.Role;
import ru.trae.backend.util.jwt.JWTUtil;

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
  private final JWTUtil jwtUtil;

  /**
   * Method for saving new manager to the database.
   * Generates access and refresh tokens for this manager.
   *
   * @param dto contains data for creating a new manager
   * @return refresh and accesses tokens
   */
  public JwtResponse saveNewManager(ManagerRegisterDto dto) {
    Manager m = new Manager();

    String encodedPass = encoder.encode(dto.password());
    m.setFirstName(dto.firstName());
    m.setMiddleName(dto.middleName());
    m.setLastName(dto.lastName());
    m.setPhone(dto.phone());
    m.setEmail(dto.email());
    m.setUsername(dto.username());
    m.setPassword(encodedPass);
    m.setRole(Role.ROLE_MANAGER); //TODO изменить на юзер по дефолту = m.setRole(Role.ROLE_USER);
    m.setDateOfRegister(LocalDateTime.now());

    m.setEnabled(true);
    m.setAccountNonExpired(true);
    m.setAccountNonLocked(true);
    m.setCredentialsNonExpired(true);

    managerRepository.save(m);

    String accessToken = jwtUtil.generateAccessToken(m.getUsername());
    String refreshToken = jwtUtil.generateRefreshToken(m.getUsername());

    return new JwtResponse(accessToken, refreshToken);
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

  public ManagerDto convertFromManager(Manager manager) {
    return managerDtoMapper.apply(manager);
  }

  public boolean existsManagerByEmail(String email) {
    return managerRepository.existsByEmailIgnoreCase(email);
  }

  public boolean existsManagerByUsername(String username) {
    return managerRepository.existsByUsernameIgnoreCase(username);
  }

  /**
   * Checks if a manager already exists with the given email.
   *
   * @param email the email to check
   * @throws ManagerException if a manager with the same email already exists
   */
  public void checkAvailableEmail(String email) {
    if (existsManagerByEmail(email)) {
      throw new ManagerException(HttpStatus.CONFLICT, "Email: " + email + " already in use");
    }
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

}
