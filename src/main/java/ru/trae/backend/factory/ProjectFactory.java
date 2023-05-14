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

import static java.time.temporal.ChronoUnit.HOURS;
import static ru.trae.backend.service.OperationService.SHIPMENT_PERIOD;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.service.ManagerService;
import ru.trae.backend.util.Util;

@Component
@RequiredArgsConstructor
public class ProjectFactory {
  private final ManagerService managerService;
  
  public Project create(int number,
                        String name,
                        LocalDateTime currentDate,
                        LocalDateTime plannedEndDate,
                        int operationCount,
                        String customer,
                        String comment,
                        String authUsername) {
    Project p = new Project();
    
    p.setNumber(number);
    p.setName(name);
    p.setStartDate(currentDate);
    p.setStartFirstOperationDate(null);
    p.setPlannedEndDate(plannedEndDate);
    p.setEndDateInContract(plannedEndDate);
    p.setRealEndDate(null);
    p.setPeriod((int) HOURS.between(p.getStartDate(), p.getPlannedEndDate()));
    int operationPeriod =
        Util.calculateOperationPeriod(p.getPeriod() - SHIPMENT_PERIOD, operationCount);
    p.setOperationPeriod(operationPeriod);
    p.setEnded(false);
    p.setManager(managerService.getManagerByUsername(authUsername));
    p.setCustomer(customer);
    p.setComment(comment);
    
    return p;
  }
}
