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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.operation.OperationInfoForProjectTemplateDto;
import ru.trae.backend.dto.project.ProjectShortDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.util.Util;

/**
 * A mapper for mapping a {@link Project} to a {@link ru.trae.backend.dto.project.ProjectShortDto}.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class ProjectShortDtoMapper implements Function<Project, ProjectShortDto> {
  private final OperationInfoForProjectTemplateDtoMapper operationInfoForProjectTemplateDtoMapper;

  @Override
  public ProjectShortDto apply(Project p) {
    OperationInfoForProjectTemplateDto opDto = p.getOperations().stream()
        .filter(Util.opIsAcceptanceOrInWork())
        .findFirst()
        .map(operationInfoForProjectTemplateDtoMapper)
        .orElse(operationInfoForProjectTemplateDtoMapper.apply(
            p.getOperations().stream()
                .sorted(Util::prioritySorting)
                .toList()
                .get(p.getOperations().size() - 1)));

    return new ProjectShortDto(
        p.getId(),
        p.isEnded(),
        checkIsOverdue(p),
        checkIsOverdueByCurrentOp(p.getOperations()),
        p.getNumber(),
        p.getName(),
        p.getCustomer(),
        opDto
    );
  }

  private boolean checkIsOverdue(Project p) {
    boolean result;
    if (p.isEnded()) {
      result = p.getEndDateInContract().isBefore(p.getRealEndDate());
    } else {
      result = p.getEndDateInContract().isBefore(LocalDateTime.now());
    }
    return result;
  }

  private boolean checkIsOverdueByCurrentOp(List<Operation> operations) {
    Optional<Operation> currentOp = operations.stream()
        .filter(o -> o.isInWork() || o.isReadyToAcceptance())
        .findFirst();

    return currentOp.isEmpty() || currentOp.get().getPlannedEndDate().isBefore(LocalDateTime.now());
  }
}
