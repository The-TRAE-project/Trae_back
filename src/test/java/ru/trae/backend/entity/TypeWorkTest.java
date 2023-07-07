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

import org.junit.jupiter.api.Test;

class TypeWorkTest {

  @Test
  void equals_SameObject_ShouldReturnTrue() {
    //given
    TypeWork tw = new TypeWork();
    tw.setId(1L);
    tw.setName("work");

    //then
    assertEquals(tw, tw);
  }

  @Test
  void equals_NullObject_ShouldReturnFalse() {
    //given
    TypeWork tw = new TypeWork();
    tw.setId(1L);
    tw.setName("work");

    //then
    assertNotEquals(null, tw);
  }

  @Test
  void equals_DifferentClassObject_ShouldReturnFalse() {
    //given
    TypeWork tw = new TypeWork();
    tw.setId(1L);
    tw.setName("work");

    String otherObject = "another_type";

    //then
    assertFalse(tw.equals(otherObject));
  }

  @Test
  void equals_EqualObjects_ShouldReturnTrue() {
    //given
    TypeWork tw1 = new TypeWork();
    tw1.setId(1L);
    tw1.setName("work");

    TypeWork tw2 = new TypeWork();
    tw2.setId(1L);
    tw2.setName("work");

    //then
    assertEquals(tw1, tw2);
  }

  @Test
  void equals_DifferentId_ShouldReturnFalse() {
    //given
    TypeWork tw1 = new TypeWork();
    tw1.setId(1L);
    tw1.setName("work");

    TypeWork tw2 = new TypeWork();
    tw2.setId(2L);
    tw2.setName("work");

    //then
    assertNotEquals(tw1, tw2);
  }

  @Test
  void equals_DifferentName_ShouldReturnFalse() {
    //given
    TypeWork tw1 = new TypeWork();
    tw1.setId(1L);
    tw1.setName("work");

    TypeWork tw2 = new TypeWork();
    tw2.setId(1L);
    tw2.setName("task");

    //then
    assertNotEquals(tw1, tw2);
  }

  @Test
  void hashCode_EqualObjects_ShouldReturnSameHashCode() {
    //given
    TypeWork tw1 = new TypeWork();
    tw1.setId(1L);
    tw1.setName("work");

    TypeWork tw2 = new TypeWork();
    tw2.setId(1L);
    tw2.setName("work");

    //then
    assertEquals(tw1.hashCode(), tw2.hashCode());
  }

  @Test
  void toString_ShouldReturnStringRepresentation() {
    //given
    TypeWork tw = new TypeWork();
    tw.setId(1L);
    tw.setName("work");
    tw.setActive(true);

    String expected = "TypeWork{id=1, name='work', isActive=true}";

    //when
    String actual = tw.toString();

    //then
    assertEquals(expected, actual);
  }
}
