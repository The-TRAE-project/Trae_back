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
import ru.trae.backend.dto.project.ProjectDto;
import ru.trae.backend.entity.task.Project;

/**
 * A mapper for mapping a {@link Project} to a {@link ProjectDto}.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class ProjectDtoMapper implements Function<Project, ProjectDto> {
  private final ManagerDtoMapper managerDtoMapper;
  private final OperationDtoMapper operationDtoMapper;

  @Override
  public ProjectDto apply(Project p) {
    return new ProjectDto(
        p.getId(),
        p.getNumber(),
        p.getName(),
        p.getStartDate(),
        p.getPlannedEndDate(),
        p.getRealEndDate(),
        p.getPeriod(),
        p.isEnded(),
        p.getOperations().stream()
            .map(operationDtoMapper)
            .toList(),
        managerDtoMapper.apply(p.getManager()),
        p.getComment() != null ? p.getComment() : null
    );
  }
}
