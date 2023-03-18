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

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.trae.backend.entity.task.Operation;

/**
 * Repository interface for the {@link Operation} entity.
 *
 * @author Vladimir Olennikov
 */
@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
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
  @Query("update Operation o set o.isEnded = ?1 where o.id = ?2")
  void updateIsEndedById(boolean isEnded, Long id);
}
