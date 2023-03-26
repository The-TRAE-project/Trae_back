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
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.type.ChangeNameAndActiveReq;
import ru.trae.backend.dto.type.NewTypeWorkDto;
import ru.trae.backend.dto.type.TypeWorkDto;
import ru.trae.backend.service.TypeWorkService;

/**
 * TypeWorkController is a REST controller for managing type-work related operations. It provides
 * endpoints for retrieving
 * a list of types, as well as creating new types of work.
 *
 * @author Vladimir Olennikov
 */
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/type-work")
public class TypeWorkController {
  private final TypeWorkService typeWorkService;

  /**
   * This endpoint is used to retrieve a list of types of work.
   *
   * @return a list of type work dtos
   */
  @Operation(summary = "Список типов работ",
      description = "Доступен администратору. Возвращает список типов работ")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Список типов работ",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = List.class))}),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @GetMapping("/types")
  public ResponseEntity<List<TypeWorkDto>> types() {
    return ResponseEntity.ok(typeWorkService.getTypes());
  }

  /**
   * This endpoint is used to create a new type of work.
   *
   * @param dto a dto containing the name of the new type of work
   * @return {@link ResponseEntity} with status code <b>201</b> (Created)
   */
  @Operation(summary = "Добавление нового типа работы",
      description = "Доступен администратору. Возвращает ДТО созданного типа работы")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "ДТО созданного типа работы",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = TypeWorkDto.class))}),
      @ApiResponse(responseCode = "400", description =
          "Неправильные данные для создания типа работы", content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "409", description =
          "Такое названия для типа работы уже используется", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/new")
  public ResponseEntity<TypeWorkDto> typeWorkPersist(@Valid @RequestBody NewTypeWorkDto dto) {
    typeWorkService.checkAvailableByName(dto.name());

    return new ResponseEntity<>(typeWorkService.saveNewTypeWork(dto), HttpStatus.CREATED);
  }

  /**
   * This API is used to change the name and active status of a specified type of work.
   *
   * @param request A request with the type of work id, new name and active status
   * @return The type of work corresponding to the given id
   */
  @Operation(summary = "Изменение названия типа работы, отключение/включение типа работы",
      description =
          "Доступен администратору. Изменяет название и/или отключает/включает тип работы")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Возвращает ДТО с измененным типом работы",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = TypeWorkDto.class))}),
      @ApiResponse(responseCode = "400", description = "Неправильное новое название типа работы",
          content = @Content),
      @ApiResponse(responseCode = "401", description = "Требуется аутентификация",
          content = @Content),
      @ApiResponse(responseCode = "403", description = "Доступ запрещен",
          content = @Content),
      @ApiResponse(responseCode = "404",
          description = "Тип работы с таким идентификатором не найден", content = @Content),
      @ApiResponse(responseCode = "409", description =
          "Тип работы уже имеет такое название, либо оно занято другим типом работы, "
              + "тип работы уже отключен/включен", content = @Content),
      @ApiResponse(responseCode = "423", description = "Учетная запись заблокирована",
          content = @Content)})
  @PostMapping("/change-name-active")
  public ResponseEntity<TypeWorkDto> typeWorkChange(
      @Valid @RequestBody ChangeNameAndActiveReq request) {
    typeWorkService.changeNameAndActive(request);

    return ResponseEntity.ok(typeWorkService.getTypeWorkDtoById(request.typeWorkId()));
  }
}
