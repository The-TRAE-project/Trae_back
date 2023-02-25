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
import javax.persistence.MappedSuperclass;
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
  private String name;
  private String description;
  private LocalDateTime startDate;
  private LocalDateTime plannedEndDate;
  private LocalDateTime realEndDate;
  private int period;
  private boolean isEnded;

}
