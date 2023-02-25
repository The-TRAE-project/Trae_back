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

import java.time.LocalDateTime;
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
import ru.trae.backend.entity.user.Employee;

/**
 * The TimeControl entity represents a record of time control for a given employee and
 * working shift.
 * It contains information about the employee's arrival and departure time, whether the employee
 * is on shift and if the shift should be automatically closed.
 *
 * @author Vladimir Olennikov
 */
@Entity
@Getter
@Setter
@Table(name = "time_controls")
public class TimeControl {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  private boolean isOnShift;
  private boolean autoClosingShift;
  private LocalDateTime arrival;
  private LocalDateTime departure;
  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "employee_id", nullable = false)
  private Employee employee;
  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "working_shift_id", nullable = false)
  private WorkingShift workingShift;
}
