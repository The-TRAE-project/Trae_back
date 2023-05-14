/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.factory;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.service.TypeWorkService;

@Component
@RequiredArgsConstructor
public class OperationFactory {
  private final TypeWorkService typeWorkService;
  
  public Operation create(Project p, String name, int period, int priority,
                          LocalDateTime start,
                          boolean ready, long typeWorkId
  ) {
    Operation o = new Operation();
    
    o.setProject(p);
    o.setName(name);
    o.setPeriod(period);
    o.setPriority(priority);
    o.setStartDate(start);
    o.setPlannedEndDate(start != null ? start.plusHours(period) : null);
    o.setAcceptanceDate(null);
    o.setEnded(false);
    o.setInWork(false);
    o.setReadyToAcceptance(ready);
    o.setTypeWork(typeWorkService.getTypeWorkById(typeWorkId));
    
    return o;
  }
  
  public Operation createShipmentOp(Project p, int priority) {
    TypeWork shipment = typeWorkService.getTypeWorkByName("Отгрузка");
    
    return create(
        p,
        shipment.getName(),
        24, priority,
        null,
        false,
        shipment.getId()
    );
  }
}
