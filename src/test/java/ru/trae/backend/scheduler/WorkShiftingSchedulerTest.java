/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.scheduler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.trae.backend.service.WorkingShiftService;

class WorkShiftingSchedulerTest {
  private WorkingShiftService workingShiftService;
  private WorkShiftingScheduler workShiftingScheduler;

  @BeforeEach
  public void setup() {
    workingShiftService = mock(WorkingShiftService.class);
    workShiftingScheduler = new WorkShiftingScheduler(workingShiftService);
  }

  @Test
  void workShiftingDayHandler_WhenNoActiveWorkingShift_ShouldCreateNewWorkingShift() {
    //when
    when(workingShiftService.existsActiveWorkingShift()).thenReturn(false);

    workShiftingScheduler.workShiftingDayHandler();

    //then
    verify(workingShiftService).createWorkingShift();
  }

  @Test
  void workShiftingDayHandler_WhenActiveWorkingShiftExists_ShouldNotCreateNewWorkingShift() {
    when(workingShiftService.existsActiveWorkingShift()).thenReturn(true);

    workShiftingScheduler.workShiftingDayHandler();

    verify(workingShiftService).createWorkingShift();
  }

  @Test
  void workShiftingDayHandler_WhenNoActiveWorkingShift_ShouldCloseExistingWorkingShift() {
    //when
    when(workingShiftService.existsActiveWorkingShift()).thenReturn(false);

    workShiftingScheduler.workShiftingDayHandler();

    //then
    verify(workingShiftService).closeWorkingShift();
  }

  @Test
  void workShiftingDayEndHandler_ShouldCloseWorkingShift() {
    //when
    workShiftingScheduler.workShiftingDayEndHandler();

    //then
    verify(workingShiftService).closeWorkingShift();
  }

  @Test
  void createWorkingShiftAfterInit_WhenNoActiveWorkingShiftAndCurrentTimeIsWithinShiftHours_ShouldCreateNewWorkingShift() {
    //when
    when(workingShiftService.existsActiveWorkingShift()).thenReturn(false);

    workShiftingScheduler.createWorkingShiftAfterInit();

    //then
    verify(workingShiftService).createWorkingShift();
  }

  @Test
  void createWorkingShiftAfterInit_WhenActiveWorkingShiftExists_ShouldNotCreateNewWorkingShift() {
    //when
    when(workingShiftService.existsActiveWorkingShift()).thenReturn(true);

    workShiftingScheduler.createWorkingShiftAfterInit();

    //then
    verify(workingShiftService, never()).createWorkingShift();
  }

  @Test
  void createWorkingShiftAfterInit_WhenActiveWorkingShiftExistsButNotCurrentDate_ShouldClosePreviousShiftAndCreateNewWorkingShift() {
    //when
    when(workingShiftService.existsByIsEndedFalseAndStartShiftNotCurrentDate()).thenReturn(true);

    workShiftingScheduler.createWorkingShiftAfterInit();

    //then
    verify(workingShiftService, times(1)).closeWorkingShift();
    verify(workingShiftService, times(1)).createWorkingShift();
  }
}

