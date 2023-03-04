/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.entity.user;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Abstract class representing a user.
 *
 * @author Vladimir Olennikov
 */
@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass
public abstract class User {
  @Size(min = 1, max = 100)
  @Column(name = "first_name", columnDefinition = "varchar(100)")
  private String firstName;
  @Size(max = 100)
  @Column(name = "middle_name", columnDefinition = "varchar(100)")
  private String middleName;
  @Size(min = 2, max = 100)
  @Column(name = "last_name", columnDefinition = "varchar(100)")
  private String lastName;
  @Size(min = 7, max = 30)
  @Column(name = "phone", columnDefinition = "varchar(30)")
  private String phone;
  @Column(name = "date_of_register", nullable = false)
  private LocalDateTime dateOfRegister;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(firstName, user.firstName) && Objects.equals(middleName, user.middleName)
            && Objects.equals(lastName, user.lastName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstName, middleName, lastName);
  }
}
