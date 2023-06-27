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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordGeneratorTest {
  
  @Test
  void emptyConstructor_ThrowsUnsupportedOperationException() {
    assertThrows(UnsupportedOperationException.class, PasswordGenerator::new);
  }
  
  
  @Test
  void generate_ShouldReturnRandomStringOfSpecifiedLength() {
    //given
    int length = 0;
    PasswordGenerator passwordGenerator = new PasswordGenerator.Builder().build();
    
    //when
    String password = passwordGenerator.generate(length);
    
    //then
    assertNotNull(password);
    assertEquals(length, password.length());
  }
  
  
  @Test
  void generate_ShouldReturnEmptyStringWhenLengthIsZero() {
    //given
    int length = 0;
    PasswordGenerator passwordGenerator = new PasswordGenerator.Builder().build();
    
    //when
    String password = passwordGenerator.generate(length);
    
    //then
    assertNotNull(password);
    assertEquals("", password);
  }
  
  @Test
  void generate_ShouldReturnEmptyStringWhenLengthIsNegative() {
    //given
    int length = -5;
    PasswordGenerator passwordGenerator = new PasswordGenerator.Builder().build();
    
    //when
    String password = passwordGenerator.generate(length);
    
    //then
    assertNotNull(password);
    assertEquals("", password);
  }
  
  @Test
  void generate_ShouldReturnRandomStringWithRequiredCharacters() {
    //given
    int length = 10;
    PasswordGenerator passwordGenerator = new PasswordGenerator.Builder()
        .lower(1)
        .upper(1)
        .digits(1)
        .punctuation(1)
        .build();
    
    //when
    String password = passwordGenerator.generate(length);
    
    //then
    assertNotNull(password);
    assertEquals(length, password.length());
    assertTrue(password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%&*+-]).+$"));
  }
  
  @Test
  void digits_ShouldReturnBuilderWithCustomMethodCalledWithDigitsAndZeroMinimumCount() {
    //given
    PasswordGenerator.Builder builder = new PasswordGenerator.Builder();
    
    //when
    PasswordGenerator.Builder result = builder.digits();
    
    //then
    assertEquals(builder, result);
  }
  
  @Test
  void lower_ShouldReturnBuilderWithLowerMethodCalledWithZeroMinimumCount() {
    //given
    PasswordGenerator.Builder builder = new PasswordGenerator.Builder();
    
    //when
    PasswordGenerator.Builder result = builder.lower();
    
    //then
    assertEquals(builder, result);
  }
  
  @Test
  void upper_ShouldReturnBuilderWithUpperMethodCalledWithZeroMinimumCount() {
    //given
    PasswordGenerator.Builder builder = new PasswordGenerator.Builder();
    
    //when
    PasswordGenerator.Builder result = builder.upper();
    
    //then
    assertEquals(builder, result);
  }
}
