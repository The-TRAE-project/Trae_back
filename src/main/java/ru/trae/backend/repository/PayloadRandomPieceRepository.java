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
import ru.trae.backend.entity.PayloadRandomPiece;

/**
 * Repository interface for {@link PayloadRandomPiece}.
 *
 * @author Vladimir Olennikov
 */
@Repository
public interface PayloadRandomPieceRepository extends JpaRepository<PayloadRandomPiece, Long> {
  /**
   * Checks if the given username is already used.
   *
   * @param username the username to check
   * @return true if the given username is already used
   */
  boolean existsByUsernameIgnoreCase(String username);

  /**
   * Updates the uuid for a given username.
   *
   * @param uuid     the new uuid
   * @param username the username
   */
  @Transactional
  @Modifying
  @Query("update PayloadRandomPiece p set p.uuid = ?1 where upper(p.username) = upper(?2)")
  void updateUuidByUsernameIgnoreCase(String uuid, String username);

  /**
   * Gets the payload random piece with the given username ignoring the case.
   *
   * @param username the username
   * @return the payload random piece with the given username ignoring the case
   */
  Optional<PayloadRandomPiece> findByUsernameIgnoreCase(String username);

}
