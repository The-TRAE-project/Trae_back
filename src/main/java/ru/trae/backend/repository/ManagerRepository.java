/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.util.Role;

/**
 * Repository for managing {@link Manager}s.
 *
 * @author Vladimir Olennikov
 */
@Repository
public interface ManagerRepository extends PagingAndSortingRepository<Manager, Long> {
  /**
   * Find a {@link Manager} by their username.
   *
   * @param username the username of the {@link Manager}
   * @return the {@link Manager} with the given username
   */
  Optional<Manager> findByUsername(String username);

  /**
   * Check if there is a {@link Manager} with the given username ignoring case.
   *
   * @param username the username to check
   * @return true if a {@link Manager} with the given username exists, false otherwise
   */
  boolean existsByUsernameIgnoreCase(String username);

  /**
   * Updates the password of the Manager with the given username.
   *
   * @param password the new password
   * @param username the username of the Manager
   */
  @Transactional
  @Modifying
  @Query("update Manager m set m.password = ?1 where m.username = ?2")
  void updatePasswordByUsername(String password, String username);

  @Query("select (count(m) > 0) from Manager m where m.id = ?1 and m.accountNonLocked = ?2")
  boolean existsByIdAndAccountNonLocked(Long id, boolean accountNonLocked);

  @Transactional
  @Modifying
  @Query("update Manager m set m.accountNonLocked = ?1, m.dateOfDismissal = ?2 where m.id = ?3")
  void updateAccountNonLockedAndDateOfDismissalById(
      boolean accountNonLocked, LocalDate dateOfDismissal, Long id);

  @Transactional
  @Modifying
  @Query("update Manager m set m.role = ?1 where m.id = ?2")
  void updateRoleById(Role role, Long id);

  @Query("select m from Manager m where m.accountNonLocked = ?1")
  Page<Manager> findByAccountNonLocked(Pageable pageable, boolean accountNonLocked);

  @Query("select m from Manager m where m.role = ?1")
  Page<Manager> findByRole(Pageable pageable, Role role);

  @Query("select m from Manager m where m.accountNonLocked = ?1 and m.role = ?2")
  Page<Manager> findByAccountNonLockedAndRole(
      Pageable pageable, boolean accountNonLocked, Role role);

  @Query("select (count(m) > 0) from Manager m where m.username = ?1 and m.role = ?2")
  boolean existsByUsernameAndRole(String username, Role role);

  @Query("select m.role from Manager m where m.id = ?1")
  Role getRoleById(Long id);

  @Query("select m.role from Manager m where m.username = ?1")
  Role getRoleByUsername(String username);

  @Query("select m.lastName, m.firstName from Manager m where m.username =?1")
  String getLastAndFirstNameByUsername(String username);

  /**
   * Checks if an {@link Manager} exists with the given first, middle and last name
   * (case-insensitive).
   *
   * @param firstName  the first name to search for
   * @param middleName the middle name to search for
   * @param lastName   the last name to search for
   * @return true if an {@link Manager} exists with given first, middle and last name,
   *     false otherwise
   */
  @Query("""
      select (count(m) > 0) from Manager m
      where upper(m.firstName) = upper(?1) and upper(m.middleName) = upper(?2) and\s
      upper(m.lastName) = upper(?3)""")
  boolean existsByFirstMiddleLastNameIgnoreCase(String firstName,
                                                String middleName,
                                                String lastName);
}
