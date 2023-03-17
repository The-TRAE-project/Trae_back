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

import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.manager.ManagerDto;
import ru.trae.backend.entity.user.Manager;

/**
 * The ManagerDtoMapper is a class responsible for mapping a Manager object to its corresponding
 * ManagerDto object.
 *
 * @author Vladimir Olennikov
 */
@Service
public class ManagerDtoMapper implements Function<Manager, ManagerDto> {
  @Override
  public ManagerDto apply(Manager m) {
    return new ManagerDto(
        m.getId(),
        m.getFirstName(),
        m.getMiddleName(),
        m.getLastName(),
        m.getPhone(),
        m.getRole().value,
        m.getDateOfRegister().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        m.getDateOfEmployment().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
        m.getDateOfDismissal() != null
            ? m.getDateOfEmployment().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null
    );
  }
}
