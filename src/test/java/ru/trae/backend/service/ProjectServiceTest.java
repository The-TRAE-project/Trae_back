/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.mapper.PageToPageDtoMapper;
import ru.trae.backend.dto.mapper.ProjectAvailableDtoMapper;
import ru.trae.backend.dto.mapper.ProjectDtoMapper;
import ru.trae.backend.dto.operation.NewOperationDto;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.dto.project.ProjectDto;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.exceptionhandler.exception.ProjectException;
import ru.trae.backend.factory.ProjectFactory;
import ru.trae.backend.repository.ProjectRepository;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private ProjectFactory projectFactory;
  @Mock
  private OperationService operationService;
  @Mock
  private EmployeeService employeeService;
  @Mock
  private ProjectDtoMapper projectDtoMapper;
  @Mock
  private ProjectAvailableDtoMapper projectAvailableDtoMapper;
  @Mock
  private PageToPageDtoMapper pageToPageDtoMapper;
  @InjectMocks
  private ProjectService projectService;
  private ManagerDto managerDto;
  private NewProjectDto newProjectDto;
  private NewOperationDto newOperationDto;
  private ProjectDto projectDto;
  private long projectId = 1L;
  private int projectNumber = 1;
  private int period = 240;
  private int actualPriod = 20;
  private String name = "Test project";
  private String customer = "Test customer";
  private LocalDateTime startDate = LocalDateTime.now();
  private LocalDateTime plannedEndDate = LocalDateTime.now().plusDays(10);
  private LocalDateTime endDateInContract = LocalDateTime.now().plusDays(10);
  private LocalDateTime realEndDate = LocalDateTime.now().plusDays(8);
  
  @BeforeEach
  void setup() {
    managerDto = new ManagerDto(
        1L, "Man", "Man", "Manager",
        "+7 (999) 999 9999", "test", "test_username",
        true, LocalDateTime.now().toString(), null);
    newOperationDto = new NewOperationDto("Test op", 2L);
    newProjectDto = new NewProjectDto(
        1, name, endDateInContract,
        customer, null, List.of(newOperationDto));
    
    projectDto = new ProjectDto(
        projectId, projectNumber, name, startDate, startDate.plusHours(2),
        plannedEndDate, endDateInContract, realEndDate, period, actualPriod, true,
        List.of(), managerDto, customer, null);
    
    reset(projectRepository, projectFactory, operationService, employeeService, projectDtoMapper,
        projectAvailableDtoMapper, pageToPageDtoMapper);
  }
  
//  @Test
//  void saveNewProject_ValidInput_SuccessfullySaved() {
//    //given
//    String authUsername = managerDto.username();
//    Project project = new Project();
//    NewProjectDto newProjectDto1 = new NewProjectDto(anyInt(), anyString(),
//        any(LocalDateTime.class), anyString(), anyString(), null);
//
//    //when
//    when(projectFactory.create(anyInt(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class),
//        anyInt(), anyString(), anyString(), anyString())).thenReturn(project);
//
//    projectService.saveNewProject(newProjectDto1, authUsername);
//
//    //then
//    verify(projectFactory).create(anyInt(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class),
//        anyInt(), anyString(), anyString(), anyString());
//    verify(projectRepository).save(project);
//    verify(operationService).saveNewOperations(eq(project), anyList());
//  }
  
  @Test
  void getProjectById_ExistingId_ReturnsProject() {
    //given
    Project project = new Project();
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    
    //when
    Project result = projectService.getProjectById(projectId);
    
    //then
    assertEquals(project, result);
  }
  
  @Test
  void getProjectById_NonExistingId_ThrowsProjectException() {
    //when
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    
    //then
    assertThrows(ProjectException.class, () -> projectService.getProjectById(projectId));
  }
  
  @Test
  void getAllProjects_NoProjects_ReturnsEmptyList() {
    //given
    when(projectRepository.findAll()).thenReturn(Collections.emptyList());
    
    //when
    List<ProjectDto> result = projectService.getAllProjects();
    
    //then
    assertTrue(result.isEmpty());
  }
  
  @Test
  void getAllProjects_HasProjects_ReturnsProjectList() {
    //given
    List<Project> projects = Collections.singletonList(new Project());
    
    //when
    when(projectRepository.findAll()).thenReturn(projects);
    when(projectDtoMapper.apply(any(Project.class))).thenReturn(projectDto);
    
    List<ProjectDto> result = projectService.getAllProjects();
    
    //then
    assertEquals(projects.size(), result.size());
  }
  
  
  @Test
  void getProject_ValidId_ReturnsProjectDto() {
    //given
    Project project = new Project();
    
    //when
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    when(projectDtoMapper.apply(project)).thenReturn(projectDto);
    
    ProjectDto result = projectService.getProjectDtoById(projectId);
    
    //then
    assertNotNull(result);
    assertEquals(projectDto, result);
    verify(projectRepository).findById(projectId);
    verify(projectDtoMapper).apply(project);
  }
  
  @Test
  void deleteProject_ValidId_SuccessfullyDeleted() {
    //when
    Project p = new Project();
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(p));
    projectService.deleteProject(projectId);
    
    //then
    verify(projectRepository).delete(p);
  }
  
  @Test
  void deleteProject_NonExistingId_ThrowsProjectException() {
    //when
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    
    //then
    assertThrows(ProjectException.class, () -> projectService.deleteProject(projectId));
    verify(projectRepository).findById(projectId);
  }
}
