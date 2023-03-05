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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
  @PostMapping("/new")
  public ResponseEntity<HttpStatus> typeWorkPersist(@RequestBody NewTypeWorkDto dto) {
    typeWorkService.checkAvailableByName(dto.name());
    typeWorkService.saveNewTypeWork(dto);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
