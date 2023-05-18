/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.dto.mapper;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.working_shift.WorkingShiftDto;
import ru.trae.backend.entity.WorkingShift;

/**
 * This class is a mapper for WorkingShift entities to WorkingShiftDto objects.
 * It uses the TimeControlMapper to map the list of TimeControls from WorkingShift entities.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class WorkingShiftDtoMapper implements Function<WorkingShift, WorkingShiftDto> {

  private final TimeControlMapper timeControlMapper;

  @Override
  public WorkingShiftDto apply(WorkingShift ws) {
    return new WorkingShiftDto(
        ws.getStartShift(),
        ws.getEndShift(),
        ws.isEnded(),
        ws.getTimeControls().stream()
            .map(timeControlMapper)
            .toList()
    );
  }
}
