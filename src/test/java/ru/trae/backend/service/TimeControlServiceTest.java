/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.trae.backend.entity.TimeControl;
import ru.trae.backend.entity.WorkingShift;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.repository.TimeControlRepository;

class TimeControlServiceTest {
  
  @Test
  void createArrivalTimeControl_ShouldSaveTimeControlWithCorrectValues() {
    //given
    TimeControlRepository timeControlRepository = mock(TimeControlRepository.class);
    TimeControlService timeControlService = new TimeControlService(timeControlRepository);
    Employee employee = new Employee();
    WorkingShift workingShift = new WorkingShift();
    boolean onShift = true;
    LocalDateTime time = LocalDateTime.now();
    TimeControl expectedTimeControl = new TimeControl();
    ArgumentCaptor<TimeControl> timeControlCaptor = ArgumentCaptor.forClass(TimeControl.class);
    
    //when
    when(timeControlRepository.save(timeControlCaptor.capture())).thenReturn(expectedTimeControl);
    
    TimeControl result = timeControlService.createArrivalTimeControl(employee, workingShift, onShift, time);
    
    //then
    verify(timeControlRepository).save(timeControlCaptor.capture());
    assertEquals(expectedTimeControl, result);
    
    TimeControl capturedTimeControl = timeControlCaptor.getValue();
    assertEquals(time, capturedTimeControl.getArrival());
    assertNull(capturedTimeControl.getDeparture());
    assertEquals(employee, capturedTimeControl.getEmployee());
    assertEquals(onShift, capturedTimeControl.isOnShift());
    assertFalse(capturedTimeControl.isAutoClosingShift());
    assertEquals(workingShift, capturedTimeControl.getWorkingShift());
  }
  
  @Test
  void updateTimeControlForDeparture_ShouldUpdateTimeControlWithCorrectValues() {
    //given
    TimeControlRepository timeControlRepository = mock(TimeControlRepository.class);
    TimeControlService timeControlService = new TimeControlService(timeControlRepository);
    Long empId = 1L;
    LocalDateTime time = LocalDateTime.now();
    TimeControl timeControl = new TimeControl();
    
    //when
    when(timeControlRepository.findByEmployeeIdAndIsOnShiftTrueAndWorkingShiftIsEndedFalse(empId))
        .thenReturn(timeControl);
    
    timeControlService.updateTimeControlForDeparture(empId, time);
    
    //then
    verify(timeControlRepository).save(timeControl);
    assertEquals(time, timeControl.getDeparture());
    assertFalse(timeControl.isOnShift());
  }
  
  @Test
  void autoClosingShift_ShouldUpdateTimeControlWithCorrectValues() {
    //given
    TimeControlRepository timeControlRepository = mock(TimeControlRepository.class);
    TimeControlService timeControlService = new TimeControlService(timeControlRepository);
    TimeControl timeControl = new TimeControl();
    
    //when
    timeControlService.autoClosingShift(timeControl);
    
    //then
    verify(timeControlRepository).save(timeControl);
    assertFalse(timeControl.isOnShift());
    assertTrue(timeControl.isAutoClosingShift());
  }
}
