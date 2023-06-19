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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.trae.backend.util.Constant.NOT_FOUND_CONST;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.mapper.PageToPageDtoMapper;
import ru.trae.backend.dto.mapper.ProjectAvailableDtoMapper;
import ru.trae.backend.dto.mapper.ProjectDtoMapper;
import ru.trae.backend.dto.operation.NewOperationDto;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.dto.project.ProjectDto;
import ru.trae.backend.dto.project.ProjectShortDto;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.exceptionhandler.exception.ProjectException;
import ru.trae.backend.factory.ProjectFactory;
import ru.trae.backend.projection.ProjectIdNumberDto;
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
  Project project;
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
    project = new Project();
    
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
  
  @Test
  void saveNewProject_ShouldSaveProjectAndOperations() {
    //given
    project.setOperationPeriod(45);
    
    //when
    when(projectFactory.create(anyInt(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class),
        anyInt(), anyString(), isNull(), anyString())).thenReturn(project);
    
    projectService.saveNewProject(newProjectDto, managerDto.username());
    
    //then
    verify(projectRepository).save(project);
    verify(operationService).saveNewOperations(project, newProjectDto.operations());
  }
  
  @Test
  void getProjectById_ExistingId_ReturnsProject() {
    //given
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
    when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
    projectService.deleteProject(projectId);
    
    //then
    verify(projectRepository).delete(project);
  }
  
  @Test
  void deleteProject_NonExistingId_ThrowsProjectException() {
    //when
    when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
    
    //then
    assertThrows(ProjectException.class, () -> projectService.deleteProject(projectId));
    verify(projectRepository).findById(projectId);
  }
  
  @Test
  void getProjectByOperationId_WithExistingProject_ShouldReturnProject() {
    //given
    long operationId = 1L;
    
    //when
    when(projectRepository.findByOperationId(operationId)).thenReturn(Optional.of(project));
    
    Project result = projectService.getProjectByOperationId(operationId);
    
    //then
    assertNotNull(result);
    assertEquals(project, result);
    
    verify(projectRepository).findByOperationId(operationId);
  }
  
  @Test
  void getProjectByOperationId_WithNonExistingProject_ShouldThrowProjectException() {
    //given
    long operationId = 1L;
    
    //when
    when(projectRepository.findByOperationId(operationId)).thenReturn(Optional.empty());
    
    ProjectException exception = assertThrows(ProjectException.class,
        () -> projectService.getProjectByOperationId(operationId));
    
    //then
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    assertEquals("Project with operation ID: " + operationId
        + NOT_FOUND_CONST.value, exception.getMessage());
    
    verify(projectRepository).findByOperationId(operationId);
  }
  
  @Test
  void getProjectIdNumberDtoListWithFilters_WithEmployeeIds_ShouldReturnFilteredProjects() {
    //given
    Set<Long> employeeIds = Set.of(1L, 2L);
    LocalDate startOfPeriod = LocalDate.of(2023, 6, 1);
    LocalDate endOfPeriod = LocalDate.of(2023, 6, 30);
    List<ProjectIdNumberDto> expectedProjects = new ArrayList<>();
    
    //when
    when(projectRepository.findByPeriodAndEmployeeIds(startOfPeriod, endOfPeriod, employeeIds))
        .thenReturn(expectedProjects);
    
    List<ProjectIdNumberDto> result = projectService.getProjectIdNumberDtoListWithFilters(
        employeeIds, null, startOfPeriod, endOfPeriod);
    
    //then
    assertEquals(expectedProjects, result);
    verify(projectRepository).findByPeriodAndEmployeeIds(startOfPeriod, endOfPeriod, employeeIds);
  }
  
  @Test
  void getProjectIdNumberDtoListWithFilters_WithOperationIds_ShouldReturnFilteredProjects() {
    //given
    Set<Long> operationIds = Set.of(1L, 2L);
    LocalDate startOfPeriod = LocalDate.of(2023, 6, 1);
    LocalDate endOfPeriod = LocalDate.of(2023, 6, 30);
    List<ProjectIdNumberDto> expectedProjects = new ArrayList<>();
    
    //when
    when(projectRepository.findByPeriodAndOperationIds(startOfPeriod, endOfPeriod, operationIds))
        .thenReturn(expectedProjects);
    
    List<ProjectIdNumberDto> result = projectService.getProjectIdNumberDtoListWithFilters(
        null, operationIds, startOfPeriod, endOfPeriod);
    
    //then
    assertEquals(expectedProjects, result);
    verify(projectRepository).findByPeriodAndOperationIds(startOfPeriod, endOfPeriod, operationIds);
  }
  
  @Test
  void getProjectIdNumberDtoListWithFilters_WithoutEmployeeIdsAndOperationIds_ShouldReturnAllProjects() {
    //given
    LocalDate startOfPeriod = LocalDate.of(2023, 6, 1);
    LocalDate endOfPeriod = LocalDate.of(2023, 6, 30);
    List<ProjectIdNumberDto> expectedProjects = new ArrayList<>();
    
    //when
    when(projectRepository.findByPeriod(startOfPeriod, endOfPeriod)).thenReturn(expectedProjects);
    
    List<ProjectIdNumberDto> result = projectService.getProjectIdNumberDtoListWithFilters(
        null, null, startOfPeriod, endOfPeriod);
    
    //then
    assertEquals(expectedProjects, result);
    verify(projectRepository).findByPeriod(startOfPeriod, endOfPeriod);
  }
  
  @Test
  void getProjectIdNumberDtoListWithFilters_WithEmptyEmployeeIdsAndOperationIds_ShouldReturnAllProjects() {
    //given
    Set<Long> operationIds = Set.of();
    Set<Long> employeeIds = Set.of();
    LocalDate startOfPeriod = LocalDate.of(2023, 6, 1);
    LocalDate endOfPeriod = LocalDate.of(2023, 6, 30);
    List<ProjectIdNumberDto> expectedProjects = new ArrayList<>();
    
    //when
    when(projectRepository.findByPeriod(startOfPeriod, endOfPeriod)).thenReturn(expectedProjects);
    
    List<ProjectIdNumberDto> result = projectService.getProjectIdNumberDtoListWithFilters(
        employeeIds, operationIds, startOfPeriod, endOfPeriod);
    
    //then
    assertEquals(expectedProjects, result);
    verify(projectRepository).findByPeriod(startOfPeriod, endOfPeriod);
  }
  
  @Test
  void findProjectsForPeriod_ShouldReturnProjectsInSpecifiedPeriod() {
    //given
    LocalDate startOfPeriod = LocalDate.of(2023, 6, 1);
    LocalDate endOfPeriod = LocalDate.of(2023, 6, 30);
    List<Project> expectedProjects = List.of(project);
    
    //when
    when(projectRepository.findProjectsForPeriod(startOfPeriod, endOfPeriod))
        .thenReturn(expectedProjects);
    
    List<Project> result = projectService.findProjectsForPeriod(startOfPeriod, endOfPeriod);
    
    //then
    assertEquals(expectedProjects, result);
    verify(projectRepository).findProjectsForPeriod(startOfPeriod, endOfPeriod);
  }
  
  @Test
  void findProjectByNumberOrCustomer_ShouldReturnPageDtoOfProjectShortDto() {
    //given
    Pageable projectPage = PageRequest.of(0, 1);
    String projectNumberOrCustomer = "123";
    
    List<Project> projects = List.of(project);
    Page<Project> projectPageResult = new PageImpl<>(projects, projectPage, projects.size());
    
    //when
    when(projectService.findProjectPage(projectPage, projectNumberOrCustomer))
        .thenReturn(projectPageResult);
    
    PageDto<ProjectShortDto> projectShortDtoPageDto = pageToPageDtoMapper.projectPageToPageDto(projectPageResult);
    PageDto<ProjectShortDto> result = projectService.findProjectByNumberOrCustomer(projectPage, projectNumberOrCustomer);
    
    //then
    assertEquals(projectShortDtoPageDto, result);
  }
  
  @Test
  void findProjectPage_ShouldReturnPageOfProjects_ByCustomer() {
    //given
    Pageable projectPage = PageRequest.of(0, 10);
    String projectNumberOrCustomer = "Customer";
    
    List<Project> projects = List.of(project);
    Page<Project> projectPageResult = new PageImpl<>(projects, projectPage, projects.size());
    
    //when
    when(projectRepository.findByCustomerLikeIgnoreCase(anyString(), any(Pageable.class)))
        .thenReturn(projectPageResult);
    
    Page<Project> result = projectService.findProjectPage(projectPage, projectNumberOrCustomer);
    
    //then
    assertEquals(projectPageResult, result);
    verify(projectRepository).findByCustomerLikeIgnoreCase(eq("CUSTOMER"), eq(projectPage));
    verify(projectRepository, never()).findByNumber(anyInt(), any(Pageable.class));
  }
  
}
