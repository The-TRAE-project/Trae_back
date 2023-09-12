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
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.projection.OperationIdNameProjectNumberDto;

/**
 * Repository interface for the {@link Operation} entity.
 *
 * @author Vladimir Olennikov
 */
@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
  @Query("select o from Operation o where o.id in ?1")
  List<Operation> findByIdIn(Collection<Long> ids);
  
  @Query("select (count(o) > 0) from Operation o where o.id = ?1 and o.typeWork.id = ?2")
  boolean existsByTypeWorkIdEqualsShipment(long operationId, long shipmentId);
  
  /**
   * Retrieve all operations associated with a given project.
   *
   * @param projectId the ID of the project to find operations for
   * @return a list of all operations associated with the given project
   */
  @Query("select o from Operation o where o.project.id = ?1")
  List<Operation> findByProjectId(long projectId);
  
  /**
   * Retrieve all operations that are currently in-work for a given employee.
   *
   * @param employeeId the ID of the employee to find operations for
   * @return a list of all operations that are currently in-work for the given employee
   */
  @Query("select o from Operation o where o.inWork = true and o.employee.id = ?1 "
      + "order by o.acceptanceDate")
  List<Operation> findByEmpIdAndInWork(long employeeId);
  
  /**
   * Returns a list of operations based on the project id, ordered by priority in ascending order.
   *
   * @param projectId the id of the project
   * @return a list of operations
   */
  @Query("select o from Operation o where o.project.id = ?1 order by o.priority")
  List<Operation> findByProjectIdOrderByPriorityAsc(Long projectId);
  
  @Transactional
  @Modifying
  @Query("update Operation o set o.priority = ?1 where o.id = ?2")
  void updatePriorityById(int priority, Long id);
  
  @Transactional
  @Modifying
  @Query("""
      update Operation o\s
      set o.realEndDate = ?1, o.isEnded = ?2, o.readyToAcceptance = ?3, o.inWork = ?4
      where o.id = ?5""")
  void updateRealEndDateAndIsEndedAndReadyToAcceptanceAndInWorkById(
      LocalDateTime realEndDate,
      boolean isEnded,
      boolean readyToAcceptance,
      boolean inWork,
      Long id);
  
  @Query("""
      select (count(o) > 0) from Operation o
      where o.id = ?1 and (o.isEnded = ?2 or o.inWork = ?3 or o.readyToAcceptance = ?4)""")
  boolean existsByIdOrIsEndedOrInWorkOrReadyToAcceptance(
      Long id, boolean isEnded, boolean inWork, boolean readyToAcceptance);
  
  @Modifying
  @Query("delete from Operation o where o.id = ?1")
  void deleteById(long operationId);
  
  @Query("select (count(o) > 0) from Operation o where o.id = ?1 and o.priority = ?2")
  boolean existsByIdAndPriority(Long id, int priority);
  
  @Query(value = """
      select o.id, o.name, (select p.number from projects p where p.id = o.project_id)\s
      as project_number from operations o where\s
      o.employee_id is not NULL and\s
      (cast(?1 as date) is null and cast(?2 as date) is null)\s
      or (cast(o.start_date as date) between ?1 and ?2)\s
      or (cast(o.planned_end_date as date) between ?1 and ?2)\s
      or (cast(o.real_end_date as date) between ?1 and ?2)
      or (?1 between cast(o.start_date as date) and cast(o.real_end_date as date))
      or (?1 between cast(o.start_date as date) and cast(o.planned_end_date as date))""",
      nativeQuery = true)
  List<OperationIdNameProjectNumberDto> findByPeriod(
      LocalDate startOfPeriod, LocalDate endOfPeriod);
  
  @Query(value = """
      select o.id, o.name, (select p.number from projects p where p.id = o.project_id)\s
      as project_number from operations o\s
      where o.employee_id is not NULL and\s
      o.project_id in (select p.id from projects p where p.id in (?3))\s
      and ((cast(?1 as date) is null and cast(?2 as date) is null)\s
      or (cast(o.start_date as date) between ?1 and ?2)\s
      or (cast(o.planned_end_date as date) between ?1 and ?2)\s
      or (cast(o.real_end_date as date) between ?1 and ?2)
      or (?1 between cast(o.start_date as date) and cast(o.real_end_date as date))
      or (?1 between cast(o.start_date as date) and cast(o.planned_end_date as date)))""",
      nativeQuery = true)
  List<OperationIdNameProjectNumberDto> findByPeriodAndProjectIds(
      LocalDate startOfPeriod, LocalDate endOfPeriod, Set<Long> projectIds);
  
  @Query(value = """
      select o.id, o.name, (select p.number from projects p where p.id = o.project_id)\s
      as project_number from operations o\s
      where o.employee_id in (select e.id from employees e where e.id in (?3))\s
      and ((cast(?1 as date) is null and cast(?2 as date) is null)\s
      or (cast(o.start_date as date) between ?1 and ?2)\s
      or (cast(o.planned_end_date as date) between ?1 and ?2)\s
      or (cast(o.real_end_date as date) between ?1 and ?2)
      or (?1 between cast(o.start_date as date) and cast(o.real_end_date as date))
      or (?1 between cast(o.start_date as date) and cast(o.planned_end_date as date)))""",
      nativeQuery = true)
  List<OperationIdNameProjectNumberDto> findByPeriodAndEmployeeIds(
      LocalDate startOfPeriod, LocalDate endOfPeriod, Set<Long> employeeIds);
  
  @Query(value = """
      select o.id, o.name, (select p.number from projects p where p.id = o.project_id)\s
      as project_number from operations o\s
      where o.employee_id in (select e.id from employees e where e.id in (?3))\s
      and o.project_id in (select p.id from projects p where p.id in (?4))\s
      and ((cast(?1 as date) is null and cast(?2 as date) is null)\s
      or (cast(o.start_date as date) between ?1 and ?2)\s
      or (cast(o.planned_end_date as date) between ?1 and ?2)\s
      or (cast(o.real_end_date as date) between ?1 and ?2)
      or (?1 between cast(o.start_date as date) and cast(o.real_end_date as date))
      or (?1 between cast(o.start_date as date) and cast(o.planned_end_date as date)))""",
      nativeQuery = true)
  List<OperationIdNameProjectNumberDto> findByPeriodAndEmployeeAndProjectIds(
      LocalDate startOfPeriod, LocalDate endOfPeriod, Set<Long> employeeIds, Set<Long> projectIds);
  
  @Query("select o from Operation o where o.id in (?1)")
  List<Operation> findOpsByIds(Set<Long> operationIds);
}
