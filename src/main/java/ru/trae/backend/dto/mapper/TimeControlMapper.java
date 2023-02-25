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
import ru.trae.backend.dto.TimeControlDto;
import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.entity.TimeControl;
import ru.trae.backend.entity.user.Employee;

/**
 * The TimeControlMapper class is a service that provides a function to map a TimeControl
 * object to a TimeControlDto object.
 * It requires the use of constructor injection.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class TimeControlMapper implements Function<TimeControl, TimeControlDto> {

  @Override
  public TimeControlDto apply(TimeControl tc) {
    Employee e = tc.getEmployee();

    return new TimeControlDto(
            tc.isOnShift(),
            tc.isAutoClosingShift(),
            tc.getArrival(),
            tc.getDeparture(),
            new ShortEmployeeDto(e.getId(), e.getFirstName(), e.getLastName()));
  }
}
