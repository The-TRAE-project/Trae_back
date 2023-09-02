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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import ru.trae.backend.dto.operation.OperationInfoForProjectTemplateDto;
import ru.trae.backend.dto.project.ProjectShortDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;

class ProjectShortDtoMapperTest {
  
  @Test
  void apply_WhenProjectHasOperations_ShouldMapToProjectShortDtoWithOperationInfo() {
    //given
    Project project = new Project();
    project.setEndDateInContract(LocalDateTime.now().plusDays(30));
    project.setId(1L);
    project.setEnded(true);
    project.setNumber(100);
    project.setName("test_project");
    project.setCustomer("test_customer");
    
    Operation o1 = new Operation();
    o1.setPlannedEndDate(LocalDateTime.now().plusDays(5));
    o1.setId(1L);
    o1.setPriority(0);
    o1.setName("operation_1");
    o1.setInWork(true);
    o1.setReadyToAcceptance(false);
    
    Operation o2 = new Operation();
    o1.setPlannedEndDate(LocalDateTime.now().plusDays(10));
    o2.setId(2L);
    o2.setPriority(1);
    o2.setName("operation_2");
    o2.setInWork(true);
    o2.setReadyToAcceptance(true);
    
    project.setOperations(List.of(o1, o2));
    
    OperationInfoForProjectTemplateDtoMapper operationInfoForProjectTemplateDtoMapper
        = mock(OperationInfoForProjectTemplateDtoMapper.class);
    OperationInfoForProjectTemplateDto operationInfoDto
        = new OperationInfoForProjectTemplateDto(o2.getName(), o2.isEnded(), o2.isInWork(), o2.isReadyToAcceptance());
    when(operationInfoForProjectTemplateDtoMapper.apply(o2)).thenReturn(operationInfoDto);
    
    ProjectShortDtoMapper projectShortDtoMapper = new ProjectShortDtoMapper(operationInfoForProjectTemplateDtoMapper);
    
    //when
    ProjectShortDto projectShortDto = projectShortDtoMapper.apply(project);
    
    //then
    assertEquals(project.getId(), projectShortDto.id());
    assertEquals(project.isEnded(), projectShortDto.isEnded());
    assertEquals(project.getNumber(), projectShortDto.number());
    assertEquals(project.getName(), projectShortDto.name());
    assertEquals(project.getCustomer(), projectShortDto.customer());
    assertEquals(operationInfoDto, projectShortDto.operation());
    
    verify(operationInfoForProjectTemplateDtoMapper).apply(o2);
  }
}
