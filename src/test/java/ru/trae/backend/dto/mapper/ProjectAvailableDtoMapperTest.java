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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import ru.trae.backend.dto.project.ProjectAvailableForEmpDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.exceptionhandler.exception.ProjectException;

class ProjectAvailableDtoMapperTest {
  
  @InjectMocks
  private ProjectAvailableDtoMapper projectAvailableDtoMapper;
  
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  void apply_ShouldMapProjectToProjectAvailableForEmpDto() {
    //given
    Project project = new Project();
    project.setId(1L);
    project.setNumber(111);
    project.setCustomer("Customer");
    project.setName("Project");
    
    Operation operation1 = new Operation();
    operation1.setName("Operation 1");
    operation1.setReadyToAcceptance(false);
    
    Operation operation2 = new Operation();
    operation2.setName("Operation 2");
    operation2.setReadyToAcceptance(true);
    
    List<Operation> operations = new ArrayList<>();
    operations.add(operation1);
    operations.add(operation2);
    project.setOperations(operations);
    
    //when
    ProjectAvailableForEmpDto result = projectAvailableDtoMapper.apply(project);
    
    //then
    assertEquals(project.getId(), result.id());
    assertEquals(project.getNumber(), result.number());
    assertEquals(project.getCustomer(), result.customer());
    assertEquals(project.getName(), result.projectName());
    assertEquals(operation2.getName(), result.availableOperationName());
  }
  
  @Test
  void apply_ShouldThrowException_WhenNoAvailableOperations() {
    //given
    Project project = new Project();
    project.setId(1L);
    project.setNumber(111);
    project.setCustomer("Customer");
    project.setName("Project");
    project.setOperations(Collections.emptyList());
    
    //when
    ProjectException exception = assertThrows(ProjectException.class, () -> projectAvailableDtoMapper.apply(project));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("Available projects not found", exception.getMessage());
  }
}
