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

import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.dto.project.ProjectAvailableForEmpDto;
import ru.trae.backend.dto.project.ProjectDto;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.service.ProjectService;

/**
 * Controller class for managing {@link Project}s.
 *
 * @author Vladimir Olennikov
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {

  private final ProjectService projectService;

  /**
   * Endpoint for saving a new project.
   *
   * @param dto the {@link NewProjectDto} provided from the client
   * @param principal the {@link Principal} provided from the client
   * @return {@link ResponseEntity<HttpStatus>} with status code <b>201</b> (Created)
   */
  @PostMapping("/new")
  public ResponseEntity<HttpStatus> projectPersist(
          @RequestBody NewProjectDto dto, Principal principal) {
    projectService.saveNewProject(dto, principal.getName());
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  /**
   * Get project by id.
   *
   * @param projectId Unique project identifier
   * @return ProjectDto
   */
  @GetMapping("/{projectId}")
  public ResponseEntity<ProjectDto> project(@PathVariable long projectId) {
    return ResponseEntity.ok(projectService.getProjectDtoById(projectId));
  }

  /**
   * Get all projects.
   *
   * @return a list of all projects
   */
  @GetMapping("/projects")
  public ResponseEntity<List<ProjectDto>> projects() {
    return ResponseEntity.ok(projectService.getAllProjects());
  }

  /**
   * Get list of available projects for employee.
   *
   * @param employeeId employee id
   * @return list of projects
   */
  @GetMapping("/employee/available-projects/{employeeId}")
  public ResponseEntity<List<ProjectAvailableForEmpDto>> availableProjectsByEmpId(
          @PathVariable long employeeId) {
    return ResponseEntity.ok(projectService.getAvailableProjects(employeeId));
  }

  /**
   * Finish project by id.
   *
   * @param projectId id of project
   * @return status code
   */
  @GetMapping("/finish-project/{projectId}")
  public ResponseEntity<HttpStatus> finishProject(@PathVariable long projectId) {
    projectService.finishProject(projectId);
    return ResponseEntity.ok().build();
  }

  /**
   * Delete project by id.
   *
   * @param projectId the project id
   * @return the response entity
   */
  @DeleteMapping("/delete-project/{projectId}")
  public ResponseEntity<HttpStatus> deleteProject(@PathVariable long projectId) {
    projectService.deleteProject(projectId);
    return ResponseEntity.ok().build();
  }
}
