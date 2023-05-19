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
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.trae.backend.entity.WorkingShift;
import ru.trae.backend.projection.WorkingShiftEmployeePercentage;

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
  @Query(value = "select exists ("
      + "select 1 from time_controls tc "
      + "inner join employees e on tc.employee_id = e.id "
      + "inner join working_shifts ws on ws.id = tc.working_shift_id "
      + "where ws.is_ended = false and tc.is_on_shift = :isOnShift and e.id = :id)",
      nativeQuery = true)
  boolean existsEmpOnShift(@Param("isOnShift") boolean isOnShift, @Param("id") Long id);
  
  @Query(value = """
      select employee_id,
             cast(ws.start_shift as date) as shift_date,
             round(sum(extract(epoch from
                               (case
                                    when cast(ws.start_shift as date) + time '18:00' < tc.departure
                                        then cast(ws.start_shift as date) + time '18:00'
                                    else tc.departure end) -
                               (case
                                    when cast(ws.start_shift as date) + time '08:00' > tc.arrival
                                        then cast(ws.start_shift as date) + time '08:00'
                                    else tc.arrival end)) / 60) / 6, 1) as percent_of_shift
      from working_shifts ws
               inner join time_controls tc on ws.id = tc.working_shift_id
      group by tc.employee_id, tc.auto_closing_shift, ws.is_ended, ws.start_shift
      having tc.auto_closing_shift = false
         and ws.is_ended = false
         and cast(ws.start_shift as date) between ?1 and ?2""", nativeQuery = true)
  List<WorkingShiftEmployeePercentage> getWorkingShiftsDates(
      LocalDate startOfPeriod, LocalDate endOfPeriod);
}
