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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.trae.backend.dto.mapper.WorkingShiftDtoMapper;
import ru.trae.backend.dto.workingshift.WorkingShiftDto;
import ru.trae.backend.entity.TimeControl;
import ru.trae.backend.entity.WorkingShift;
import ru.trae.backend.entity.user.Employee;
import ru.trae.backend.exceptionhandler.exception.WorkingShiftException;
import ru.trae.backend.projection.WorkingShiftEmployeeDto;
import ru.trae.backend.repository.WorkingShiftRepository;

@ExtendWith(MockitoExtension.class)
class WorkingShiftServiceTest {
  @Mock
  private WorkingShiftRepository workingShiftRepository;
  @Mock
  private TimeControlService timeControlService;
  @Mock
  private WorkingShiftDtoMapper workingShiftDtoMapper;
  @InjectMocks
  private WorkingShiftService workingShiftService;

  @Test
  void testCreateWorkingShift() {
    // Mock repository
    WorkingShift workingShift = new WorkingShift();
    when(workingShiftRepository.save(any(WorkingShift.class))).thenReturn(workingShift);

    // Call the method
    workingShiftService.createWorkingShift();

    // Verify that the repository save method was called
    verify(workingShiftRepository).save(any(WorkingShift.class));
  }

  @Test
  void testGetActiveWorkingShiftExists() {
    //given
    WorkingShift workingShift = new WorkingShift();

    //when
    when(workingShiftRepository.existsByIsEndedFalse()).thenReturn(true);
    when(workingShiftRepository.findByIsEndedFalse()).thenReturn(workingShift);

    WorkingShiftDto workingShiftDto = new WorkingShiftDto(
        LocalDateTime.now(), LocalDateTime.now().plusHours(9), false, Collections.emptyList()
    );
    when(workingShiftDtoMapper.apply(workingShift)).thenReturn(workingShiftDto);

    //then
    WorkingShiftDto actualDto = workingShiftService.getActive();

    verify(workingShiftRepository).findByIsEndedFalse();
    verify(workingShiftDtoMapper).apply(workingShift);

    assertEquals(workingShiftDto, actualDto);
  }

  @Test
  void testGetActiveWorkingShiftNotExists() {
    //when
    when(workingShiftRepository.existsByIsEndedFalse()).thenReturn(false);

    //then
    Assertions.assertThrows(WorkingShiftException.class, () -> workingShiftService.getActive());
  }

  @Test
  void testCloseWorkingShiftExists() {
    //given
    WorkingShift workingShift = mock(WorkingShift.class);
    TimeControl timeControl1 = mock(TimeControl.class);
    TimeControl timeControl2 = mock(TimeControl.class);

    //when
    when(workingShiftRepository.existsByIsEndedFalse()).thenReturn(true);
    when(workingShiftRepository.findByIsEndedFalse()).thenReturn(workingShift);

    when(timeControl1.isOnShift()).thenReturn(true);
    when(timeControl2.isOnShift()).thenReturn(false);
    when(workingShift.getTimeControls()).thenReturn(List.of(timeControl1, timeControl2));
    when(workingShift.getStartShift()).thenReturn(LocalDateTime.of(2023, 1, 1, 7, 1, 0));

    workingShiftService.closeWorkingShift();

    //then
    verify(timeControlService).autoClosingShift(timeControl1);
    verify(workingShiftRepository).save(workingShift);
    verify(workingShift).setEnded(true);
    verify(workingShift).setEndShift(any(LocalDateTime.class));
  }

  @Test
  void testCloseWorkingShiftNotExists() {
    //when
    when(workingShiftRepository.existsByIsEndedFalse()).thenReturn(false);

    workingShiftService.closeWorkingShift();

    //then
    verifyNoInteractions(timeControlService);
  }

  @Test
  void testArrivalEmployeeOnShiftExists() {
    //given
    WorkingShift workingShift = mock(WorkingShift.class);
    Employee employee = mock(Employee.class);
    TimeControl timeControl = mock(TimeControl.class);

    //when
    when(workingShiftRepository.existsByIsEndedFalse()).thenReturn(true);
    when(workingShiftRepository.findByIsEndedFalse()).thenReturn(workingShift);
    when(timeControlService.createArrivalTimeControl(
        eq(employee), eq(workingShift), eq(true), any(LocalDateTime.class)))
        .thenReturn(timeControl);

    workingShiftService.arrivalEmployeeOnShift(employee);

    //then
    verify(timeControlService).createArrivalTimeControl(
        eq(employee), eq(workingShift), eq(true), any(LocalDateTime.class));
    verify(workingShiftRepository).save(workingShift);
  }

  @Test
  void testArrivalEmployeeOnShiftNotExists() {
    //when
    when(workingShiftRepository.existsByIsEndedFalse()).thenReturn(false);

    Employee employee = mock(Employee.class);

    //then
    Assertions.assertThrows(WorkingShiftException.class,
        () -> workingShiftService.arrivalEmployeeOnShift(employee));
  }

  @Test
  void testExistsActiveWorkingShift() {
    //when
    when(workingShiftRepository.existsByIsEndedFalse()).thenReturn(true);

    boolean exists = workingShiftService.existsActiveWorkingShift();

    //then
    verify(workingShiftRepository).existsByIsEndedFalse();
    Assertions.assertTrue(exists);
  }

  @Test
  void testEmployeeOnShiftExists() {
    //when
    when(workingShiftRepository.existsEmpOnShift(true, 123L)).thenReturn(true);

    boolean onShift = workingShiftService.employeeOnShift(true, 123L);

    //then
    verify(workingShiftRepository).existsEmpOnShift(true, 123L);
    Assertions.assertTrue(onShift);
  }

  @Test
  void testExistsByIsEndedFalseAndStartShiftNotCurrentDateExists() {
    //when
    when(workingShiftRepository.existsByIsEndedFalseAndStartShiftNotCurrentDate()).thenReturn(true);

    boolean onShift = workingShiftService.existsByIsEndedFalseAndStartShiftNotCurrentDate();

    //then
    verify(workingShiftRepository).existsByIsEndedFalseAndStartShiftNotCurrentDate();
    Assertions.assertTrue(onShift);
  }

  @Test
  void testGetCountEmpsOnActiveWorkingShift() {
    //when
    when(workingShiftRepository.countEmployeeOnActiveWorkingShift()).thenReturn(10L);

    long count = workingShiftService.getCountEmpsOnActiveWorkingShift();

    //then
    verify(workingShiftRepository).countEmployeeOnActiveWorkingShift();
    Assertions.assertEquals(10L, count);
  }

  @Test
  void testEmployeeOnShiftNotExists() {
    //when
    when(workingShiftRepository.existsEmpOnShift(true, 123L)).thenReturn(false);

    boolean onShift = workingShiftService.employeeOnShift(true, 123L);

    //then
    verify(workingShiftRepository).existsEmpOnShift(true, 123L);
    Assertions.assertFalse(onShift);
  }

  @Test
  void testGetWorkingShiftEmployeeByEmpIdsWithEmployeeIds() {
    //given
    List<WorkingShiftEmployeeDto> expectedList = Collections.singletonList(mock(WorkingShiftEmployeeDto.class));
    when(workingShiftRepository.getWorkingShiftsDatesByEmpIds(any(LocalDate.class), any(LocalDate.class),
        any(Set.class))).thenReturn(expectedList);

    List<WorkingShiftEmployeeDto> actualList = workingShiftService.getWorkingShiftEmployeeByEmpIds(
        LocalDate.now(), LocalDate.now(), Set.of(1L, 2L, 3L));

    //then
    verify(workingShiftRepository).getWorkingShiftsDatesByEmpIds(any(LocalDate.class), any(LocalDate.class), any(Set.class));
    assertEquals(expectedList, actualList);
  }

  @Test
  void testGetWorkingShiftEmployeeByEmpIdsWithoutEmployeeIds() {
    //given
    List<WorkingShiftEmployeeDto> expectedList = Collections.singletonList(mock(WorkingShiftEmployeeDto.class));
    when(workingShiftRepository.getWorkingShiftsDates(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(expectedList);

    List<WorkingShiftEmployeeDto> actualList = workingShiftService.getWorkingShiftEmployeeByEmpIds(
        LocalDate.now(), LocalDate.now(), null);

    //then
    verify(workingShiftRepository).getWorkingShiftsDates(any(LocalDate.class), any(LocalDate.class));
    assertEquals(expectedList, actualList);
  }

  @Test
  void testGetWorkingShiftEmployeeByEmpIdsWithEmptyEmployeeIds() {
    //given
    List<WorkingShiftEmployeeDto> expectedList = Collections.singletonList(mock(WorkingShiftEmployeeDto.class));
    when(workingShiftRepository.getWorkingShiftsDates(any(LocalDate.class), any(LocalDate.class)))
        .thenReturn(expectedList);

    List<WorkingShiftEmployeeDto> actualList = workingShiftService.getWorkingShiftEmployeeByEmpIds(
        LocalDate.now(), LocalDate.now(), Set.of());

    //then
    verify(workingShiftRepository).getWorkingShiftsDates(any(LocalDate.class), any(LocalDate.class));
    assertEquals(expectedList, actualList);
  }
}
