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

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trae.backend.dto.WorkingShiftDto;
import ru.trae.backend.service.WorkingShiftService;

/**
 * WorkingShiftController is used to provide endpoints to handle requests related to WorkingShift
 * objects.
 *
 * @author Vladimir Olennikov
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/working-shift")
public class WorkingShiftController {
  private final WorkingShiftService workingShiftService;

  @GetMapping("/active")
  public ResponseEntity<WorkingShiftDto> activeWorkingShift() {
    return ResponseEntity.ok(workingShiftService.getActive());
  }

  @GetMapping("/on-shift/{employeeId}")
  public ResponseEntity<Boolean> statusEmployee(@PathVariable long employeeId) {
    return ResponseEntity.ok(workingShiftService.employeeOnShift(true, employeeId));
  }
}
