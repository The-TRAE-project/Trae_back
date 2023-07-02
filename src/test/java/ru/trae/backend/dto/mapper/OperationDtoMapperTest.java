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
    // Arrange
    Operation operation = new Operation();
    operation.setId(1L);
    operation.setPriority(0);
    operation.setName("Test Operation");
    operation.setStartDate(LocalDateTime.now().minusHours(2));
    operation.setAcceptanceDate(LocalDateTime.now().minusHours(1));
    operation.setPlannedEndDate(LocalDateTime.now().plusHours(2));
    operation.setRealEndDate(LocalDateTime.now());
    operation.setPeriod(4);
    operation.setEnded(true);
    operation.setInWork(true);
    operation.setReadyToAcceptance(false);
    operation.setTypeWork(new TypeWork());
    
    Project project = new Project();
    project.setNumber(100);
    operation.setProject(project);
    
    Employee employee = new Employee();
    employee.setFirstName("John");
    employee.setLastName("Doe");
    operation.setEmployee(employee);
    
    OperationDtoMapper operationDtoMapper = new OperationDtoMapper();
    
    // Act
    OperationDto operationDto = operationDtoMapper.apply(operation);
    
    // Assert
    assertEquals(operation.getId(), operationDto.id());
    assertEquals(operation.getPriority(), operationDto.priority());
    assertEquals(operation.getName(), operationDto.name());
    assertEquals(operation.getStartDate(), operationDto.startDate());
    assertEquals(operation.getAcceptanceDate(), operationDto.acceptanceDate());
    assertEquals(operation.getPlannedEndDate(), operationDto.plannedEndDate());
    assertEquals(operation.getRealEndDate(), operationDto.realEndDate());
    assertEquals(operation.getPeriod(), operationDto.period());
    assertEquals(Math.toIntExact(operation.getRealEndDate().toLocalTime().getHour()
        - operation.getStartDate().toLocalTime().getHour()), operationDto.actualPeriod());
    assertEquals(operation.isEnded(), operationDto.isEnded());
    assertEquals(operation.isInWork(), operationDto.inWork());
    assertEquals(operation.isReadyToAcceptance(), operationDto.readyToAcceptance());
    assertEquals(operation.getProject().getNumber(), operationDto.projectNumber());
    assertEquals(operation.getTypeWork().getName(), operationDto.typeWorkName());
    assertEquals(operation.getEmployee().getFirstName(), operationDto.employeeFirstLastNameDto().firstName());
    assertEquals(operation.getEmployee().getLastName(), operationDto.employeeFirstLastNameDto().lastName());
  }
  
  @Test
  void apply_WhenOperationNotEnded_ShouldMapToOperationDtoWithNullActualPeriod() {
    // Arrange
    Operation operation = new Operation();
    operation.setId(2L);
    operation.setPriority(1);
    operation.setName("Another Operation");
    operation.setStartDate(LocalDateTime.now().minusHours(4));
    operation.setAcceptanceDate(null);
    operation.setPlannedEndDate(LocalDateTime.now().plusHours(6));
    operation.setRealEndDate(null);
    operation.setPeriod(10);
    operation.setEnded(false);
    operation.setInWork(true);
    operation.setReadyToAcceptance(true);
    operation.setTypeWork(new TypeWork());
    
    Project project = new Project();
    project.setNumber(200);
    operation.setProject(project);
    
    OperationDtoMapper operationDtoMapper = new OperationDtoMapper();
    
    // Act
    OperationDto operationDto = operationDtoMapper.apply(operation);
    
    // Assert
    assertEquals(operation.getId(), operationDto.id());
    assertEquals(operation.getPriority(), operationDto.priority());
    assertEquals(operation.getName(), operationDto.name());
    assertEquals(operation.getStartDate(), operationDto.startDate());
    assertEquals(operation.getAcceptanceDate(), operationDto.acceptanceDate());
    assertEquals(operation.getPlannedEndDate(), operationDto.plannedEndDate());
    assertEquals(operation.getRealEndDate(), operationDto.realEndDate());
    assertEquals(operation.getPeriod(), operationDto.period());
    assertNull(operationDto.actualPeriod());
    assertEquals(operation.isEnded(), operationDto.isEnded());
    assertEquals(operation.isInWork(), operationDto.inWork());
    assertEquals(operation.isReadyToAcceptance(), operationDto.readyToAcceptance());
    assertEquals(operation.getProject().getNumber(), operationDto.projectNumber());
    assertEquals(operation.getTypeWork().getName(), operationDto.typeWorkName());
    assertNull(operationDto.employeeFirstLastNameDto());
  }
}
