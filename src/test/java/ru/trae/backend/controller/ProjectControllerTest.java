/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.project.ChangingCommonDataReq;
import ru.trae.backend.dto.project.ChangingCommonDataResp;
import ru.trae.backend.dto.project.ChangingEndDatesReq;
import ru.trae.backend.dto.project.ChangingEndDatesResp;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.dto.project.ProjectAvailableForEmpDto;
import ru.trae.backend.dto.project.ProjectDto;
import ru.trae.backend.dto.project.ProjectShortDto;
import ru.trae.backend.projection.ProjectIdNumberDto;
import ru.trae.backend.service.ProjectService;
import ru.trae.backend.util.PageSettings;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {
  
  @Mock
  private ProjectService projectService;
  @InjectMocks
  private ProjectController projectController;
  private final long projectId = 1;
  private final String projectName = "test_project_name";
  private final int projectNumber = 100;
  
  @Test
  void createNewProject_WhenValidDto_ShouldReturnHttpStatusCreated() {
    //given
    NewProjectDto dto = new NewProjectDto(projectNumber, projectName,
        LocalDateTime.now().plusDays(60), "customer", null, Collections.emptyList());
    Principal principal = mock(Principal.class);
    
    //when
    ResponseEntity<HttpStatus> response = projectController.createNewProject(dto, principal);
    
    //then
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    verify(projectService).saveNewProject(dto, principal.getName());
  }
  
  @Test
  void getProject_WhenValidProjectId_ShouldReturnProjectDto() {
    //given
    ProjectDto expectedProjectDto = new ProjectDto(projectId, projectNumber, projectName,
        LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(9), LocalDateTime.now().plusDays(60),
        LocalDateTime.now().plusDays(70), null, 70, 56, false,
        Collections.emptyList(), null, "customer", null);
    
    //when
    when(projectService.getProjectDtoById(projectId)).thenReturn(expectedProjectDto);
    ResponseEntity<ProjectDto> response = projectController.project(projectId);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedProjectDto, response.getBody());
    verify(projectService).getProjectDtoById(projectId);
  }
  
  @Test
  void projectsWithPagination_WhenValidParameters_ShouldReturnPageDto() {
    //given
    PageSettings pageSettings = new PageSettings();
    Boolean isEnded = true;
    Boolean isOnlyFirstOpReadyToAcceptance = true;
    Boolean isOnlyLastOpReadyToAcceptance = true;
    Boolean isCurrentOpInWorkOrReadyToAcceptance = true;
    Boolean isOverdueProject = true;
    Boolean isOverdueCurrentOpInProject = true;
    
    Sort projectSort = pageSettings.buildProjectSort();
    Pageable projectPage = PageRequest.of(
        pageSettings.getPage(), pageSettings.getElementPerPage(), projectSort);
    PageDto<ProjectShortDto> expectedPageDto = new PageDto<>(Collections.emptyList(), 1L, 1L, 1);
    
    //when
    when(projectService.getProjectDtoPage(
        projectPage, isEnded, isOnlyFirstOpReadyToAcceptance, isOnlyLastOpReadyToAcceptance,
        isOverdueCurrentOpInProject, isCurrentOpInWorkOrReadyToAcceptance, isOverdueProject))
        .thenReturn(expectedPageDto);
    
    ResponseEntity<PageDto<ProjectShortDto>> response = projectController.projectsWithPagination(
        pageSettings, isEnded, isOnlyFirstOpReadyToAcceptance, isOnlyLastOpReadyToAcceptance,
        isCurrentOpInWorkOrReadyToAcceptance, isOverdueProject, isOverdueCurrentOpInProject);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedPageDto, response.getBody());
    verify(projectService).getProjectDtoPage(
        projectPage, isEnded, isOnlyFirstOpReadyToAcceptance, isOnlyLastOpReadyToAcceptance,
        isOverdueCurrentOpInProject, isCurrentOpInWorkOrReadyToAcceptance, isOverdueProject);
  }
  
  @Test
  void projectsForReportWithoutPagination_WhenValidParameters_ShouldReturnList() {
    // Given
    LocalDate startOfPeriod = LocalDate.of(2022, 1, 1);
    LocalDate endOfPeriod = LocalDate.of(2022, 12, 31);
    Set<Long> employeeIds = new HashSet<>(List.of(1L, 2L, 3L));
    Set<Long> operationIds = new HashSet<>(List.of(4L, 5L, 6L));
    
    List<ProjectIdNumberDto> expectedList = Collections.emptyList();
    
    //when
    when(projectService.getProjectIdNumberDtoListWithFilters(
        employeeIds, operationIds, startOfPeriod, endOfPeriod))
        .thenReturn(expectedList);
    
    ResponseEntity<List<ProjectIdNumberDto>> response = projectController
        .projectsForReportWithoutPagination(startOfPeriod, endOfPeriod, employeeIds, operationIds);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedList, response.getBody());
    verify(projectService).getProjectIdNumberDtoListWithFilters(
        employeeIds, operationIds, startOfPeriod, endOfPeriod);
  }
  
  @Test
  void availableProjectsByEmpId_WhenValidEmployeeId_ShouldReturnList() {
    //given
    long employeeId = 123L;
    
    List<ProjectAvailableForEmpDto> expectedList = List.of(
        new ProjectAvailableForEmpDto(1L, 100, "customer", "project_name_1", "available_op_1"),
        new ProjectAvailableForEmpDto(2L, 200, "customer", "project_name_2", "available_op_2"),
        new ProjectAvailableForEmpDto(3L, 300, "customer", "project_name_3", "available_op_3")
    );
    
    //when
    when(projectService.getAvailableProjects(employeeId))
        .thenReturn(expectedList);
    
    ResponseEntity<List<ProjectAvailableForEmpDto>> response = projectController
        .availableProjectsByEmpId(employeeId);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedList, response.getBody());
    verify(projectService).getAvailableProjects(employeeId);
  }
  
  @Test
  void finishProject_WhenValidProjectId_ShouldReturnOkStatus() {
    //when
    ResponseEntity<HttpStatus> response = projectController.finishProject(projectId);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(projectService).checkExistsProjectById(projectId);
    verify(projectService).finishProject(projectId);
  }
  
  @Test
  void deleteProject_WhenValidProjectId_ShouldReturnNoContentStatus() {
    //when
    ResponseEntity<HttpStatus> response = projectController.deleteProject(projectId);
    
    //then
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(projectService).deleteProject(projectId);
  }
  
  @Test
  void updateCommonData_WhenValidRequest_ShouldReturnChangingCommonDataResp() {
    //given
    ChangingCommonDataReq request = new ChangingCommonDataReq(projectId, null,
        "another_project_name", null, null);
    
    ChangingCommonDataResp expectedResponse = new ChangingCommonDataResp(projectId, projectNumber,
        "another_project_name", "customer", null);
    
    //when
    when(projectService.getChangingCommonDataResp(request.projectId())).thenReturn(expectedResponse);
    
    ResponseEntity<ChangingCommonDataResp> response = projectController.updateCommonData(request);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedResponse, response.getBody());
    verify(projectService).checkAvailableUpdateCommonData(request);
    verify(projectService).updateCommonData(request);
    verify(projectService).getChangingCommonDataResp(request.projectId());
  }
  
  @Test
  void updateEndDates_WhenValidRequest_ShouldReturnChangingEndDatesResp() {
    //given
    final LocalDateTime newEndDate = LocalDateTime.now().plusDays(40);
    ChangingEndDatesReq request = new ChangingEndDatesReq(projectId, newEndDate);
    
    ChangingEndDatesResp expectedResponse = new ChangingEndDatesResp(projectId, newEndDate);
    
    //when
    when(projectService.getChangingEndDatesResp(request.projectId())).thenReturn(expectedResponse);
    
    ResponseEntity<ChangingEndDatesResp> response = projectController.updateEndDates(request);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedResponse, response.getBody());
    verify(projectService).updateEndDates(request);
    verify(projectService).getChangingEndDatesResp(request.projectId());
  }
  
  @Test
  void searchProjects_WhenValidProjectNumberOrCustomer_ShouldReturnPageDto() {
    //given
    PageSettings pageSettings = new PageSettings();
    String projectNumberOrCustomer = "123";
    
    Sort projectSort = pageSettings.buildProjectSort();
    Pageable projectPage = PageRequest.of(pageSettings.getPage(), pageSettings.getElementPerPage(), projectSort);
    PageDto<ProjectShortDto> expectedPageDto = new PageDto<>(Collections.emptyList(), 1L, 1L, 1);
    
    //when
    when(projectService.findProjectByNumberOrCustomer(projectPage, projectNumberOrCustomer)).thenReturn(expectedPageDto);
    
    ResponseEntity<PageDto<ProjectShortDto>> response = projectController.searchProjects(pageSettings, projectNumberOrCustomer);
    
    //then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(expectedPageDto, response.getBody());
    verify(projectService).findProjectByNumberOrCustomer(projectPage, projectNumberOrCustomer);
  }
}
