/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.dto.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import ru.trae.backend.dto.operation.OperationDto;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;

class OperationDtoMapperTest {
  @Test
  void apply_WhenOperationEnded_ShouldMapToOperationDtoWithActualPeriod() {
    //given
    Operation o = new Operation();
    o.setId(1L);
    o.setPriority(0);
    o.setName("test_operation");
    o.setStartDate(LocalDateTime.now().minusHours(2));
    o.setAcceptanceDate(LocalDateTime.now().minusHours(1));
    o.setPlannedEndDate(LocalDateTime.now().plusHours(2));
    o.setRealEndDate(LocalDateTime.now());
    o.setPeriod(4);
    o.setEnded(true);
    o.setInWork(true);
    o.setReadyToAcceptance(false);
    o.setTypeWork(new TypeWork());
    
    Project p = new Project();
    p.setNumber(100);
    o.setProject(p);
    
    Employee e = new Employee();
    e.setFirstName("test_first_name");
    e.setLastName("test_last_name");
    o.setEmployee(e);
    
    OperationDtoMapper operationDtoMapper = new OperationDtoMapper();
    
    //when
    OperationDto operationDto = operationDtoMapper.apply(o);
    
    //then
    assertEquals(o.getId(), operationDto.id());
    assertEquals(o.getPriority(), operationDto.priority());
    assertEquals(o.getName(), operationDto.name());
    assertEquals(o.getStartDate(), operationDto.startDate());
    assertEquals(o.getAcceptanceDate(), operationDto.acceptanceDate());
    assertEquals(o.getPlannedEndDate(), operationDto.plannedEndDate());
    assertEquals(o.getRealEndDate(), operationDto.realEndDate());
    assertEquals(o.getPeriod(), operationDto.period());
    assertEquals(Math.toIntExact(o.getRealEndDate().toLocalTime().getHour()
        - o.getStartDate().toLocalTime().getHour()), operationDto.actualPeriod());
    assertEquals(o.isEnded(), operationDto.isEnded());
    assertEquals(o.isInWork(), operationDto.inWork());
    assertEquals(o.isReadyToAcceptance(), operationDto.readyToAcceptance());
    assertEquals(o.getProject().getNumber(), operationDto.projectNumber());
    assertEquals(o.getTypeWork().getName(), operationDto.typeWorkName());
    assertEquals(o.getEmployee().getFirstName(), operationDto.employeeFirstLastNameDto().firstName());
    assertEquals(o.getEmployee().getLastName(), operationDto.employeeFirstLastNameDto().lastName());
  }
  
  @Test
  void apply_WhenOperationNotEnded_ShouldMapToOperationDtoWithNullActualPeriod() {
    //given
    Operation o = new Operation();
    o.setId(2L);
    o.setPriority(1);
    o.setName("another_operation");
    o.setStartDate(LocalDateTime.now().minusHours(4));
    o.setAcceptanceDate(null);
    o.setPlannedEndDate(LocalDateTime.now().plusHours(6));
    o.setRealEndDate(null);
    o.setPeriod(10);
    o.setEnded(false);
    o.setInWork(true);
    o.setReadyToAcceptance(true);
    o.setTypeWork(new TypeWork());
    
    Project p = new Project();
    p.setNumber(200);
    o.setProject(p);
    
    OperationDtoMapper operationDtoMapper = new OperationDtoMapper();
    
    //when
    OperationDto dto = operationDtoMapper.apply(o);
    
    //then
    assertEquals(o.getId(), dto.id());
    assertEquals(o.getPriority(), dto.priority());
    assertEquals(o.getName(), dto.name());
    assertEquals(o.getStartDate(), dto.startDate());
    assertEquals(o.getAcceptanceDate(), dto.acceptanceDate());
    assertEquals(o.getPlannedEndDate(), dto.plannedEndDate());
    assertEquals(o.getRealEndDate(), dto.realEndDate());
    assertEquals(o.getPeriod(), dto.period());
    assertNull(dto.actualPeriod());
    assertEquals(o.isEnded(), dto.isEnded());
    assertEquals(o.isInWork(), dto.inWork());
    assertEquals(o.isReadyToAcceptance(), dto.readyToAcceptance());
    assertEquals(o.getProject().getNumber(), dto.projectNumber());
    assertEquals(o.getTypeWork().getName(), dto.typeWorkName());
    assertNull(dto.employeeFirstLastNameDto());
  }
}
