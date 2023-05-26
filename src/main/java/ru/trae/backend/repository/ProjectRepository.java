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
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.trae.backend.dto.project.ChangingCommonDataResp;
import ru.trae.backend.dto.project.ChangingEndDatesResp;
import ru.trae.backend.entity.task.Project;

/**
 * This repository provides the necessary CRUD operations for working with {@link Project} objects.
 * Additionally, it provides custom query methods to find available projects by type work.
 *
 * @author Vladimir Olennikov
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
  
  @Query("""
      select p from Project p inner join p.operations operations
      where p.isEnded = false and operations.priority = (select max(o.priority)\s
      from Operation o where o.project.id = p.id) and operations.inWork = true""")
  Page<Project> findLastByIsEndedAndOpPriorityAndInWorkTrue(Pageable pageable);
  
  @Query("""
      select p from Project p inner join p.operations o
      where p.isEnded = false and o.priority = ?1 and o.readyToAcceptance = true""")
  Page<Project> findFirstByIsEndedAndOpPriorityAndReadyToAcceptance(
      int priority, Pageable pageable);
  
  @Query("""
      select p from Project p inner join p.operations operations
      where p.isEnded = false and\s
      (operations.priority = (select max(o.priority)\s
      from Operation o where o.project.id = p.id) and operations.inWork = true) or\s
      (operations.priority = (select min(o.priority)\s
      from Operation o where o.project.id = p.id) and operations.readyToAcceptance = true)""")
  Page<Project> findFirstAndLast(Pageable pageable);
  
  @Query("""
      select p from Project p left join p.operations operations
      where p.isEnded = false\s
      and (operations.inWork = true or operations.readyToAcceptance = true)\s
      and operations.plannedEndDate < ?1""")
  Page<Project> findProjectsWithOverdueCurrentOperation(
      LocalDateTime currentDate, Pageable pageable);
  
  @Query("""
      select p from Project p
      where p.isEnded = false and exists (
                                          select o from Operation o\s
                                          where o.project.id = p.id and o.inWork = true)""")
  Page<Project> findByIsEndedFalseAndAnyOperationInWork(Pageable pageable);
  
  @Query("""
      select p from Project p
      where p.isEnded = false and\s
      (p.plannedEndDate > p.endDateInContract or current_timestamp > p.endDateInContract)""")
  Page<Project> findOverdueProjects(Pageable pageable);
  
  @Query(value = """
      select p.* from projects p where (cast(p.start_date as date) between ?1 and ?2)\s
      or (cast(p.end_date_in_contract as date) between ?1 and ?2)\s
      or (cast(p.planned_end_date as date) between ?1 and ?2)
      or (?1 between cast(p.start_date as date) and cast(p.end_date_in_contract as date))
      or (?1 between cast(p.start_date as date) and cast(p.planned_end_date as date))""",
      nativeQuery = true)
  List<Project> findProjectsForPeriod(LocalDate startOfPeriod, LocalDate endOfPeriod);
  
  @Transactional
  @Modifying
  @Query("update Project p set p.plannedEndDate = ?1 where p.id = ?2")
  void updatePlannedEndDateById(LocalDateTime plannedEndDate, Long id);
  
  @Query("""
      select p from Project p inner join p.operations operations
      where p.isEnded = false and operations.readyToAcceptance = true\s
      and operations.typeWork.id = ?1""")
  List<Project> findAvailableProjectsByTypeWork(long typeWorkId);
  
  @Transactional
  @Modifying
  @Query("update Project p set p.isEnded = ?1, p.realEndDate = ?2 where p.id = ?3")
  void updateIsEndedAndRealEndDateById(boolean isEnded, LocalDateTime realEndDate, Long id);
  
  @Query("select p from Project p where p.isEnded = ?1")
  Page<Project> findByIsEnded(boolean isEnded, Pageable pageable);
  
  @Query("select p from Project p where p.number = ?1")
  Page<Project> findByNumber(int number, Pageable pageable);
  
  @Query("select p from Project p where upper(p.customer) like %?1%")
  Page<Project> findByCustomerLikeIgnoreCase(String customer, Pageable pageable);
  
  ChangingCommonDataResp findChangedCommonDataById(long projectId);
  
  ChangingEndDatesResp findChangedPlannedEndDateById(long projectId);
  
  @Query("select p from Project p where p.id = "
      + "(select o.project.id from Operation o where o.id = ?1)")
  Optional<Project> findByOperationId(long id);
  
  @Transactional
  @Modifying
  @Query(value = """
      update projects
      set start_first_operation_date = (select o.acceptance_date
                                        from operations as o
                                        where o.id = ?1)
      where id = (select o.project_id
                  from operations as o
                  where o.id = ?1 and o.in_work = true)""", nativeQuery = true)
  void updateStartFirstOperationDateByOperationId(long operationId);
}
