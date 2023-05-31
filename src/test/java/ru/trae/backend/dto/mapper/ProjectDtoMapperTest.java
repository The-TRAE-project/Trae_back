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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.trae.backend.dto.project.ProjectDto;
import ru.trae.backend.entity.task.Project;

class ProjectDtoMapperTest {
  
  @Mock
  private ManagerDtoMapper managerDtoMapper;
  @Mock
  private OperationDtoMapper operationDtoMapper;
  
  @InjectMocks
  private ProjectDtoMapper projectDtoMapper;
  
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  void apply_ShouldMapProjectToProjectDto() {
    //given
    Project project = new Project();
    project.setId(1L);
    project.setNumber(111);
    project.setName("Project 1");
    project.setStartDate(LocalDateTime.now());
    project.setStartFirstOperationDate(LocalDateTime.now());
    project.setPlannedEndDate(LocalDateTime.now());
    project.setEndDateInContract(LocalDateTime.now());
    project.setRealEndDate(LocalDateTime.now().plusHours(2));
    project.setPeriod(10);
    project.setEnded(true);
    project.setOperations(Collections.emptyList());
    project.setManager(null);
    project.setCustomer("Customer");
    project.setComment("Comment");
    
    //when
    when(operationDtoMapper.apply(any())).thenReturn(null);
    when(managerDtoMapper.apply(any())).thenReturn(null);
    
    ProjectDto result = projectDtoMapper.apply(project);
    
    //then
    assertEquals(project.getId(), result.id());
    assertEquals(project.getNumber(), result.number());
    assertEquals(project.getName(), result.name());
    assertEquals(project.getStartDate(), result.startDate());
    assertEquals(project.getStartFirstOperationDate(), result.startFirstOperationDate());
    assertEquals(project.getPlannedEndDate(), result.plannedEndDate());
    assertEquals(project.getEndDateInContract(), result.endDateInContract());
    assertEquals(project.getRealEndDate(), result.realEndDate());
    assertEquals(project.getPeriod(), result.period());
    assertEquals((Integer) 2, result.actualPeriod());
    assertEquals(project.isEnded(), result.isEnded());
    assertEquals(project.getOperations().size(), result.operations().size());
    assertEquals(project.getManager(), result.managerDto());
    assertEquals(project.getCustomer(), result.customer());
    assertEquals(project.getComment(), result.comment());
    
    // Verify that mocked mappers were called
    verify(operationDtoMapper, times(project.getOperations().size())).apply(any());
    verify(managerDtoMapper, times(1)).apply(any());
  }
  
  @Test
  void apply_ShouldMapProjectToProjectDto_WithNullComment_AndIsNotEndedProject() {
    //given
    Project project = new Project();
    project.setId(1L);
    project.setNumber(111);
    project.setName("Project 1");
    project.setStartDate(LocalDateTime.now());
    project.setStartFirstOperationDate(LocalDateTime.now());
    project.setPlannedEndDate(LocalDateTime.now());
    project.setEndDateInContract(LocalDateTime.now());
    project.setRealEndDate(LocalDateTime.now().plusHours(2));
    project.setPeriod(10);
    project.setEnded(false);
    project.setOperations(Collections.emptyList());
    project.setManager(null);
    project.setCustomer("Customer");
    project.setComment(null);
    
    //when
    when(operationDtoMapper.apply(any())).thenReturn(null);
    when(managerDtoMapper.apply(any())).thenReturn(null);
    
    ProjectDto result = projectDtoMapper.apply(project);
    
    //then
    assertEquals(project.getId(), result.id());
    assertEquals(project.getNumber(), result.number());
    assertEquals(project.getName(), result.name());
    assertEquals(project.getStartDate(), result.startDate());
    assertEquals(project.getStartFirstOperationDate(), result.startFirstOperationDate());
    assertEquals(project.getPlannedEndDate(), result.plannedEndDate());
    assertEquals(project.getEndDateInContract(), result.endDateInContract());
    assertEquals(project.getRealEndDate(), result.realEndDate());
    assertEquals(project.getPeriod(), result.period());
    assertNull(result.actualPeriod());
    assertEquals(project.isEnded(), result.isEnded());
    assertEquals(project.getOperations().size(), result.operations().size());
    assertEquals(project.getManager(), result.managerDto());
    assertEquals(project.getCustomer(), result.customer());
    assertEquals(project.getComment(), result.comment());
    
    // Verify that mocked mappers were called
    verify(operationDtoMapper, times(project.getOperations().size())).apply(any());
    verify(managerDtoMapper, times(1)).apply(any());
  }
}
