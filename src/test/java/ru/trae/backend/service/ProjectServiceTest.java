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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.trae.backend.service.OperationService.MIN_PERIOD_OPERATION;
import static ru.trae.backend.service.OperationService.SHIPMENT_PERIOD;
import static ru.trae.backend.util.Constant.NOT_FOUND_CONST;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
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
import ru.trae.backend.dto.project.ChangingCommonDataReq;
import ru.trae.backend.dto.project.ChangingCommonDataResp;
import ru.trae.backend.dto.project.ChangingEndDatesReq;
import ru.trae.backend.dto.project.ChangingEndDatesResp;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.dto.project.ProjectAvailableForEmpDto;
import ru.trae.backend.dto.project.ProjectDto;
import ru.trae.backend.dto.project.ProjectShortDto;
import ru.trae.backend.entity.TypeWork;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;
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
  void saveNewProject_ShouldThrowException_MinimalPeriodOpNotCorrect() {
    //given
    project.setOperationPeriod(23);
    
    //when
    when(projectFactory.create(anyInt(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class),
        anyInt(), anyString(), isNull(), anyString())).thenReturn(project);
    
    ProjectException exception = assertThrows(ProjectException.class,
        () -> projectService.saveNewProject(newProjectDto, managerDto.username()));
    
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("The calculated period(" + project.getOperationPeriod()
        + " hours) for performing operations should not be less than "
        + MIN_PERIOD_OPERATION + " hours", exception.getMessage());
  }
  
  @Test
  void saveNewProject_ShouldThrowException_EmptyListOps() {
    //given
    project.setOperationPeriod(45);
    newProjectDto = new NewProjectDto(
        1, name, endDateInContract,
        customer, null, Collections.emptyList());
    
    //when
    ProjectException exception = assertThrows(ProjectException.class,
        () -> projectService.saveNewProject(newProjectDto, managerDto.username()));
    
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("List of operations cannot be empty", exception.getMessage());
  }
  
  @Test
  void saveNewProject_ShouldThrowException_NullListOps() {
    //given
    project.setOperationPeriod(45);
    newProjectDto = new NewProjectDto(
        1, name, endDateInContract,
        customer, null, null);
    
    //when
    ProjectException exception = assertThrows(ProjectException.class,
        () -> projectService.saveNewProject(newProjectDto, managerDto.username()));
    
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("List of operations cannot be empty", exception.getMessage());
  }
  
  @Test
  void saveNewProject_ShouldThrowException_IncorrectEndPlannedDate() {
    //given
    project.setOperationPeriod(45);
    newProjectDto = new NewProjectDto(
        1, name, LocalDateTime.now(),
        customer, null, List.of(newOperationDto));
    
    //when
    ProjectException exception = assertThrows(ProjectException.class,
        () -> projectService.saveNewProject(newProjectDto, managerDto.username()));
    
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("The planned end date cannot be less than current(start) date of project + "
        + (MIN_PERIOD_OPERATION + SHIPMENT_PERIOD) + " additional hours.", exception.getMessage());
  }
  
  @Test
  void saveNewProject_ShouldThrowException_IncorrectEndPlannedDateIsTooBig() {
    //given
    project.setOperationPeriod(45);
    newProjectDto = new NewProjectDto(
        1, name, LocalDateTime.now().plusHours(8761),
        customer, null, List.of(newOperationDto));
    
    //when
    ProjectException exception = assertThrows(ProjectException.class,
        () -> projectService.saveNewProject(newProjectDto, managerDto.username()));
    
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("The planned end date cannot be more than "
        + "start date of project + 1 year (or 8760 hours).", exception.getMessage());
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
  void getProjectIdNumberDtoListWithFilters_WithEmployeeIds_ShouldThrowException() {
    //given
    Set<Long> employeeIds = Set.of(1L, 2L);
    LocalDate startOfPeriod = LocalDate.of(2023, 6, 1);
    LocalDate endOfPeriod = LocalDate.of(2023, 5, 30);
    
    ProjectException exception = assertThrows(ProjectException.class,
        () -> projectService.getProjectIdNumberDtoListWithFilters(employeeIds, null, startOfPeriod, endOfPeriod));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("Start date cannot be after end date.", exception.getMessage());
  }
  
  @Test
  void getProjectIdNumberDtoListWithFilters_WithEndDate() {
    //given
    Set<Long> employeeIds = Set.of(1L, 2L);
    LocalDate endOfPeriod = LocalDate.of(2023, 6, 30);
    List<ProjectIdNumberDto> expectedProjects = new ArrayList<>();
    
    //when
    when(projectRepository.findByPeriodAndEmployeeIds(null, endOfPeriod, employeeIds))
        .thenReturn(expectedProjects);
    
    List<ProjectIdNumberDto> result = projectService.getProjectIdNumberDtoListWithFilters(
        employeeIds, null, null, endOfPeriod);
    
    //then
    assertEquals(expectedProjects, result);
    verify(projectRepository).findByPeriodAndEmployeeIds(null, endOfPeriod, employeeIds);
  }
  
  @Test
  void getProjectIdNumberDtoListWithFilters_WithStartDate() {
    //given
    Set<Long> employeeIds = Set.of(1L, 2L);
    LocalDate startOfPeriod = LocalDate.of(2023, 6, 30);
    List<ProjectIdNumberDto> expectedProjects = new ArrayList<>();
    
    //when
    when(projectRepository.findByPeriodAndEmployeeIds(startOfPeriod, null, employeeIds))
        .thenReturn(expectedProjects);
    
    List<ProjectIdNumberDto> result = projectService.getProjectIdNumberDtoListWithFilters(
        employeeIds, null, startOfPeriod, null);
    
    //then
    assertEquals(expectedProjects, result);
    verify(projectRepository).findByPeriodAndEmployeeIds(startOfPeriod, null, employeeIds);
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
  
  @Test
  void getProjectPage_ShouldReturnPageOfAllProjects() {
    Pageable projectPage = PageRequest.of(0, 10);
    Page<Project> expectedPage = new PageImpl<>(List.of(project), projectPage, 1);
    
    //when
    when(projectRepository.findAll(projectPage)).thenReturn(expectedPage);
    
    //then
    Page<Project> result = projectService.getProjectPage(
        projectPage,
        null,
        null,
        null,
        null,
        null,
        null
    );
    
    //then
    verify(projectRepository).findAll(projectPage);
    assertEquals(expectedPage, result);
  }
  
  @Test
  void getProjectPage_ShouldReturnPageOfAllEndedProjects() {
    Pageable projectPage = PageRequest.of(0, 10);
    Page<Project> expectedPage = new PageImpl<>(List.of(project), projectPage, 1);
    
    //when
    when(projectRepository.findByIsEnded(true, projectPage))
        .thenReturn(expectedPage);
    
    //then
    Page<Project> result = projectService.getProjectPage(
        projectPage,
        true,
        null,
        null,
        null,
        null,
        null
    );
    
    //then
    verify(projectRepository).findByIsEnded(true, projectPage);
    assertEquals(expectedPage, result);
  }
  
  @Test
  void getProjectPage_ShouldReturnPageOfAllNotEndedProjects() {
    Pageable projectPage = PageRequest.of(0, 10);
    Page<Project> expectedPage = new PageImpl<>(List.of(project), projectPage, 1);
    
    //when
    when(projectRepository.findByIsEnded(false, projectPage))
        .thenReturn(expectedPage);
    
    //then
    Page<Project> result = projectService.getProjectPage(
        projectPage,
        false,
        null,
        null,
        null,
        null,
        null
    );
    
    //then
    verify(projectRepository).findByIsEnded(false, projectPage);
    assertEquals(expectedPage, result);
  }
  
  @Test
  void getProjectPage_ShouldReturnPageOfNotEndedProjects_WithFirstReadyToAcceptanceOp() {
    Pageable projectPage = PageRequest.of(0, 10);
    Page<Project> expectedPage = new PageImpl<>(List.of(project), projectPage, 1);
    
    //when
    when(projectRepository.findFirstByIsEndedAndOpPriorityAndReadyToAcceptance(0, projectPage))
        .thenReturn(expectedPage);
    
    //then
    Page<Project> result = projectService.getProjectPage(
        projectPage,
        false,
        true,
        null,
        null,
        null,
        null
    );
    
    //then
    verify(projectRepository).findFirstByIsEndedAndOpPriorityAndReadyToAcceptance(0, projectPage);
    assertEquals(expectedPage, result);
  }
  
  @Test
  void getProjectPage_ShouldReturnPageOfNotEndedProjects_WithOverdueCurrentOperation() {
    //given
    Pageable projectPage = PageRequest.of(0, 10);
    Page<Project> expectedPage = new PageImpl<>(List.of(project), projectPage, 1);
    LocalDateTime ldt = LocalDateTime.now();
    
    //define the acceptable range for LocalDateTime comparison
    LocalDateTime acceptableStartDateTime = ldt.minusSeconds(1);
    LocalDateTime acceptableEndDateTime = ldt.plusSeconds(1);
    
    //when
    when(projectRepository.findProjectsWithOverdueCurrentOperation(
        argThat(dateTime -> dateTime.isAfter(acceptableStartDateTime) && dateTime.isBefore(acceptableEndDateTime)),
        eq(projectPage)))
        .thenReturn(expectedPage);
    
    Page<Project> result = projectService.getProjectPage(
        projectPage,
        false,
        null,
        null,
        true,
        null,
        null
    );
    
    //then
    verify(projectRepository).findProjectsWithOverdueCurrentOperation(
        argThat(dateTime -> dateTime.isAfter(acceptableStartDateTime) && dateTime.isBefore(acceptableEndDateTime)),
        eq(projectPage));
    assertEquals(expectedPage, result);
  }
  
  @Test
  void getProjectPage_ShouldReturnPageOfNotEndedProjects_WithLastReadyToAcceptanceOp() {
    Pageable projectPage = PageRequest.of(0, 10);
    Page<Project> expectedPage = new PageImpl<>(List.of(project), projectPage, 1);
    
    //when
    when(projectRepository.findLastByIsEndedAndOpPriorityAndReadyToAcceptanceTrue(projectPage))
        .thenReturn(expectedPage);
    
    //then
    Page<Project> result = projectService.getProjectPage(
        projectPage,
        false,
        null,
        true,
        null,
        null,
        null
    );
    
    //then
    verify(projectRepository).findLastByIsEndedAndOpPriorityAndReadyToAcceptanceTrue(projectPage);
    assertEquals(expectedPage, result);
  }
  
  @Test
  void getProjectPage_ShouldReturnPageOfNotEndedProjects_WithCurrentInWorkOrReadyToAcceptanceOp() {
    Pageable projectPage = PageRequest.of(0, 10);
    Page<Project> expectedPage = new PageImpl<>(List.of(project), projectPage, 1);
    
    //when
    when(projectRepository.findOpsInWorkOrReadyToAcceptanceExceptFirstOpReadyToAcceptance(projectPage))
        .thenReturn(expectedPage);
    
    //then
    Page<Project> result = projectService.getProjectPage(
        projectPage,
        false,
        null,
        null,
        null,
        true,
        null
    );
    
    //then
    verify(projectRepository).findOpsInWorkOrReadyToAcceptanceExceptFirstOpReadyToAcceptance(projectPage);
    assertEquals(expectedPage, result);
  }
  
  @Test
  void getProjectPage_ShouldReturnPageOfNotEndedProjects_WithOverdueProject() {
    Pageable projectPage = PageRequest.of(0, 10);
    Page<Project> expectedPage = new PageImpl<>(List.of(project), projectPage, 1);
    
    //when
    when(projectRepository.findOverdueProjects(projectPage)).thenReturn(expectedPage);
    
    //then
    Page<Project> result = projectService.getProjectPage(
        projectPage,
        false,
        null,
        null,
        null,
        null,
        true
    );
    
    //then
    verify(projectRepository).findOverdueProjects(projectPage);
    assertEquals(expectedPage, result);
  }
  
  @Test
  void getProjectPage_ShouldThrowException_InternalParametersWithoutExternalParameter() {
    Pageable projectPage = PageRequest.of(0, 10);
    
    ProjectException exception = assertThrows(ProjectException.class,
        () -> projectService.getProjectPage(
            projectPage,
            null,
            true,
            null,
            null,
            null,
            null));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("Internal parameters without external "
        + "parameter(isEnded) for filters in the request", exception.getMessage());
  }
  
  @Test
  void getProjectPage_ShouldThrowException_IncorrectNumberOfInternalParametersForFilters() {
    Pageable projectPage = PageRequest.of(0, 10);
    
    ProjectException exception = assertThrows(ProjectException.class,
        () -> projectService.getProjectPage(
            projectPage,
            false,
            true,
            true,
            null,
            null,
            null));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("Incorrect number of internal parameters for filters in the request", exception.getMessage());
  }
  
  @Test
  void getProjectPage_ShouldThrowException_InternalParametersForFiltersAreNotAllowedForClosedProjects() {
    Pageable projectPage = PageRequest.of(0, 10);
    
    ProjectException exception = assertThrows(ProjectException.class,
        () -> projectService.getProjectPage(
            projectPage,
            true,
            true,
            null,
            null,
            null,
            null));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("Internal parameters for filters are not allowed for closed projects", exception.getMessage());
  }
  
  @Test
  void getProjectDtoPage_ShouldReturnPageDtoOfProjectShortDto() {
    //given
    Pageable projectPage = PageRequest.of(0, 10);
    Boolean isEnded = false;
    Boolean isOnlyFirstOpReadyToAcceptance = true;
    
    Page<Project> projectPageResult = new PageImpl<>(List.of(project), projectPage, 1);
    
    PageDto<ProjectShortDto> expectedPageDto = new PageDto<>(
        List.of(),
        projectPageResult.getTotalElements(),
        projectPage.getPageSize(),
        projectPage.getPageNumber());
    
    //when
    when(projectService.getProjectPage(
        projectPage,
        isEnded,
        isOnlyFirstOpReadyToAcceptance,
        null,
        null,
        null,
        null
    )).thenReturn(projectPageResult);
    
    when(pageToPageDtoMapper.projectPageToPageDto(projectPageResult)).thenReturn(expectedPageDto);
    
    PageDto<ProjectShortDto> result = projectService.getProjectDtoPage(
        projectPage,
        isEnded,
        isOnlyFirstOpReadyToAcceptance,
        null,
        null,
        null,
        null
    );
    
    //then
    verify(pageToPageDtoMapper).projectPageToPageDto(projectPageResult);
    assertEquals(expectedPageDto, result);
  }
  
  @Test
  void getAvailableProjects_ShouldReturnListOfProjectAvailableForEmpDto() {
    //given
    long employeeId = 1;
    Employee employee = new Employee();
    employee.setId(employeeId);
    
    TypeWork typeWork1 = new TypeWork();
    typeWork1.setId(1L);
    TypeWork typeWork2 = new TypeWork();
    typeWork2.setId(2L);
    
    employee.setTypeWorks(Set.of(typeWork1, typeWork2));
    
    Project project1 = new Project();
    project1.setId(1L);
    project1.setEndDateInContract(LocalDateTime.now().plusDays(20));
    Project project2 = new Project();
    project2.setId(2L);
    project2.setEndDateInContract(LocalDateTime.now().plusDays(30));
    
    //when
    when(employeeService.getEmployeeById(employeeId)).thenReturn(employee);
    when(projectRepository.findAvailableProjectsByTypeWork(anyLong())).thenReturn(List.of(project1, project2));
    
    List<ProjectAvailableForEmpDto> result = projectService.getAvailableProjects(employeeId);
    
    //then
    verify(employeeService).getEmployeeById(employeeId);
    verify(projectRepository, times(2)).findAvailableProjectsByTypeWork(anyLong());
    assertEquals(4, result.size());
  }
  
  
  @Test
  void finishProject_ShouldSetIsEndedAndRealEndDate() {
    //define a flexible argument matcher for LocalDateTime
    ArgumentMatcher<LocalDateTime> dateTimeMatcher = actualDateTime ->
        actualDateTime.isAfter(LocalDateTime.now().minusSeconds(1)) &&
            actualDateTime.isBefore(LocalDateTime.now().plusSeconds(1));
    
    //then
    projectService.finishProject(projectId);
    
    verify(projectRepository).updateIsEndedAndRealEndDateById(
        eq(true),
        argThat(dateTimeMatcher),
        eq(projectId)
    );
  }
  
  @Test
  void checkAndUpdateProjectEndDateAfterFinishOperation_ShouldUpdatePlusHoursProjectEndDate() {
    //given
    Operation operation = new Operation();
    LocalDateTime plannedEndDate = LocalDateTime.now().minusHours(2);
    operation.setPlannedEndDate(plannedEndDate);
    
    LocalDateTime currentEndDate = LocalDateTime.now();
    project.setPlannedEndDate(currentEndDate);
    project.setId(projectId);
    operation.setProject(project);
    
    //when
    projectService.checkAndUpdateProjectEndDateAfterFinishOperation(operation);
    
    ArgumentCaptor<LocalDateTime> newPlannedEndDateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
    verify(projectRepository).updatePlannedEndDateById(newPlannedEndDateCaptor.capture(), eq(project.getId()));
    
    LocalDateTime newPlannedEndDate = newPlannedEndDateCaptor.getValue();
    
    //then
    assertEquals(currentEndDate.plusHours(2), newPlannedEndDate);
  }
  
  @Test
  void checkAndUpdateProjectEndDateAfterFinishOperation_ShouldUpdateMinusHoursProjectEndDate() {
    //given
    Operation operation = new Operation();
    LocalDateTime plannedEndDate = LocalDateTime.now().plusHours(2);
    operation.setPlannedEndDate(plannedEndDate);
    
    LocalDateTime currentEndDate = LocalDateTime.now();
    project.setPlannedEndDate(currentEndDate);
    project.setId(projectId);
    operation.setProject(project);
    
    //when
    projectService.checkAndUpdateProjectEndDateAfterFinishOperation(operation);
    
    ArgumentCaptor<LocalDateTime> newPlannedEndDateCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
    verify(projectRepository).updatePlannedEndDateById(newPlannedEndDateCaptor.capture(), eq(project.getId()));
    
    LocalDateTime newPlannedEndDate = newPlannedEndDateCaptor.getValue();
    
    //then
    assertEquals(currentEndDate.minusHours(2), newPlannedEndDate);
  }
  
  @Test
  void checkAndUpdateProjectEndDateAfterFinishOperation_ShouldReturnFromMethod() {
    //given
    Operation operation = new Operation();
    LocalDateTime plannedEndDate = LocalDateTime.now();
    operation.setPlannedEndDate(plannedEndDate);
    
    LocalDateTime currentEndDate = LocalDateTime.now();
    project.setPlannedEndDate(currentEndDate);
    project.setId(projectId);
    operation.setProject(project);
    
    //when
    projectService.checkAndUpdateProjectEndDateAfterFinishOperation(operation);
    
    //then
    verify(projectRepository, never()).updatePlannedEndDateById(LocalDateTime.now(), projectId);
  }
  
  @Test
  void getChangingEndDatesResp_ShouldReturnChangingEndDatesResp() {
    //given
    ChangingEndDatesResp expectedResp = new ChangingEndDatesResp(projectId, LocalDateTime.now());
    
    //when
    when(projectRepository.findChangedPlannedEndDateById(projectId)).thenReturn(expectedResp);
    
    ChangingEndDatesResp result = projectService.getChangingEndDatesResp(projectId);
    
    //then
    verify(projectRepository).findChangedPlannedEndDateById(projectId);
    assertEquals(expectedResp, result);
  }
  
  @Test
  void testUpdateEndDates() {
    //given
    ChangingEndDatesReq req = new ChangingEndDatesReq(projectId, LocalDateTime.now().plusDays(15));
    ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
    
    project.setStartDate(startDate);
    project.setEndDateInContract(endDateInContract);
    project.setPlannedEndDate(plannedEndDate);
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    projectService.updateEndDates(req);
    
    //then
    verify(projectRepository).save(projectCaptor.capture());
    
    Project updatedProject = projectCaptor.getValue();
    assertEquals(req.newPlannedAndContractEndDate(), updatedProject.getEndDateInContract());
    assertEquals(req.newPlannedAndContractEndDate(), updatedProject.getPlannedEndDate());
    
    long expectedPeriod = ChronoUnit.HOURS.between(project.getStartDate(), req.newPlannedAndContractEndDate());
    
    assertEquals(expectedPeriod, updatedProject.getPeriod());
    verify(projectRepository).findById(req.projectId());
    verify(projectRepository).save(updatedProject);
  }
  
  @Test
  void testUpdateEndDates_WithCalcNewPeriodAfterChangingEndDates_WithAllEnded() {
    //given
    ChangingEndDatesReq req = new ChangingEndDatesReq(projectId, LocalDateTime.now().plusDays(15));
    ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
    
    Operation o1 = new Operation();
    o1.setEnded(true);
    Operation o2 = new Operation();
    o2.setEnded(true);
    
    project.setStartDate(startDate);
    project.setEndDateInContract(endDateInContract);
    project.setPlannedEndDate(plannedEndDate);
    project.setOperations(List.of(o1,o2));
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    projectService.updateEndDates(req);
    
    //then
    verify(projectRepository).save(projectCaptor.capture());
    
    Project updatedProject = projectCaptor.getValue();
    assertEquals(req.newPlannedAndContractEndDate(), updatedProject.getEndDateInContract());
    assertEquals(req.newPlannedAndContractEndDate(), updatedProject.getPlannedEndDate());
    
    long expectedPeriod = ChronoUnit.HOURS.between(project.getStartDate(), req.newPlannedAndContractEndDate());
    
    assertEquals(expectedPeriod, updatedProject.getPeriod());
    verify(projectRepository).findById(req.projectId());
    verify(projectRepository).save(updatedProject);
  }
  
  @Test
  void testUpdateEndDates_WithCalcNewPeriodAfterChangingEndDates_WithLastOneInWork() {
    //given
    ChangingEndDatesReq req = new ChangingEndDatesReq(projectId, LocalDateTime.now().plusDays(15));
    ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
    
    Operation o1 = new Operation();
    o1.setEnded(true);
    Operation o2 = new Operation();
    o2.setEnded(false);
    o2.setInWork(true);
    
    project.setStartDate(startDate);
    project.setEndDateInContract(endDateInContract);
    project.setPlannedEndDate(plannedEndDate);
    project.setOperations(List.of(o1,o2));
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    projectService.updateEndDates(req);
    
    //then
    verify(projectRepository).save(projectCaptor.capture());
    
    Project updatedProject = projectCaptor.getValue();
    assertEquals(req.newPlannedAndContractEndDate(), updatedProject.getEndDateInContract());
    assertEquals(req.newPlannedAndContractEndDate(), updatedProject.getPlannedEndDate());
    
    long expectedPeriod = ChronoUnit.HOURS.between(project.getStartDate(), req.newPlannedAndContractEndDate());
    
    assertEquals(expectedPeriod, updatedProject.getPeriod());
    verify(projectRepository).findById(req.projectId());
    verify(projectRepository).save(updatedProject);
  }
  
  @Test
  void testUpdateEndDates_ShouldThrowExceptionCriticalError() {
    //given
    ChangingEndDatesReq req = new ChangingEndDatesReq(projectId, LocalDateTime.now().plusDays(15));
    
    Operation o1 = new Operation();
    Operation o2 = new Operation();
    
    project.setStartDate(startDate);
    project.setEndDateInContract(endDateInContract);
    project.setPlannedEndDate(plannedEndDate);
    project.setOperations(List.of(o1,o2));
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    ProjectException exception = assertThrows(ProjectException.class, () -> projectService.updateEndDates(req));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("Incorrect state of project operations. Critical error", exception.getMessage());
  }
  
  @Test
  void testUpdateEndDates_ShouldThrowExceptionWhenCheckNewDateEarlyOldDate() {
    //given
    ChangingEndDatesReq req = new ChangingEndDatesReq(projectId, LocalDateTime.now().plusDays(5));
    
    project.setStartDate(startDate);
    project.setEndDateInContract(endDateInContract);
    project.setPlannedEndDate(plannedEndDate);
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    ProjectException exception = assertThrows(ProjectException.class, () -> projectService.updateEndDates(req));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("The new planned and contract end date must not be earlier than the "
        + "current date under the contract", exception.getMessage());
  }
  
  @Test
  void testUpdateEndDates_ShouldThrowExceptionWhenProjectIsEnded() {
    //given
    ChangingEndDatesReq req = new ChangingEndDatesReq(projectId, LocalDateTime.now().plusDays(5));
    
    project.setEnded(true);
    project.setStartDate(startDate);
    project.setEndDateInContract(endDateInContract);
    project.setPlannedEndDate(plannedEndDate);
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    ProjectException exception = assertThrows(ProjectException.class, () -> projectService.updateEndDates(req));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("The planned and contract end date cannot be changed in a "
        + "completed project", exception.getMessage());
  }
  
  @Test
  void testUpdateEndDates_ShouldThrowExceptionWhenNewDateIsBeforeCurrentDatePlus24hours() {
    //given
    ChangingEndDatesReq req = new ChangingEndDatesReq(projectId, LocalDateTime.now());
    
    project.setStartDate(startDate);
    project.setEndDateInContract(endDateInContract.minusDays(12));
    project.setPlannedEndDate(plannedEndDate);
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    ProjectException exception = assertThrows(ProjectException.class, () -> projectService.updateEndDates(req));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("The new planned and contract end date must not be earlier"
        + " than the current date + 24 hours", exception.getMessage());
  }
  
  @Test
  void testUpdateEndDates_ShouldThrowExceptionSuchDate() {
    //given
    ChangingEndDatesReq req = new ChangingEndDatesReq(projectId, endDateInContract);
    
    project.setStartDate(startDate);
    project.setEndDateInContract(endDateInContract);
    project.setPlannedEndDate(plannedEndDate);
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    ProjectException exception = assertThrows(ProjectException.class, () -> projectService.updateEndDates(req));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("The project planned and contract end date must not match an existing one",
        exception.getMessage());
  }
  
  @Test
  void testUpdateEndDates_ShouldThrowExceptionNewDateIsTooBig() {
    //given
    ChangingEndDatesReq req = new ChangingEndDatesReq(projectId, LocalDateTime.now().plusHours(8761));
    
    project.setStartDate(startDate);
    project.setEndDateInContract(endDateInContract);
    project.setPlannedEndDate(plannedEndDate);
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    ProjectException exception = assertThrows(ProjectException.class, () -> projectService.updateEndDates(req));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("The planned and contract end date cannot be more than "
        + "start date of project + 1 year (or 8760 hours)", exception.getMessage());
  }
  
  @Test
  void testUpdatePlannedEndDateAfterInsertDeleteOp() {
    //given
    LocalDateTime plannedEndDate = LocalDateTime.now().plusDays(10);
    project.setPlannedEndDate(plannedEndDate);
    project.setOperationPeriod(240);
    
    boolean isIncreased = true;
    boolean shipmentIsAdded = true;
    
    //when
    projectService.updatePlannedEndDateAfterInsertDeleteOp(project, isIncreased, shipmentIsAdded);
    
    //then
    LocalDateTime expectedEndDate = plannedEndDate.plusHours(project.getOperationPeriod()).plusHours(SHIPMENT_PERIOD);
    assertEquals(expectedEndDate, project.getPlannedEndDate());
    
    verify(projectRepository).save(project);
  }
  
  @Test
  void testUpdatePlannedEndDateAfterInsertDeleteOp_WithFalseFlags() {
    //given
    LocalDateTime plannedEndDate = LocalDateTime.now().plusDays(10);
    project.setPlannedEndDate(plannedEndDate);
    project.setOperationPeriod(240);
    
    boolean isIncreased = false;
    boolean shipmentIsAdded = false;
    
    //when
    projectService.updatePlannedEndDateAfterInsertDeleteOp(project, isIncreased, shipmentIsAdded);
    
    //then
    verify(projectRepository).save(project);
  }
  
  @Test
  void testUpdateStartFirstOperationDate() {
    //given
    long operationId = 1;
    
    //when
    projectService.updateStartFirstOperationDate(operationId);
    
    //then
    verify(projectRepository).updateStartFirstOperationDateByOperationId(operationId);
  }
  
  @Test
  void testGetChangingCommonDataResp() {
    //given
    ChangingCommonDataResp expectedData = new ChangingCommonDataResp(
        projectId, projectNumber, name, customer, null);
    
    //when
    when(projectRepository.findChangedCommonDataById(projectId)).thenReturn(expectedData);
    
    ChangingCommonDataResp result = projectService.getChangingCommonDataResp(projectId);
    
    //then
    assertEquals(expectedData, result);
    verify(projectRepository).findChangedCommonDataById(projectId);
  }
  
  @Test
  void testCheckAvailableUpdateCommonData_NoDataAvailable() {
    //given
    ChangingCommonDataReq req = new ChangingCommonDataReq(
        projectId, null, null, null, null);
    
    //then
    assertThrows(ProjectException.class, () -> projectService.checkAvailableUpdateCommonData(req));
  }
  
  @Test
  void testCheckAvailableUpdateCommonData_WithOnlyNumber() {
    //given
    ChangingCommonDataReq req = new ChangingCommonDataReq(
        projectId, projectNumber, null, null, null);
    
    //then
    assertDoesNotThrow(() -> projectService.checkAvailableUpdateCommonData(req));
  }
  
  @Test
  void testCheckAvailableUpdateCommonData_WithOnlyName() {
    //given
    ChangingCommonDataReq req = new ChangingCommonDataReq(
        projectId, null, name, null, null);
    
    //then
    assertDoesNotThrow(() -> projectService.checkAvailableUpdateCommonData(req));
  }
  
  @Test
  void testCheckAvailableUpdateCommonData_WithCustomerAndComment() {
    //given
    ChangingCommonDataReq req = new ChangingCommonDataReq(
        projectId, null, null, customer, "Comment");
    
    //then
    assertDoesNotThrow(() -> projectService.checkAvailableUpdateCommonData(req));
  }
  
  @Test
  void testUpdateCommonData() {
    //given
    ChangingCommonDataReq req = new ChangingCommonDataReq(projectId, projectNumber, name, customer, "Comment");
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    projectService.updateCommonData(req);
    
    //then
    verify(projectRepository).save(project);
  }
  
  @Test
  void testUpdateCommonData_WithNullComment() {
    //given
    ChangingCommonDataReq req = new ChangingCommonDataReq(projectId, projectNumber, name, customer, null);
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    projectService.updateCommonData(req);
    
    //then
    verify(projectRepository).save(project);
  }
  
  @Test
  void testUpdateCommonData_ShouldThrowExceptionWithSuchComment() {
    //given
    project.setComment("Comment");
    ChangingCommonDataReq req = new ChangingCommonDataReq(projectId, projectNumber, name, customer, "Comment");
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    ProjectException exception = assertThrows(ProjectException.class, () -> projectService.updateCommonData(req));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("The project commentary info must not match an existing one", exception.getMessage());
  }
  
  @Test
  void testUpdateCommonData_WithNullCustomer() {
    //given
    ChangingCommonDataReq req = new ChangingCommonDataReq(projectId, projectNumber, name, null, null);
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    projectService.updateCommonData(req);
    
    //then
    verify(projectRepository).save(project);
  }
  
  @Test
  void testUpdateCommonData_ShouldThrowExceptionWithSuchCustomer() {
    //given
    project.setCustomer(customer);
    ChangingCommonDataReq req = new ChangingCommonDataReq(projectId, null, null, customer, null);
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    ProjectException exception = assertThrows(ProjectException.class, () -> projectService.updateCommonData(req));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("The project customer info must not match an existing one", exception.getMessage());
  }
  
  @Test
  void testUpdateCommonData_ShouldThrowExceptionWithSuchName() {
    //given
    project.setName(name);
    ChangingCommonDataReq req = new ChangingCommonDataReq(projectId, null, name, null, null);
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    ProjectException exception = assertThrows(ProjectException.class, () -> projectService.updateCommonData(req));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("The project name must not match an existing one", exception.getMessage());
  }
  
  @Test
  void testUpdateCommonData_ShouldThrowExceptionWithSuchNumber() {
    //given
    project.setNumber(projectNumber);
    ChangingCommonDataReq req = new ChangingCommonDataReq(projectId, projectNumber, null, null, null);
    
    //when
    when(projectRepository.findById(req.projectId())).thenReturn(Optional.ofNullable(project));
    
    ProjectException exception = assertThrows(ProjectException.class, () -> projectService.updateCommonData(req));
    
    //then
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    assertEquals("The project number must not match an existing one", exception.getMessage());
  }
  
  @Test
  void testCheckExistsProjectById_ProjectNotFound() {
    //given
    when(projectRepository.existsById(projectId)).thenReturn(false);
    
    ProjectException exception = assertThrows(ProjectException.class, () -> projectService.checkExistsProjectById(projectId));
    
    //then
    verify(projectRepository).existsById(projectId);
    assertEquals("Project with ID: " + projectId + " not found", exception.getMessage());
  }
  
  @Test
  void testCheckExistsProjectById_ProjectFound() {
    //given
    when(projectRepository.existsById(projectId)).thenReturn(true);
    
    //then
    assertDoesNotThrow(() -> projectService.checkExistsProjectById(projectId));
  }
  
}
