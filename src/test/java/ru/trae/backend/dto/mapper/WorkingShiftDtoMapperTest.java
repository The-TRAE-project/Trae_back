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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import ru.trae.backend.dto.TimeControlDto;
import ru.trae.backend.dto.workingshift.WorkingShiftDto;
import ru.trae.backend.entity.TimeControl;
import ru.trae.backend.entity.WorkingShift;

class WorkingShiftDtoMapperTest {
  
  @Test
  void testApply() {
    //given
    TimeControlMapper timeControlMapper = mock(TimeControlMapper.class);
    WorkingShiftDtoMapper mapper = new WorkingShiftDtoMapper(timeControlMapper);
    
    WorkingShift workingShift = new WorkingShift();
    workingShift.setStartShift(LocalDateTime.of(2023, 1, 1, 9, 0));
    workingShift.setEndShift(LocalDateTime.of(2023, 1, 1, 18, 0));
    workingShift.setEnded(true);
    
    TimeControl tc1 = new TimeControl();
    TimeControl tc2 = new TimeControl();
    workingShift.getTimeControls().add(tc1);
    workingShift.getTimeControls().add(tc2);
    
    TimeControlDto tcDto1 = new TimeControlDto(true, false,
        LocalDateTime.of(2023, 1, 1, 9, 0),
        LocalDateTime.of(2023, 1, 1, 12, 0), null);
    TimeControlDto tcDto2 = new TimeControlDto(true, true,
        LocalDateTime.of(2023, 1, 1, 12, 0),
        LocalDateTime.of(2023, 1, 1, 18, 0), null);
    
    //when
    when(timeControlMapper.apply(tc1)).thenReturn(tcDto1);
    when(timeControlMapper.apply(tc2)).thenReturn(tcDto2);
    
    WorkingShiftDto result = mapper.apply(workingShift);
    
    //then
    assertEquals(workingShift.getStartShift(), result.startShift());
    assertEquals(workingShift.getEndShift(), result.endShift());
    assertEquals(workingShift.isEnded(), result.isEnded());
    assertEquals(2, result.timeControls().size());
    assertTrue(result.timeControls().contains(tcDto1));
    assertTrue(result.timeControls().contains(tcDto2));
  }
}
