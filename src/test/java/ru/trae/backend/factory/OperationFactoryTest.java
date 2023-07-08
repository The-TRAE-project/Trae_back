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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.service.TypeWorkService;

@ExtendWith(MockitoExtension.class)
 class OperationFactoryTest {
  @Mock
  private TypeWorkService typeWorkService;
  @InjectMocks
  private OperationFactory operationFactory;

  @Test
   void create_ShouldCreateOperationWithSpecifiedDetails() {
    //given
    Project p = new Project();
    String name = "test_operation";
    int period = 8;
    int priority = 1;
    LocalDateTime start = LocalDateTime.now();
    boolean ready = true;
    long typeWorkId = 1;

    TypeWork tw = new TypeWork();
    when(typeWorkService.getTypeWorkById(typeWorkId)).thenReturn(tw);

    //when
    Operation o = operationFactory.create(p, name, period, priority, start, ready, typeWorkId);

    //then
    assertEquals(p, o.getProject());
    assertEquals(name, o.getName());
    assertEquals(period, o.getPeriod());
    assertEquals(priority, o.getPriority());
    assertEquals(start, o.getStartDate());
    assertEquals(start.plusHours(period), o.getPlannedEndDate());
    assertEquals(null, o.getAcceptanceDate());
    assertEquals(false, o.isEnded());
    assertEquals(false, o.isInWork());
    assertEquals(ready, o.isReadyToAcceptance());
    assertEquals(tw, o.getTypeWork());

    verify(typeWorkService, times(1)).getTypeWorkById(typeWorkId);
  }

  @Test
   void createShipmentOp_ShouldCreateShipmentOperationWithSpecifiedDetails() {
    //given
    Project p = new Project();
    int priority = 1;

    TypeWork shipmentTypeWork = new TypeWork();
    shipmentTypeWork.setId(1L);
    shipmentTypeWork.setName("shipment");
    when(typeWorkService.getTypeWorkById(1)).thenReturn(shipmentTypeWork);

    //when
    Operation o = operationFactory.createShipmentOp(p, priority);

    //then
    assertEquals(p, o.getProject());
    assertEquals("shipment", o.getName());
    assertEquals(24, o.getPeriod());
    assertEquals(priority, o.getPriority());
    assertEquals(null, o.getStartDate());
    assertEquals(false, o.isReadyToAcceptance());
    assertEquals(shipmentTypeWork, o.getTypeWork());

    verify(typeWorkService, times(2)).getTypeWorkById(1);
  }
}
