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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.trae.backend.entity.user.Manager;

/**
 * Represents a Project in the system.
 *
 * @author Vladimir Olennikov
 */
@Entity
@Getter
@Setter
@Table(name = "projects")
public class Project extends Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @Min(1)
  @Max(999)
  @NotNull
  @Column(name = "number", nullable = false)
  private int number;
  @Max(value = 8760,
      message = "The operation period cannot be more than the number (8760) of hours per year")
  @Column(name = "operation_period", nullable = false)
  private int operationPeriod;
  @Column(name = "comment", columnDefinition = "varchar(1000)")
  private String comment;
  @Size(min = 3, max = 200)
  @NotNull
  @Column(name = "customer", nullable = false, columnDefinition = "varchar(200)")
  private String customer;
  @Column(name = "start_first_operation_date")
  private LocalDateTime startFirstOperationDate;
  @Column(name = "end_date_in_contract")
  private LocalDateTime endDateInContract;
  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "manager_id", nullable = false)
  private Manager manager;
  @ToString.Exclude
  @OneToMany(mappedBy = "project", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<Operation> operations = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Project project = (Project) o;
    return Objects.equals(id, project.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
