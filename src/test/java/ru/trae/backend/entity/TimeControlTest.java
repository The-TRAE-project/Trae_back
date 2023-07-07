/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import ru.trae.backend.entity.user.Employee;

class TimeControlTest {

  @Test
  void setId_AfterCreation_ShouldSetId() {
    //given
    TimeControl timeControl = new TimeControl();

    //when
    timeControl.setId(1L);

    //then
    assertEquals(1L, timeControl.getId());
  }

  @Test
  void setIsOnShift_AfterCreation_ShouldSetIsOnShift() {
    //given
    TimeControl timeControl = new TimeControl();

    //when
    timeControl.setOnShift(true);

    //then
    assertTrue(timeControl.isOnShift());
  }

  @Test
  void setAutoClosingShift_AfterCreation_ShouldSetAutoClosingShift() {
    //given
    TimeControl timeControl = new TimeControl();

    //when
    timeControl.setAutoClosingShift(true);

    //then
    assertTrue(timeControl.isAutoClosingShift());
  }

  @Test
  void setArrival_AfterCreation_ShouldSetArrival() {
    //given
    TimeControl timeControl = new TimeControl();
    LocalDateTime arrival = LocalDateTime.now();

    //when
    timeControl.setArrival(arrival);

    //then
    assertEquals(arrival, timeControl.getArrival());
  }

  @Test
  void setDeparture_AfterCreation_ShouldSetDeparture() {
    //given
    TimeControl timeControl = new TimeControl();
    LocalDateTime departure = LocalDateTime.now();

    //when
    timeControl.setDeparture(departure);

    //then
    assertEquals(departure, timeControl.getDeparture());
  }

  @Test
  void setEmployee_AfterCreation_ShouldSetEmployee() {
    //given
    TimeControl timeControl = new TimeControl();
    Employee employee = new Employee();

    //when
    timeControl.setEmployee(employee);

    //then
    assertEquals(employee, timeControl.getEmployee());
  }

  @Test
  void setWorkingShift_AfterCreation_ShouldSetWorkingShift() {
    //given
    TimeControl timeControl = new TimeControl();
    WorkingShift workingShift = new WorkingShift();

    //when
    timeControl.setWorkingShift(workingShift);

    //then
    assertEquals(workingShift, timeControl.getWorkingShift());
  }
}
