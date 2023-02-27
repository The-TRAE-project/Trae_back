/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.entity.task;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.entity.user.Employee;

/**
 * The Operation class represents a task of a project.
 *
 * @author Vladimir Olennikov
 */
@Entity
@Getter
@Setter
@Table(name = "operations")
public class Operation extends Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @Column(name = "priority")
  private int priority;
  @Column(name = "in_work")
  private boolean inWork;
  @Column(name = "ready_to_acceptance")
  private boolean readyToAcceptance;
  @Column(name = "acceptance_date")
  private LocalDateTime acceptanceDate;
  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "employee_id")
  private Employee employee;
  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "project_id", nullable = false)
  private Project project;
  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "type_work_id", nullable = false)
  private TypeWork typeWork;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Operation operation = (Operation) o;
    return Objects.equals(id, operation.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
