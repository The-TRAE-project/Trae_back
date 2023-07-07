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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

class PayloadRandomPieceTest {

  private final Validator validator;

  public PayloadRandomPieceTest() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void setId_AfterSaving_ShouldSetId() {
    //given
    PayloadRandomPiece piece = new PayloadRandomPiece();
    piece.setId(1L);

    //then
    assertNotNull(piece.getId());
    assertTrue(piece.getId() > 0);
  }

  @Test
  void setUsername_ValidUsername_ShouldSetUsername() {
    //given
    String username = "testUser";
    PayloadRandomPiece piece = new PayloadRandomPiece();

    //when
    piece.setUsername(username);

    //then
    assertEquals(username, piece.getUsername());
  }

//  @Test
//  void setUsername_InvalidUsername_ShouldThrowIllegalArgumentException() {
//    //given
//    String username = "ab";
//    PayloadRandomPiece piece = new PayloadRandomPiece();
//
//    //then
//    assertThrows(IllegalArgumentException.class, () -> piece.setUsername(username));
//  }

  @Test
  void payloadRandomPiece_ValidData_ShouldPassValidation() {
    //given
    PayloadRandomPiece piece = new PayloadRandomPiece();
    piece.setUsername("user123");
    piece.setUuid("abc123");

    //when
    Set<ConstraintViolation<PayloadRandomPiece>> violations = validator.validate(piece);

    //then
    assertTrue(violations.isEmpty());
  }

  @Test
  void payloadRandomPiece_InvalidUsername_ShouldFailValidation() {
    //given
    PayloadRandomPiece piece = new PayloadRandomPiece();
    piece.setUsername("u");
    piece.setUuid("abc123");

    //when
    Set<ConstraintViolation<PayloadRandomPiece>> violations = validator.validate(piece);

    //then
    assertEquals(1, violations.size());
    ConstraintViolation<PayloadRandomPiece> violation = violations.iterator().next();
    assertEquals("размер должен находиться в диапазоне от 3 до 15", violation.getMessage());
    assertEquals("username", violation.getPropertyPath().toString());
  }

  @Test
  void payloadRandomPiece_InvalidUuid_ShouldFailValidation() {
    //given
    PayloadRandomPiece piece = new PayloadRandomPiece();
    piece.setUsername("user123");
    piece.setUuid("a".repeat(51));

    //when
    Set<ConstraintViolation<PayloadRandomPiece>> violations = validator.validate(piece);

    //then
    assertEquals(1, violations.size());
    ConstraintViolation<PayloadRandomPiece> violation = violations.iterator().next();
    assertEquals("размер должен находиться в диапазоне от 0 до 50", violation.getMessage());
    assertEquals("uuid", violation.getPropertyPath().toString());
  }
}
