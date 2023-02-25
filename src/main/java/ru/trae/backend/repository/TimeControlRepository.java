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

import org.springframework.data.jpa.repository.JpaRepository;
import ru.trae.backend.entity.TimeControl;

/**
 * JpaRepository interface for the TimeControl entity.
 *
 * @author Vladimir Olennikov
 */
public interface TimeControlRepository extends JpaRepository<TimeControl, Long> {
  /**
   * Finds the TimeControl object with the given employee ID, isOnShift set to true,
   * and workingShift set to false.
   *
   * @param id The ID of the employee to search for
   * @return The TimeControl object with the given parameters
   */
  TimeControl findByEmployee_IdAndIsOnShiftTrueAndWorkingShift_IsEndedFalse(Long id);

}
