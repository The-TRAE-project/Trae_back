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

import static ru.trae.backend.service.OperationService.SHIPMENT_PERIOD;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.operation.OperationForReportDto;
import ru.trae.backend.dto.project.ProjectForReportDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.util.Util;

/**
 * A mapper for mapping a {@link Project} to a {@link ProjectForReportDto}.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class ProjectForReportDtoMapper implements Function<Project, ProjectForReportDto> {
  private final OperationForReportDtoMapper operationForReportDtoMapper;

  @Override
  public ProjectForReportDto apply(Project p) {

    List<Operation> operations = p.getOperations().stream()
            .sorted(Util::prioritySorting)
            .toList();
    LocalDateTime currentDate = LocalDateTime.now();
    List<OperationForReportDto> operationForReportDtoList = new ArrayList<>();
    for (int i = 0; i < operations.size(); i++) {
      Operation o = operations.get(i);
      //изменяет планируюмую дату окончания операции на текущую, в случаи возникновения задержки FIXME:
      if (o.getPlannedEndDate() != null && o.getPlannedEndDate().toLocalDate().isBefore(currentDate.toLocalDate()) && !o.isEnded()) {
        o.setPlannedEndDate(currentDate.plusHours(SHIPMENT_PERIOD));
      }
      if (o.getStartDate() == null) {
        if (i == 0) {
          //обработка случая, когда первая операция не имеет предыдущей операции
          o.setStartDate(p.getStartFirstOperationDate());
        } else {
          o.setStartDate(operations.get(i - 1).getPlannedEndDate());
        }
        //проверка на последнюю операцию в списке
        //если это отгрузка, то добавляется другое количество часов
        if (i == operations.size() - 1) {
          o.setPlannedEndDate(o.getStartDate().plusHours(SHIPMENT_PERIOD));
        } else {
          o.setPlannedEndDate(o.getStartDate().plusHours(p.getOperationPeriod()));
        }
      }
      operationForReportDtoList.add(operationForReportDtoMapper.apply(o));
    }

    return new ProjectForReportDto(
            p.getId(),
            p.getNumber(),
            p.getName(),
            p.getStartDate(),
            p.getStartFirstOperationDate(),
            p.getPlannedEndDate(),
            p.getEndDateInContract(),
            p.getRealEndDate(),
            p.isEnded(),
            p.getOperationPeriod(),
            operationForReportDtoList,
            p.getCustomer(),
            p.getComment() != null ? p.getComment() : null
    );
  }
}
