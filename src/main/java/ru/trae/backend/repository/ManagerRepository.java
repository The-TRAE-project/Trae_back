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
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.user.Manager;

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
   * Check if there is a {@link Manager} with the given email ignoring case.
   *
   * @param email the email to check
   * @return true if a {@link Manager} with the given email exists, false otherwise
   */
  boolean existsByEmailIgnoreCase(String email);

  /**
   * Check if there is a {@link Manager} with the given username ignoring case.
   *
   * @param username the username to check
   * @return true if a {@link Manager} with the given username exists, false otherwise
   */
  boolean existsByUsernameIgnoreCase(String username);

}
