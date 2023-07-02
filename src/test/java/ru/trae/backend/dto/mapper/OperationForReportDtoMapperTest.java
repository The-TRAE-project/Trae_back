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
import ru.trae.backend.dto.operation.OperationForReportDto;
import ru.trae.backend.entity.task.Operation;

 class OperationForReportDtoMapperTest {
  
  @Test
  void testApply() {
    //given
    OperationForReportDtoMapper mapper = new OperationForReportDtoMapper();
    
    Operation o = new Operation();
    o.setId(1L);
    o.setPriority(2);
    o.setName("Test Operation");
    o.setStartDate(LocalDateTime.of(2023, 1, 1, 9, 0));
    o.setAcceptanceDate(LocalDateTime.of(2023, 1, 5, 12, 0));
    o.setPlannedEndDate(LocalDateTime.of(2023, 1, 10, 18, 0));
    o.setRealEndDate(LocalDateTime.of(2023, 1, 12, 14, 0));
    o.setEnded(true);
    o.setInWork(false);
    o.setReadyToAcceptance(true);
    
    //when
    OperationForReportDto result = mapper.apply(o);
    
    //then
    assertEquals(o.getId(), result.id());
    assertEquals(o.getPriority(), result.priority());
    assertEquals(o.getName(), result.name());
    assertEquals(o.getStartDate(), result.startDate());
    assertEquals(o.getAcceptanceDate(), result.acceptanceDate());
    assertEquals(o.getPlannedEndDate(), result.plannedEndDate());
    assertEquals(o.getRealEndDate(), result.realEndDate());
    assertEquals(o.isEnded(), result.isEnded());
    assertEquals(o.isInWork(), result.inWork());
    assertEquals(o.isReadyToAcceptance(), result.readyToAcceptance());
  }
}
