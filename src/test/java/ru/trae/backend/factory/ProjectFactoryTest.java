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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.trae.backend.service.OperationService.SHIPMENT_PERIOD;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.service.ManagerService;
import ru.trae.backend.util.Util;

@ExtendWith(MockitoExtension.class)
class ProjectFactoryTest {
  @Mock
  private ManagerService managerService;
  @InjectMocks
  private ProjectFactory projectFactory;

  @Test
  void create_ShouldCreateProjectWithSpecifiedDetails() {
    //given
    int number = 100;
    String name = "test_project";
    LocalDateTime currentDate = LocalDateTime.now();
    LocalDateTime plannedEndDate = currentDate.plusDays(10);
    int operationCount = 5;
    String customer = "test_customer";
    String comment = "test_comment";
    String authUsername = "test_user";
    Manager m = new Manager();

   //when
    when(managerService.getManagerByUsername(authUsername)).thenReturn(m);

    Project p = projectFactory.create(number, name, currentDate, plannedEndDate,
        operationCount, customer, comment, authUsername);

    //then
    assertEquals(number, p.getNumber());
    assertEquals(name, p.getName());
    assertEquals(currentDate, p.getStartDate());
    assertEquals(null, p.getStartFirstOperationDate());
    assertEquals(plannedEndDate, p.getPlannedEndDate());
    assertEquals(plannedEndDate, p.getEndDateInContract());
    assertEquals(null, p.getRealEndDate());
    assertEquals((int) HOURS.between(p.getStartDate(), p.getPlannedEndDate()), p.getPeriod());
    int operationPeriod = Util.calculateOperationPeriod(p.getPeriod() - SHIPMENT_PERIOD, operationCount);
    assertEquals(operationPeriod, p.getOperationPeriod());
    assertEquals(false, p.isEnded());
    assertEquals(m, p.getManager());
    assertEquals(customer, p.getCustomer());
    assertEquals(comment, p.getComment());
    verify(managerService, times(1)).getManagerByUsername(authUsername);
  }
}
