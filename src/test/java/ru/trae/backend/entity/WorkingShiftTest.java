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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WorkingShiftTest {

  @Test
  void setId_AfterSaving_ShouldSetId() {
    //given
    WorkingShift ws = new WorkingShift();
    ws.setId(1L);

    //then
    assertNotNull(ws.getId());
    assertTrue(ws.getId() > 0);
  }

  @Test
  void equals_SameObject_ShouldReturnTrue() {
    //given
    WorkingShift ws = new WorkingShift();
    ws.setId(1L);

    //then
    assertEquals(ws, ws);
  }

  @Test
  void equals_NullObject_ShouldReturnFalse() {
    //given
    WorkingShift ws = new WorkingShift();
    ws.setId(1L);

    //then
    assertNotEquals(null, ws);
  }

  @Test
  void equals_DifferentClassObject_ShouldReturnFalse() {
    //given
    WorkingShift ws = new WorkingShift();
    ws.setId(1L);

    String otherObject = "another_text";

    //then
    assertFalse(ws.equals(otherObject));
  }

  @Test
  void equals_EqualObjects_ShouldReturnTrue() {
    //given
    WorkingShift ws1 = new WorkingShift();
    ws1.setId(1L);

    WorkingShift ws2 = new WorkingShift();
    ws2.setId(1L);

    //then
    assertEquals(ws1, ws2);
  }

  @Test
  void equals_DifferentId_ShouldReturnFalse() {
    //given
    WorkingShift ws1 = new WorkingShift();
    ws1.setId(1L);

    WorkingShift ws2 = new WorkingShift();
    ws2.setId(2L);

    //then
    assertNotEquals(ws1, ws2);
  }

  @Test
  void hashCode_EqualObjects_ShouldReturnSameHashCode() {
    //given
    WorkingShift ws1 = new WorkingShift();
    ws1.setId(1L);

    WorkingShift ws2 = new WorkingShift();
    ws2.setId(1L);

    //then
    assertEquals(ws1.hashCode(), ws2.hashCode());
  }
}
