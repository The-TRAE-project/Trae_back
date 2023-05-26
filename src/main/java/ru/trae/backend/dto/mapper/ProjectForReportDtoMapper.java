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
import ru.trae.backend.dto.project.ProjectForReportDto;
import ru.trae.backend.entity.task.Project;
import ru.trae.backend.util.Util;

/**
 * A mapper for mapping a {@link Project} to a {@link ru.trae.backend.dto.project.ProjectForReportDto}.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class ProjectForReportDtoMapper implements Function<Project, ProjectForReportDto> {
  private final OperationForReportDtoMapper operationForReportDtoMapper;
  
  @Override
  public ProjectForReportDto apply(Project p) {
    
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
        p.getOperations().stream()
            .sorted(Util::prioritySorting)
            .map(operationForReportDtoMapper)
            .toList(),
        p.getCustomer(),
        p.getComment() != null ? p.getComment() : null
    );
  }
}
