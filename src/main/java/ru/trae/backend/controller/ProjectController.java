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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.dto.project.ProjectAvailableForEmpDto;
import ru.trae.backend.dto.project.ProjectDto;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.service.ProjectService;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Controller class for managing {@link Project}s.
 *
 * @author Vladimir Olennikov
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/project")
public class ProjectController {

  private final ProjectService projectService;

  /**
   * Endpoint for saving a new project.
   *
   * @param dto       the {@link NewProjectDto} provided from the client
   * @param principal the {@link Principal} provided from the client
   * @return {@link ResponseEntity} with status code <b>201</b> (Created)
   */
  @Operation(summary = "Создание нового проекта",
      description = "Доступен администратору и конструкторам. Возвращает статус 201")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "Статус 201 - проект успешно создан и сохранен в бд",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = HttpStatus.class))}),
      @ApiResponse(responseCode = "400",
          description = "Неправильные исходные данные нового проекта",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/new")
  public ResponseEntity<HttpStatus> projectPersist(
      @Valid @RequestBody NewProjectDto dto, @ApiIgnore Principal principal) {
    projectService.saveNewProject(dto, principal.getName());
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  /**
   * Get project by id.
   *
   * @param projectId Unique project identifier
   * @return ProjectDto
   */
  @Operation(summary = "Данные проекта",
      description = "Доступен администратору. Возвращает ДТО проекта")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "ДТО проекта",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ProjectDto.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат идентификатора",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Проект с таким идентификатором не найден", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
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
  @Operation(summary = "Список проектов доступных конкретному работнику",
      description = "Доступен сотрудникам. Возвращает список проектов, "
          + "в которых есть доступные для принятия операции(этапы) "
          + "согласно типам работ конкретного работника. "
          + "Возвращает пустой список, если такие проекты не найдены в системе.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список доступных проектов",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = List.class))}),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/employee/available-projects/{employeeId}")
  public ResponseEntity<List<ProjectAvailableForEmpDto>> availableProjectsByEmpId(
      @PathVariable long employeeId) {
    return ResponseEntity.ok(projectService.getAvailableProjects(employeeId));
  }

  /**
   * This method is used for finish a project.
   *
   * @param projectId the project's id
   * @return ResponseEntity with status ok
   */
  @Operation(summary = "Закрывает проект",
      description = "Доступен администратору. Закрывает проект, возвращает статус 200.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Возвращает статус 200 при успешном закрытии проекта",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = HttpStatus.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат идентификатора",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Проект с таким идентификатором не найден", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/finish-project")
  public ResponseEntity<HttpStatus> finishProject(
      @RequestParam(value = "projectId") long projectId) {
    projectService.checkExistsProjectById(projectId);
    projectService.finishProject(projectId);
    return ResponseEntity.ok().build();
  }

  /**
   * Delete project by id.
   *
   * @param projectId the project id
   * @return the response entity
   */
  @Operation(summary = "Удаляет проект",
      description = "Доступен администратору. Удаляет проект и все операции(этапы) "
          + "связанные с ним, возвращает статус 204.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204",
          description = "Возвращает статус 204 при успешном удалении проекта",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = HttpStatus.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат идентификатора",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Проект с таким идентификатором не найден", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @DeleteMapping("/delete-project/{projectId}")
  public ResponseEntity<HttpStatus> deleteProject(@PathVariable long projectId) {
    projectService.deleteProject(projectId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
