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
import org.springframework.data.jpa.repository.Query;
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
  @Query(value = """
      select * from time_controls as tc
              inner join employees e on e.id = tc.employee_id
              inner join working_shifts ws on ws.id = tc.working_shift_id
      where tc.is_on_shift = true and ws.is_ended = false and e.id = ?1""", nativeQuery = true)
  TimeControl findByEmployeeIdAndIsOnShiftTrueAndWorkingShiftIsEndedFalse(Long id);
}
