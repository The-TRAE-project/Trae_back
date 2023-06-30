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
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trae.backend.dto.operation.OperationForReportDto;
import ru.trae.backend.dto.project.ProjectForReportDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;

@ExtendWith(MockitoExtension.class)
class ProjectForReportDtoMapperTest {
  @Mock
  private OperationForReportDtoMapper operationForReportDtoMapper;
  @InjectMocks
  private ProjectForReportDtoMapper mapper;
  
  @Test
  void apply_WhenProjectProvided_ShouldMapToProjectForReportDto() {
    //given
    LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
    LocalDateTime startFirstOperationDate = LocalDateTime.of(2023, 1, 2, 0, 0);
    LocalDateTime plannedEndDate = LocalDateTime.of(2023, 1, 5, 0, 0);
    LocalDateTime endDateInContract = LocalDateTime.of(2023, 1, 6, 0, 0);
    LocalDateTime realEndDate = LocalDateTime.of(2023, 1, 7, 0, 0);
    
    Operation operation1 = new Operation();
    Operation operation2 = new Operation();
    List<Operation> operations = Arrays.asList(operation1, operation2);
    
    Project project = new Project();
    project.setId(1L);
    project.setNumber(100);
    project.setName("test_project_1");
    project.setStartDate(startDate);
    project.setStartFirstOperationDate(startFirstOperationDate);
    project.setPlannedEndDate(plannedEndDate);
    project.setEndDateInContract(endDateInContract);
    project.setRealEndDate(realEndDate);
    project.setEnded(true);
    project.setOperationPeriod(8);
    project.setOperations(operations);
    project.setCustomer("customer");
    project.setComment("comment");
    
    OperationForReportDto operationDto1 = new OperationForReportDto(1L, 0,
        "op_name_1", LocalDateTime.now(), LocalDateTime.now().plusHours(2),
        LocalDateTime.now().plusDays(5), null, false, true, false);
    OperationForReportDto operationDto2 = new OperationForReportDto(2L, 10,
        "op_name_2", null, null, null,
        null, false, false, false);
    
    //when
    when(operationForReportDtoMapper.apply(operation1)).thenReturn(operationDto1);
    when(operationForReportDtoMapper.apply(operation2)).thenReturn(operationDto2);
    
    ProjectForReportDto result = mapper.apply(project);
    
    //then
    assertEquals(project.getId(), result.id());
    assertEquals(project.getNumber(), result.number());
    assertEquals(project.getName(), result.name());
    assertEquals(project.getStartDate(), result.startDate());
    assertEquals(project.getStartFirstOperationDate(), result.startFirstOperationDate());
    assertEquals(project.getPlannedEndDate(), result.plannedEndDate());
    assertEquals(project.getEndDateInContract(), result.endDateInContract());
    assertEquals(project.getRealEndDate(), result.realEndDate());
    assertEquals(project.isEnded(), result.isEnded());
    assertEquals(project.getOperationPeriod(), result.operationPeriod());
    assertEquals(2, result.operations().size());
    assertEquals(project.getCustomer(), result.customer());
    assertEquals(project.getComment(), result.comment());
  }
  
  @Test
  void apply_WhenProjectProvidedWithoutComment_ShouldMapToProjectForReportDto() {
    //given
    LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
    LocalDateTime startFirstOperationDate = LocalDateTime.of(2023, 1, 2, 0, 0);
    LocalDateTime plannedEndDate = LocalDateTime.of(2023, 1, 5, 0, 0);
    LocalDateTime endDateInContract = LocalDateTime.of(2023, 1, 6, 0, 0);
    LocalDateTime realEndDate = LocalDateTime.of(2023, 1, 7, 0, 0);
    
    Operation operation1 = new Operation();
    operation1.setStartDate(startDate);
    operation1.setAcceptanceDate(startDate.plusHours(2));
    operation1.setPlannedEndDate(startDate.plusDays(5));
    Operation operation2 = new Operation();
    List<Operation> operations = Arrays.asList(operation1, operation2);
    
    Project project = new Project();
    project.setId(1L);
    project.setNumber(100);
    project.setName("test_project_1");
    project.setStartDate(startDate);
    project.setStartFirstOperationDate(startFirstOperationDate);
    project.setPlannedEndDate(plannedEndDate);
    project.setEndDateInContract(endDateInContract);
    project.setRealEndDate(realEndDate);
    project.setEnded(true);
    project.setOperationPeriod(8);
    project.setOperations(operations);
    project.setCustomer("customer");
    project.setComment(null);
    
    OperationForReportDto operationDto1 = new OperationForReportDto(1L, 0,
        "op_name_1", startDate, startDate.plusHours(2),
        startDate.plusDays(5), null, false, true, false);
    OperationForReportDto operationDto2 = new OperationForReportDto(2L, 10,
        "op_name_2", null, null, null,
        null, false, false, false);
    
    //when
    when(operationForReportDtoMapper.apply(operation1)).thenReturn(operationDto1);
    when(operationForReportDtoMapper.apply(operation2)).thenReturn(operationDto2);
    
    ProjectForReportDto result = mapper.apply(project);
    
    //then
    assertEquals(project.getId(), result.id());
    assertEquals(project.getNumber(), result.number());
    assertEquals(project.getName(), result.name());
    assertEquals(project.getStartDate(), result.startDate());
    assertEquals(project.getStartFirstOperationDate(), result.startFirstOperationDate());
    assertEquals(project.getPlannedEndDate(), result.plannedEndDate());
    assertEquals(project.getEndDateInContract(), result.endDateInContract());
    assertEquals(project.getRealEndDate(), result.realEndDate());
    assertEquals(project.isEnded(), result.isEnded());
    assertEquals(project.getOperationPeriod(), result.operationPeriod());
    assertEquals(2, result.operations().size());
    assertEquals(project.getCustomer(), result.customer());
    assertEquals(project.getComment(), result.comment());
  }
}

