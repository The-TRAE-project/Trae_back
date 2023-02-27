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
import ru.trae.backend.dto.project.ProjectAvailableForEmpDto;
import ru.trae.backend.entity.task.Operation;
import ru.trae.backend.entity.task.Project;

/**
 * This class is responsible for mapping a {@link Project} object to a
 * {@link ProjectAvailableForEmpDto} object.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class ProjectAvailableDtoMapper implements Function<Project, ProjectAvailableForEmpDto> {

  @Override
  public ProjectAvailableForEmpDto apply(Project p) {
    return new ProjectAvailableForEmpDto(
            p.getId(),
            p.getNumber(),
            p.getOrder().getCustomer().getLastName(),
            p.getName(),
            p.getOperations().stream()
                    .filter(Operation::isReadyToAcceptance)
                    .findFirst()
                    .get().getName()
    );
  }
}
