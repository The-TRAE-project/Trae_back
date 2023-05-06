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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import ru.trae.backend.dto.PageDto;
import ru.trae.backend.dto.project.ChangingCommonDataReq;
import ru.trae.backend.dto.project.ChangingCommonDataResp;
import ru.trae.backend.dto.project.ChangingEndDatesReq;
import ru.trae.backend.dto.project.ChangingEndDatesResp;
import ru.trae.backend.dto.project.NewProjectDto;
import ru.trae.backend.dto.project.ProjectAvailableForEmpDto;
import ru.trae.backend.dto.project.ProjectDto;
import ru.trae.backend.dto.project.ProjectShortDto;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.service.ProjectService;
import ru.trae.backend.util.PageSettings;
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
   * Gets a page of projects.
   *
   * @param pageSetting                    the page settings
   * @param isEnded                        filter by open/closed status
   * @param isOnlyFirstOpWithoutAcceptance a boolean flag indicating if the {@code Project}
   *                                       has first operation without acceptance
   * @param isOnlyLastOpInWork             a boolean flag indicating if the {@code Project}
   *                                       has last operation in work
   * @return a page of short project dtos
   */
  @Operation(summary = "Список проектов с пагинацией, сортировкой и фильтрацией",
      description = "Доступен администратору. Возвращает список ДТО проектов с сортировкой по "
          + "контрактной дате окончания, с возможностью фильтрации по статусу(открыт/закрыт) "
          + "проекта, по первой, не взятой в работу, операции, "
          + "по последней операции, взятой в работу.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список ДТО проектов. "
          + "В примере указан единичный объект из списка",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ProjectShortDto.class))}),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/projects")
  public ResponseEntity<PageDto<ProjectShortDto>> projects(
      @Valid PageSettings pageSetting,
      @RequestParam(required = false) @Parameter(description =
          "Фильтрация по статусу открыт/закрыт") Boolean isEnded,
      @RequestParam(required = false) @Parameter(
          description = "Фильтрация по первому, непринятому в работу, этапу проекта")
      Boolean isOnlyFirstOpWithoutAcceptance,
      @RequestParam(required = false) @Parameter(
          description = "Фильтрация по последнему, находящемуся в работе, этапу проекта")
      Boolean isOnlyLastOpInWork) {
    
    Sort projectSort = pageSetting.buildProjectSort();
    Pageable projectPage = PageRequest.of(
        pageSetting.getPage(), pageSetting.getElementPerPage(), projectSort);
    
    return ResponseEntity.ok(projectService.getProjectDtoPage(
        projectPage, isEnded, isOnlyFirstOpWithoutAcceptance, isOnlyLastOpInWork));
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
      @ApiResponse(responseCode = "200",
          description = "Список доступных проектов. В примере указан единичный объект из списка",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ProjectAvailableForEmpDto.class))}),
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
  @Operation(summary = "Закрытие проекта",
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
  @Operation(summary = "Удаление проекта",
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
  
  /**
   * Update common data of project.
   *
   * @param request the request
   * @return the response entity
   */
  @Operation(summary = "Изменение общих сведений проекта", description =
      "Доступен администратору. Изменяет общие сведения указанного проекта")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Возвращает ДТО с общими сведениями проекта",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ChangingCommonDataResp.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат новых данных",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Проект с таким идентификатором не найден", content = @Content),
      @ApiResponse(responseCode = "409", description = "Проект уже имеет такое название, номер, "
          + "данные заказчика или комментарий",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/update-common-data")
  public ResponseEntity<ChangingCommonDataResp> updateCommonData(
      @Valid @RequestBody ChangingCommonDataReq request) {
    projectService.checkAvailableUpdateCommonData(request);
    projectService.updateCommonData(request);
    return ResponseEntity.ok(projectService.getChangingCommonDataResp(request.projectId()));
  }
  
  @Operation(summary = "Изменение планируемой и контрактной даты окончания проекта", description =
      "Доступен администратору. Изменяет планируемую и контрактную дату окончания проекта")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200",
          description = "Возвращает ДТО с планируемой/контрактной датой окончания проекта",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ChangingEndDatesResp.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильный формат новых данных",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Проект с таким идентификатором не найден. "
              + "Нельзя изменить дату окончания в закрытом проекте. "
              + "Дата окончания не может быть меньше, чем планируемая дата окончания этапа "
              + "находящегося в работе или доступного для принятия + 2 запасных дня.",
          content = @Content),
      @ApiResponse(responseCode = "409",
          description = "Проект уже имеет такую дату окончания проекта, номер",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/update-end-dates")
  public ResponseEntity<ChangingEndDatesResp> updateEndDates(
      @Valid @RequestBody ChangingEndDatesReq request) {
    projectService.updateEndDates(request);
    return ResponseEntity.ok(projectService.getChangingEndDatesResp(request.projectId()));
  }
  
  /**
   * Find a page of projects with parameters.
   *
   * @param pageSetting             the page settings
   * @param projectNumberOrCustomer the number or customer data associated with the {@code Project}
   * @return a page of short project dtos
   */
  @Operation(summary = "Поиск проектов с пагинацией и сортировкой",
      description = "Доступен администратору. Возвращает результаты поиска(список ДТО проектов с "
          + "сортировкой по дате окончания) по номеру проекта или данным заказчика")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список ДТО проектов. "
          + "В примере указан единичный объект из списка",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ProjectShortDto.class))}),
      @ApiResponse(responseCode = "400",
          description = "Поисковой запрос пустой, короче 1 знака или длиннее 30 знаков",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/search")
  public ResponseEntity<PageDto<ProjectShortDto>> searchProjects(
      @Valid PageSettings pageSetting,
      @RequestParam(name = "projectNumberOrCustomer")
      @Parameter(description = "Номер проекта или данные заказчика")
      @NotBlank @Size(min = 1, max = 30) String projectNumberOrCustomer) {
    
    Sort projectSort = pageSetting.buildProjectSort();
    Pageable projectPage = PageRequest.of(
        pageSetting.getPage(), pageSetting.getElementPerPage(), projectSort);
    
    return ResponseEntity.ok(
        projectService.findProjectByNumberOrCustomer(projectPage, projectNumberOrCustomer));
  }
}
