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

import static java.time.temporal.ChronoUnit.HOURS;
import static ru.trae.backend.service.OperationService.MIN_PERIOD_OPERATION;
import static ru.trae.backend.service.OperationService.SHIPMENT_PERIOD;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.PageDto;
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
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.ProjectException;
import ru.trae.backend.repository.ProjectRepository;
import ru.trae.backend.util.Constant;
import ru.trae.backend.util.Util;

/**
 * A service class that provides methods for managing {@link Project} entities.
 *
 * @author Vladimir Olennikov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
  private final ProjectRepository projectRepository;
  private final ManagerService managerService;
  private final OperationService operationService;
  private final EmployeeService employeeService;
  private final ProjectDtoMapper projectDtoMapper;
  private final ProjectAvailableDtoMapper projectAvailableDtoMapper;
  private final PageToPageDtoMapper pageToPageDtoMapper;
  
  /**
   * Saves a new {@link Project} to the database.
   *
   * @param dto the {@link NewProjectDto} object containing the data of the new {@link Project}
   *            the {@link ProjectDto} object for the given {@link Project}
   */
  public void saveNewProject(NewProjectDto dto, String authUsername) {
    checkOperationsNotEmpty(dto.operations());
    checkCorrectPlannedEndDate(dto.plannedEndDate());
    
    
    Project p = new Project();
    
    p.setNumber(dto.number());
    p.setName(dto.name());
    p.setStartDate(LocalDateTime.now());
    p.setStartFirstOperationDate(null);
    p.setPlannedEndDate(dto.plannedEndDate());
    p.setEndDateInContract(dto.plannedEndDate());
    p.setRealEndDate(null);
    p.setPeriod((int) HOURS.between(p.getStartDate(), p.getPlannedEndDate()));
    int operationPeriod =
        Util.calculateOperationPeriod(p.getPeriod() - SHIPMENT_PERIOD, dto.operations().size());
    checkMinimalPeriodForOperations(operationPeriod);
    p.setOperationPeriod(operationPeriod);
    p.setEnded(false);
    p.setManager(managerService.getManagerByUsername(authUsername));
    p.setCustomer(dto.customer());
    p.setComment(dto.comment());
    
    projectRepository.save(p);
    
    List<Operation> operations = operationService.saveNewOperations(p, dto.operations());
    p.setOperations(operations);
    projectRepository.save(p);
  }
  
  /**
   * Returns a {@link Project} entity with the given id.
   *
   * @param id the id of the requested {@link Project}
   * @return the {@link Project} entity with the given id
   * @throws ProjectException if no {@link Project} with the given id is found
   */
  public Project getProjectById(long id) {
    return projectRepository.findById(id).orElseThrow(
        () -> new ProjectException(HttpStatus.NOT_FOUND,
            "Project with ID: " + id + Constant.NOT_FOUND_CONST));
  }
  
  public Project getProjectByOperationId(long operationId) {
    return projectRepository.findByOperations_Id(operationId).orElseThrow(
        () -> new ProjectException(HttpStatus.NOT_FOUND,
            "Project with operation ID: " + operationId + Constant.NOT_FOUND_CONST));
  }
  
  /**
   * Returns a list of all {@link Project} entities from the database.
   *
   * @return a list of all {@link Project} entities
   */
  public List<ProjectDto> getAllProjects() {
    return projectRepository.findAll()
        .stream()
        .map(projectDtoMapper)
        .toList();
  }
  
  public PageDto<ProjectShortDto> findProjectByNumberOrCustomer(
      Pageable projectPage, String projectNumberOrCustomer) {
    return pageToPageDtoMapper.projectPageToPageDto(
        findProjectPage(projectPage, projectNumberOrCustomer));
  }
  
  /**
   * Gets a page of {@link Project} objects according to the given parameters.
   *
   * @param projectPage             the requested page for the {@link Project} objects
   * @param projectNumberOrCustomer the number or customer data associated with the {@link Project}
   * @return a page of {@link Project} objects
   */
  public Page<Project> findProjectPage(Pageable projectPage, String projectNumberOrCustomer) {
    Page<Project> page;
    
    try {
      int number = Integer.parseInt(projectNumberOrCustomer);
      page = projectRepository.findByNumber(number, projectPage);
    } catch (NumberFormatException e) {
      page = projectRepository.findByCustomerLikeIgnoreCase(
          projectNumberOrCustomer.toUpperCase(), projectPage);
    }
    
    return page;
  }
  
  /**
   * Gets a page of {@code Project} objects according to the given parameters.
   *
   * @param projectPage                    the requested page for the {@link Project} objects
   * @param isEnded                        a boolean flag indicating if the {@link Project}
   *                                       is ended or not
   * @param isOnlyFirstOpWithoutAcceptance a boolean flag indicating if the {@link Project}
   *                                       has first operation without acceptance
   * @param isOnlyLastOpInWork             a boolean flag indicating if the {@link Project}
   *                                       has last operation in work
   * @param isOverdueCurrentOpInProject    a boolean flag indicating if the {@link  Project}
   *                                       has overdue current operation in work
   *                                       or ready for acceptance
   * @return a page of {@link Project} objects
   */
  public Page<Project> getProjectPage(
      Pageable projectPage,
      Boolean isEnded,
      Boolean isOnlyFirstOpWithoutAcceptance,
      Boolean isOnlyLastOpInWork,
      Boolean isOverdueCurrentOpInProject) {
    Page<Project> page;
    
    if (Boolean.FALSE.equals(isEnded)) {
      checkOnlyOneInternalParameterForNotEndedProjects(
          isOnlyFirstOpWithoutAcceptance, isOnlyLastOpInWork, isOverdueCurrentOpInProject);
    }
    
    if (Boolean.TRUE.equals(isEnded)) {
      page = projectRepository.findByIsEnded(true, projectPage);
    } else if (Boolean.FALSE.equals(isEnded) && Boolean.TRUE.equals(isOverdueCurrentOpInProject)) {
      page = projectRepository.findProjectsWithOverdueCurrentOperation(
          LocalDateTime.now(), projectPage);
    } else if (Boolean.FALSE.equals(isEnded)
        && Boolean.TRUE.equals(isOnlyFirstOpWithoutAcceptance)) {
      page = projectRepository.findFirstByIsEndedAndOpPriorityAndReadyToAcceptance(0, projectPage);
    } else if (Boolean.FALSE.equals(isEnded) && Boolean.TRUE.equals(isOnlyLastOpInWork)) {
      page = projectRepository.findLastByIsEndedAndOpPriorityAndInWorkTrue(projectPage);
    } else if (Boolean.FALSE.equals(isEnded)) {
      page = projectRepository.findByIsEnded(false, projectPage);
    } else {
      page = projectRepository.findAll(projectPage);
    }
    return page;
  }
  
  private void checkOnlyOneInternalParameterForNotEndedProjects(
      Boolean isOnlyFirstOpWithoutAcceptance,
      Boolean isOnlyLastOpInWork,
      Boolean isOverdueCurrentOpInProject) {
    List<Optional<Boolean>> filterParameters =
        List.of(
            Optional.ofNullable(isOnlyFirstOpWithoutAcceptance),
            Optional.ofNullable(isOnlyLastOpInWork),
            Optional.ofNullable(isOverdueCurrentOpInProject));
    
    if (filterParameters.stream().filter(Optional::isPresent).count() > 1) {
      throw new ProjectException(HttpStatus.BAD_REQUEST,
          "Incorrect number of filters in the request");
    }
  }
  
  /**
   * Accepts pagination settings, filtering parameters, returns {@link  ProjectShortDto}.
   *
   * @param projectPage                    the requested page for the {@link Project} objects
   * @param isEnded                        a boolean flag indicating if the {@link Project}
   *                                       is ended or not
   * @param isOnlyFirstOpWithoutAcceptance a boolean flag indicating if the {@link Project}
   *                                       has first operation without acceptance
   * @param isOnlyLastOpInWork             a boolean flag indicating if the {@link Project}
   *                                       has last operation in work
   * @param isOverdueCurrentOpInProject    a boolean flag indicating if the {@link  Project}
   *                                       has overdue current operation in work
   *                                       or ready for acceptance
   * @return a {@link PageDto} of {@link ProjectShortDto} objects
   */
  public PageDto<ProjectShortDto> getProjectDtoPage(
      Pageable projectPage,
      Boolean isEnded,
      Boolean isOnlyFirstOpWithoutAcceptance,
      Boolean isOnlyLastOpInWork,
      Boolean isOverdueCurrentOpInProject) {
    return pageToPageDtoMapper.projectPageToPageDto(getProjectPage(
        projectPage,
        isEnded,
        isOnlyFirstOpWithoutAcceptance,
        isOnlyLastOpInWork,
        isOverdueCurrentOpInProject));
  }
  
  /**
   * Returns a list of all available {@link Project} entities for a given {@link Employee}.
   *
   * @param employeeId the id of the requested {@link Employee}
   * @return a list of all available {@link Project} entities for the given {@link Employee}
   */
  public List<ProjectAvailableForEmpDto> getAvailableProjects(long employeeId) {
    Employee e = employeeService.getEmployeeById(employeeId);
    List<Project> projects = new ArrayList<>();
    
    e.getTypeWorks().forEach(tw -> projects.addAll(
        projectRepository.findAvailableProjectsByTypeWork(tw.getId())));
    
    return projects.stream()
        .sorted(Util::dateSorting)
        .map(projectAvailableDtoMapper)
        .toList();
  }
  
  /**
   * This method is used to finish a project by setting isEnded to true and setting the realEndDate
   * to the current date.
   *
   * @param projectId the id of the project to be finished
   */
  public void finishProject(long projectId) {
    projectRepository.updateIsEndedAndRealEndDateById(true, LocalDateTime.now(), projectId);
  }
  
  /**
   * This method deletes a project from the database.
   *
   * @param projectId The project id to delete
   */
  public void deleteProject(long projectId) {
    Project p = getProjectById(projectId);
    projectRepository.delete(p);
  }
  
  /**
   * Checks and updates the end date of the project, if necessary.
   *
   * @param o the operation
   */
  public void checkAndUpdateProjectEndDateAfterFinishOperation(Operation o) {
    long hours = HOURS.between(o.getPlannedEndDate(), LocalDateTime.now());
    
    if (hours == 0) {
      return;
    }
    
    Project p = o.getProject();
    LocalDateTime newPlannedEndDate;
    if (hours > 0) {
      newPlannedEndDate = p.getPlannedEndDate().plusHours(hours);
      log.info("the time of the operation has been increased, the planned end date of the project "
          + "will be moved by +{} hours", hours);
    } else {
      newPlannedEndDate = p.getPlannedEndDate().minusHours(Math.abs(hours));
      log.info("the time of the operation has been decreased, the planned end date of the project "
          + "will be moved by {} hours", hours);
    }
    
    projectRepository.updatePlannedEndDateById(newPlannedEndDate, p.getId());
    log.info("the end date of the project has been changed by {} hours", hours);
  }
  
  /**
   * Returns a {@link ProjectDto} object for the {@link Project} with the given id.
   *
   * @param id the id of the requested {@link Project}
   * @return the {@link ProjectDto} object for the {@link Project} with the given id
   */
  public ProjectDto getProjectDtoById(long id) {
    return projectDtoMapper.apply(getProjectById(id));
  }
  
  /**
   * Returns a {@link ProjectDto} object for the given {@link Project}.
   *
   * @param p the {@link Project} to be converted
   * @return the {@link ProjectDto} object for the given {@link Project}
   */
  public ProjectDto convertFromProject(Project p) {
    return projectDtoMapper.apply(p);
  }
  
  public ChangingEndDatesResp getChangingEndDatesResp(long projectId) {
    return projectRepository.findChangedPlannedEndDateById(projectId);
  }
  
  /**
   * Updates the planned and contract end date of the project.
   *
   * @param req Request with new planned and contract end date of the project.
   * @throws ProjectException wrong new planned and contract end date.
   */
  public void updateEndDates(ChangingEndDatesReq req) {
    Project p = getProjectById(req.projectId());
    
    checkCorrectNewPlannedAndContractDate(req, p);
    
    p.setEndDateInContract(req.newPlannedAndContractEndDate());
    p.setPlannedEndDate(req.newPlannedAndContractEndDate());
    p.setPeriod((int) HOURS.between(p.getStartDate(), req.newPlannedAndContractEndDate()));
    
    int period = calculateNewPeriodAfterChangingEndDates(p);
    p.setOperationPeriod(period);
    
    projectRepository.save(p);
  }
  
  private void checkCorrectNewPlannedAndContractDate(ChangingEndDatesReq req, Project p) {
    if (p.isEnded()) {
      throw new ProjectException(HttpStatus.BAD_REQUEST,
          "The planned and contract end date cannot be changed in a completed project");
    }
    
    if (p.getEndDateInContract().equals(req.newPlannedAndContractEndDate())) {
      throw new ProjectException(HttpStatus.BAD_REQUEST,
          "The project planned and contract end date must not match an existing one");
    }
    
    if (req.newPlannedAndContractEndDate().isBefore(p.getEndDateInContract())) {
      throw new ProjectException(HttpStatus.BAD_REQUEST,
          "The new planned and contract end date must not be earlier"
              + " than the current date under the contract");
    }
    
    if (req.newPlannedAndContractEndDate().isBefore(
        LocalDateTime.now().plusHours(MIN_PERIOD_OPERATION))) {
      throw new ProjectException(HttpStatus.BAD_REQUEST,
          "The new planned and contract end date must not be earlier"
              + " than the current date + 24 hours");
    }
    
    if (req.newPlannedAndContractEndDate().isAfter(p.getStartDate().plusHours(8760))) {
      throw new ProjectException(HttpStatus.BAD_REQUEST,
          "The planned and contract end date cannot be more than "
              + "start date of project + 1 year (or 8760 hours)");
    }
  }
  
  private int calculateNewPeriodAfterChangingEndDates(Project p) {
    int period;
    List<Operation> ops = p.getOperations();
    if (ops.stream().allMatch(Operation::isEnded)
        || ops.stream().filter(o -> !o.isEnded()).count() == 1) {
      period = p.getOperationPeriod();
    } else {
      Operation currentOp = ops.stream()
          .filter(o -> o.isInWork() || o.isReadyToAcceptance())
          .findFirst()
          .orElseThrow(() -> new ProjectException(HttpStatus.BAD_REQUEST,
              "Incorrect state of project operations. Critical error"));
      
      int remainingNotEndedOps = (int) ops.stream().filter(o -> !o.isEnded()
          && (!o.isInWork() || !o.isReadyToAcceptance())).count();
      int remainingProjectPeriod;
      if (LocalDateTime.now().isAfter(currentOp.getPlannedEndDate())) {
        remainingProjectPeriod = (int) HOURS.between(LocalDateTime.now(), p.getEndDateInContract());
      } else {
        remainingProjectPeriod =
            (int) HOURS.between(currentOp.getPlannedEndDate(), p.getEndDateInContract());
      }
      period = Util.calculateOperationPeriod(remainingProjectPeriod, remainingNotEndedOps);
      checkMinimalPeriodForOperations(period);
    }
    return period;
  }
  
  public void updatePlannedEndDateAfterInsertDeleteOp(Project p,
                                                      boolean isIncreased,
                                                      boolean shipmentIsAdded) {
    int period = p.getOperationPeriod();
    //добавляется время на новую отгрузку, если предыдущая отгрузка в проекте уже завершена,
    // находится в работе или готова к принятию.
    if (shipmentIsAdded) {
      period += SHIPMENT_PERIOD;
    }
    //флаг isIncreased дает представление, надо увеличить планируемы срок или уменьшить
    if (isIncreased) {
      p.setPlannedEndDate(p.getPlannedEndDate().plusHours(period));
    } else {
      p.setPlannedEndDate(p.getPlannedEndDate().minusHours(period));
    }
    projectRepository.save(p);
  }
  
  public void updateStartFirstOperationDate(long operationId) {
    projectRepository.updateStartFirstOperationDateByOperationId(operationId);
  }
  
  public ChangingCommonDataResp getChangingCommonDataResp(long projectId) {
    return projectRepository.findChangedCommonDataById(projectId);
  }
  
  /**
   * Checks if data for updating is available.
   *
   * @param req The request for changing common data.
   * @throws ProjectException If no data for updating is available.
   */
  public void checkAvailableUpdateCommonData(ChangingCommonDataReq req) {
    if (req.projectNumber() == null && req.projectName() == null
        && req.customer() == null && req.commentary() == null) {
      throw new ProjectException(HttpStatus.BAD_REQUEST, "No data for updating");
    }
  }
  
  /**
   * Updates the common data of a project.
   *
   * @param req the request containing the projectId, projectNumber, projectName, customer
   *            and commentary.
   */
  public void updateCommonData(ChangingCommonDataReq req) {
    Project p = getProjectById(req.projectId());
    updateProjectNumber(p, req.projectNumber());
    updateProjectName(p, req.projectName());
    updateCustomerInfo(p, req.customer());
    updateCommentary(p, req.commentary());
    
    projectRepository.save(p);
  }
  
  private void updateCommentary(Project p, String commentary) {
    if (commentary == null) {
      return;
    }
    
    if (!commentary.equals(p.getComment())) {
      p.setComment(commentary);
    } else {
      throw new ProjectException(HttpStatus.BAD_REQUEST,
          "The project commentary info must not match an existing one");
    }
  }
  
  private void updateCustomerInfo(Project p, String newCustomerInfo) {
    if (newCustomerInfo == null) {
      return;
    }
    
    if (!newCustomerInfo.equals(p.getCustomer())) {
      p.setCustomer(newCustomerInfo);
    } else {
      throw new ProjectException(HttpStatus.BAD_REQUEST,
          "The project customer info must not match an existing one");
    }
  }
  
  private void updateProjectName(Project p, String newName) {
    if (newName == null) {
      return;
    }
    
    if (!newName.equals(p.getName())) {
      p.setName(newName);
    } else {
      throw new ProjectException(HttpStatus.BAD_REQUEST,
          "The project name must not match an existing one");
    }
  }
  
  private void updateProjectNumber(Project p, Integer newProjectNumber) {
    if (newProjectNumber == null) {
      return;
    }
    
    if (newProjectNumber != p.getNumber()) {
      p.setNumber(newProjectNumber);
    } else {
      throw new ProjectException(HttpStatus.BAD_REQUEST,
          "The project number must not match an existing one");
    }
  }
  
  private void checkOperationsNotEmpty(List<NewOperationDto> operations) {
    if (operations == null || operations.isEmpty()) {
      throw new ProjectException(HttpStatus.BAD_REQUEST, "List of operations cannot be empty");
    }
  }
  
  private void checkMinimalPeriodForOperations(int operationPeriod) {
    if (operationPeriod < MIN_PERIOD_OPERATION) {
      throw new ProjectException(HttpStatus.BAD_REQUEST, "The calculated period(" + operationPeriod
          + " hours) for performing operations should not be less than "
          + MIN_PERIOD_OPERATION + " hours");
    }
  }
  
  private void checkCorrectPlannedEndDate(LocalDateTime plannedEndDate) {
    if (plannedEndDate.isBefore(
        LocalDateTime.now().plusHours((long) MIN_PERIOD_OPERATION + SHIPMENT_PERIOD))) {
      throw new ProjectException(HttpStatus.BAD_REQUEST,
          "The planned end date cannot be less than current(start) date of project + "
              + (MIN_PERIOD_OPERATION + SHIPMENT_PERIOD) + " additional hours.");
    }
    if (plannedEndDate.isAfter(LocalDateTime.now().plusHours(8760))) {
      throw new ProjectException(HttpStatus.BAD_REQUEST, "The planned end date cannot be more than "
          + "start date of project + 1 year (or 8760 hours).");
    }
  }
  
  /**
   * This method checks if the project by ID exists in the repository.
   *
   * @param projectId the ID of the project
   * @throws ProjectException if the project does not exist
   */
  public void checkExistsProjectById(long projectId) {
    if (!projectRepository.existsById(projectId)) {
      throw new ProjectException(HttpStatus.NOT_FOUND,
          "Project with ID: " + projectId + " not found");
    }
  }
}
