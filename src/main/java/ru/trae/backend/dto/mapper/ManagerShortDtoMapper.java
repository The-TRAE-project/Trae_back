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
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.manager.ManagerShortDto;
import ru.trae.backend.entity.user.Manager;

/**
 * The ManagerShortDtoMapper is a class responsible for mapping a Manager object to its
 * corresponding ManagerShortDto object.
 *
 * @author Vladimir Olennikov
 */
@Service
public class ManagerShortDtoMapper implements Function<Manager, ManagerShortDto> {
  @Override
  public ManagerShortDto apply(Manager m) {
    return new ManagerShortDto(
        m.getId(),
        m.getLastName(),
        m.getFirstName()
    );
  }
}
