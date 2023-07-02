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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import ru.trae.backend.dto.TimeControlDto;
import ru.trae.backend.dto.employee.ShortEmployeeDto;
import ru.trae.backend.entity.TimeControl;
import ru.trae.backend.entity.user.Employee;

class TimeControlMapperTest {
  
  @Test
  void testApply() {
    //given
    TimeControlMapper mapper = new TimeControlMapper();
    
    TimeControl tc = new TimeControl();
    tc.setOnShift(true);
    tc.setAutoClosingShift(false);
    tc.setArrival(LocalDateTime.of(2023, 6, 1, 9, 0));
    tc.setDeparture(LocalDateTime.of(2023, 6, 1, 17, 0));
    
    Employee e = new Employee();
    e.setId(1L);
    e.setFirstName("John");
    e.setLastName("Doe");
    tc.setEmployee(e);
    
    //when
    TimeControlDto result = mapper.apply(tc);
    
    //then
    assertEquals(tc.isOnShift(), result.isOnShift());
    assertEquals(tc.isAutoClosingShift(), result.autoClosingShift());
    assertEquals(tc.getArrival(), result.arrival());
    assertEquals(tc.getDeparture(), result.departure());
    
    ShortEmployeeDto expectedEmployeeDto = new ShortEmployeeDto(
        e.getId(), e.getFirstName(), e.getLastName(), tc.isOnShift());
    assertEquals(expectedEmployeeDto, result.employee());
  }
}
