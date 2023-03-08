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

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
public interface ManagerRepository extends JpaRepository<Manager, Long> {
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
  @Query("update Manager m set m.accountNonLocked = ?1 where m.id = ?2")
  void updateAccountNonLockedById(boolean accountNonLocked, Long id);

  @Transactional
  @Modifying
  @Query("update Manager m set m.role = ?1 where m.id = ?2")
  void updateRoleById(Role role, Long id);
}
