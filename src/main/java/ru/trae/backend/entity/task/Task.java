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
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Abstract class representing a task.
 *
 * @author Vladimir Olennikov
 */
@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class Task {
  @Size(min = 2, max = 500)
  @Column(name = "name", columnDefinition = "varchar(500)")
  private String name;
  @Column(name = "start_date")
  private LocalDateTime startDate;
  @Column(name = "planned_end_date")
  private LocalDateTime plannedEndDate;
  @Column(name = "real_end_date")
  private LocalDateTime realEndDate;
  @Max(8760)
  @Column(name = "period", nullable = false)
  private int period;
  @Column(name = "is_ended")
  private boolean isEnded;

}
