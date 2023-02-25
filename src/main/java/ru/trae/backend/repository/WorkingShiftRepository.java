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
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.WorkingShift;

/**
 * Repository interface for WorkingShift entity which enables basic CRUD operations.
 *
 * @author Vladimir Olennikov
 */
@Repository
public interface WorkingShiftRepository extends JpaRepository<WorkingShift, Long> {
  /**
   * Check if there is an active WorkingShift.
   *
   * @return true if there is an active WorkingShift, false otherwise.
   */
  boolean existsByIsEndedFalse();

  /**
   * Find the active WorkingShift.
   *
   * @return active WorkingShift if there is one, null otherwise.
   */
  WorkingShift findByIsEndedFalse();

  /**
   * Check if an employee is on shift for the current active WorkingShift.
   *
   * @param isOnShift boolean condition for the employee's shift status
   * @param id        the id of the employee
   * @return true if the employee is on shift, false otherwise.
   */
  boolean existsByIsEndedFalseAndTimeControls_IsOnShiftAndTimeControls_Employee_Id(
          boolean isOnShift, Long id);

}
