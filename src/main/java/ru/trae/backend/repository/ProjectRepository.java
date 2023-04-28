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

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.trae.backend.dto.project.ChangingCommonDataResp;
import ru.trae.backend.dto.project.ChangingPlannedEndDateResp;
import ru.trae.backend.entity.task.Project;

/**
 * This repository provides the necessary CRUD operations for working with {@link Project} objects.
 * Additionally, it provides custom query methods to find available projects by type work.
 *
 * @author Vladimir Olennikov
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
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

  @Query("select p from Project p where p.isEnded = ?1 and p.number = ?2")
  Page<Project> findByIsEndedAndNumber(boolean isEnded, int number, Pageable pageable);

  @Query("select p from Project p where p.isEnded = ?1")
  Page<Project> findByIsEnded(boolean isEnded, Pageable pageable);

  @Query("select p from Project p where p.number = ?1")
  Page<Project> findByNumber(int number, Pageable pageable);

  ChangingCommonDataResp findChangedCommonDataById(long projectId);

  ChangingPlannedEndDateResp findChangedPlannedEndDateById(long projectId);
}
