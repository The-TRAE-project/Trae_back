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

class ReportParameterTest {
  
  @Test
  void testEnumValues() {
    //given
    ReportParameter[] parameters = ReportParameter.values();
    
    //then
    assertEquals(3, parameters.length);
    assertEquals(ReportParameter.PROJECT, parameters[0]);
    assertEquals(ReportParameter.OPERATION, parameters[1]);
    assertEquals(ReportParameter.EMPLOYEE, parameters[2]);
  }
  
  @Test
  void testEnumValueOf() {
    //given
    ReportParameter projectParameter = ReportParameter.valueOf("PROJECT");
    ReportParameter operationParameter = ReportParameter.valueOf("OPERATION");
    ReportParameter employeeParameter = ReportParameter.valueOf("EMPLOYEE");
    
    //then
    assertEquals(ReportParameter.PROJECT, projectParameter);
    assertEquals(ReportParameter.OPERATION, operationParameter);
    assertEquals(ReportParameter.EMPLOYEE, employeeParameter);
  }
  
  @Test
  void testEnumValueOf_InvalidName_ThrowsIllegalArgumentException() {
    //then
    assertThrows(IllegalArgumentException.class, () -> ReportParameter.valueOf("INVALID"));
  }
}
