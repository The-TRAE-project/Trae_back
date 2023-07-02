/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import ru.trae.backend.dto.operation.OperationDto;
import ru.trae.backend.dto.operation.OperationForReportDto;

class UtilTest {
  
  @Test
  void utilityClass_ThrowsIllegalStateException() {
    assertThrows(IllegalStateException.class, Util::new);
  }
  
  @Test
  void testPrioritySorting_ForOperationDto() {
    //given
    OperationDto o1 = new OperationDto(
        1L, 2, "operation_1", null, null,
        null, null, 0, null,
        false, false, false, 0,
        "type_work_1", null);
    
    OperationDto o2 = new OperationDto(
        2L, 1, "operation_2", null, null,
        null, null, 0, null,
        false, false, false, 0,
        "type_work_2", null);
    
    //when
    int result = Util.prioritySorting(o1, o2);
    
    //then
    assertEquals(1, result);
  }
  
  @Test
  void testPrioritySorting_ForOperationForReportDto() {
    //given
    OperationForReportDto o1 = new OperationForReportDto(
        1L, 2, "Operation 1", null, null,
        null, null, false, false, false);
    
    OperationForReportDto o2 = new OperationForReportDto(
        2L, 1, "Operation 2", null, null,
        null, null, false, false, false);
    
    //when
    int result = Util.prioritySorting(o1, o2);
    
    //then
    assertEquals(1, result);
  }
  
}

