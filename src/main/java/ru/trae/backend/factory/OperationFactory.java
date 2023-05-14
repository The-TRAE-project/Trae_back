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

/**
 * Service class for creation operation objects.
 *
 * @author Vladimir Olennikov
 */
@Component
@RequiredArgsConstructor
public class OperationFactory {
  private final TypeWorkService typeWorkService;
  
  /**
   * Creates a new Operation object with the specified details.
   *
   * @param p          the project to which the operation belongs
   * @param name       the name of the operation
   * @param period     the duration of the operation in hours
   * @param priority   the priority of the operation
   * @param start      the start date/time of the operation
   * @param ready      a flag indicating if the operation is ready for acceptance
   * @param typeWorkId the ID of the type of work associated with the operation
   * @return a newly created Operation object with the specified details
   */
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
  
  /**
   * Creates a shipment operation for the specified project with the given priority.
   *
   * @param p        the project for which the shipment operation is created
   * @param priority the priority of the shipment operation
   * @return a newly created Operation object representing the shipment operation
   */
  public Operation createShipmentOp(Project p, int priority) {
    //у типа работы "Отгрузка" ID всегда = 1
    TypeWork shipment = typeWorkService.getTypeWorkById(1);
    
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
