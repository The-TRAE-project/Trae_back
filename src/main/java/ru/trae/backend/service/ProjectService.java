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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.mapper.ProjectAvailableDtoMapper;
import ru.trae.backend.dto.mapper.ProjectDtoMapper;
import ru.trae.backend.dto.operation.NewOperationDto;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.dto.project.ProjectAvailableForEmpDto;
import ru.trae.backend.dto.project.ProjectDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.ProjectException;
import ru.trae.backend.repository.ProjectRepository;
import ru.trae.backend.util.Util;

/**
 * A service class that provides methods for managing {@link Project} entities.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class ProjectService {
  private final ProjectRepository projectRepository;
  private final ManagerService managerService;
  private final OperationService operationService;
  private final EmployeeService employeeService;
  private final ProjectDtoMapper projectDtoMapper;
  private final ProjectAvailableDtoMapper projectAvailableDtoMapper;

  /**
   * Saves a new {@link Project} to the database.
   *
   * @param dto the {@link NewProjectDto} object containing the data of the new {@link Project}
   *            the {@link ProjectDto} object for the given {@link Project}
   */
  public void saveNewProject(NewProjectDto dto, String authUsername) {
    checkOperationsNotEmpty(dto.operations());

    Project p = new Project();

    p.setNumber(dto.number());
    p.setName(dto.name());
    p.setPeriod(dto.period());
    p.setStartDate(LocalDateTime.now());
    p.setPlannedEndDate(LocalDateTime.now().plusDays(dto.period()));
    p.setRealEndDate(null);
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
                    "Project with ID: " + id + " not found"));
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
  public void checkAndUpdateProjectEndDate(Operation o) {
    if (LocalDateTime.now().isBefore(o.getPlannedEndDate())) {
      return;
    }

    long hours = HOURS.between(o.getPlannedEndDate(), o.getRealEndDate());
    Project p = o.getProject();
    LocalDateTime newPlannedEndDate = p.getPlannedEndDate().plusHours(hours);

    updatePlannedEndDate(newPlannedEndDate, p.getId());
  }

  /**
   * Updates the planned end date for a {@link Project} with the given id.
   *
   * @param newPlannedEndDate the new planned end date for the {@link Project}
   * @param projectId         the id of the {@link Project} to be updated
   */
  public void updatePlannedEndDate(LocalDateTime newPlannedEndDate, long projectId) {
    projectRepository.updatePlannedEndDateById(newPlannedEndDate, projectId);
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

  private void checkOperationsNotEmpty(List<NewOperationDto> operations) {
    if (operations == null || operations.isEmpty()) {
      throw new ProjectException(HttpStatus.BAD_REQUEST, "List of operations cannot be empty");
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
