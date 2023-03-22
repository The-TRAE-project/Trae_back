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

import java.util.Arrays;
import org.springframework.http.HttpStatus;
import ru.trae.backend.exceptionhandler.exception.ManagerException;

/**
 * Enum representing the different roles a user can have.
 *
 * @author Vladimir Olennikov
 */
public enum Role {
  ROLE_ADMINISTRATOR("Администратор"),
  ROLE_EMPLOYEE("Сотрудник"),
  ROLE_DEVELOPER("Разработчик"),
  ROLE_MANAGER("Конструктор");

  public final String value;

  Role(String value) {
    this.value = value;
  }

  /**
   * Gets the role by its string representation.
   *
   * @param value the string representation
   * @return the role
   * @throws ManagerException when the role is not found
   */
  public static Role getRoleByValue(String value) {
    return Arrays.stream(Role.values())
        .filter(r -> r.value.equals(value))
        .findFirst()
        .orElseThrow(
            () -> new ManagerException(HttpStatus.BAD_REQUEST,
                "Role with value: " + value + " not found"));
  }
}
