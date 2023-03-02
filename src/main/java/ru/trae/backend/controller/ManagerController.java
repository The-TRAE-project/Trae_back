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
import ru.trae.backend.dto.jwt.JwtResponse;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.dto.manager.ManagerRegisterDto;
import ru.trae.backend.entity.user.Manager;
import ru.trae.backend.service.ManagerService;

/**
 * Controller class for operations related to managers.
 *
 * @author Vladimir Olennikov
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager")
public class ManagerController {
  private final ManagerService managerService;

  /**
   * Registers a new manager.
   *
   * @param dto the data transfer object containing the manager data
   * @return the response entity with the jwt token
   */
  @PostMapping("/register")
  public ResponseEntity<JwtResponse> register(@RequestBody ManagerRegisterDto dto) {
    managerService.checkAvailableUsername(dto.username());
    return ResponseEntity.ok(managerService.saveNewManager(dto));
  }

  /**
   * Gets a manager by its id.
   *
   * @param managerId the id of the manager
   * @return the response entity with the manager data
   */
  @GetMapping("/{managerId}")
  private ResponseEntity<ManagerDto> manager(@PathVariable long managerId) {
    Manager m = managerService.getManagerById(managerId);
    return ResponseEntity.ok(managerService.convertFromManager(m));
  }

  @GetMapping("/managers")
  public ResponseEntity<List<ManagerDto>> managers() {
    return ResponseEntity.ok(managerService.getAllManagers());
  }
}
