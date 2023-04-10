/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.trae.backend.entity.task.Operation;

/**
 * This class represents a TypeWork entity.
 * It contains information about the type and a list of operations it is associated with.
 *
 * @author Vladimir Olennikov
 */
@Entity
@Getter
@Setter
@Table(name = "types")
public class TypeWork {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @Size(min = 2, max = 100)
  @Column(name = "name", unique = true, nullable = false, columnDefinition = "varchar(100)")
  private String name;
  @Column(name = "is_active", nullable = false)
  private boolean isActive;
  @ToString.Exclude
  @OneToMany(mappedBy = "typeWork", fetch = FetchType.LAZY)
  private List<Operation> operations = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TypeWork typeWork = (TypeWork) o;
    return Objects.equals(id, typeWork.id) && Objects.equals(name, typeWork.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

  @Override
  public String toString() {
    return "TypeWork{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", isActive=" + isActive +
        '}';
  }
}
