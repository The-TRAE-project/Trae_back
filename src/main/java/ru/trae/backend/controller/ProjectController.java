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

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  @PostMapping("/new")
  public ResponseEntity<ProjectDto> projectPersist(@RequestBody NewProjectDto dto) {
    ProjectDto projectDto = projectService.convertFromProject(projectService.saveNewProject(dto));
    return ResponseEntity.ok(projectDto);
  }

  @GetMapping("/{projectId}")
  public ResponseEntity<ProjectDto> project(@PathVariable long projectId) {
    return ResponseEntity.ok(projectService.getProjectDtoById(projectId));
  }

  @GetMapping("/projects")
  public ResponseEntity<List<ProjectDto>> projects() {
    return ResponseEntity.ok(projectService.getAllProjects());
  }

  @GetMapping("/employee/available-projects/{employeeId}")
  public ResponseEntity<List<ProjectAvailableForEmpDto>> availableProjectsByEmpId(
          @PathVariable long employeeId) {
    return ResponseEntity.ok(projectService.getAvailableProjects(employeeId));
  }
}
