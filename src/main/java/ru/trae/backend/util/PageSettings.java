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

import io.swagger.annotations.ApiParam;
import java.util.Objects;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.data.domain.Sort;

/**
 * Class for pagination settings.
 *
 * @author Vladimir Olennikov
 */
@Data
public class PageSettings {
  @ApiParam(value = "Номер страницы")
  @Min(value = 0, message = "Номер страницы не может быть меньше 0")
  private int page = 0;
  @ApiParam(value = "Элементов на странице")
  @Min(value = 1, message = "Количество элементов на странице не может быть меньше 1")
  private int elementPerPage = 10;
  @ApiParam(value = "Направление сортировки (asc или dsc)", example = "asc или dsc")
  @Pattern(regexp = "(asc|dsc)", message = "Направление сортировки указывается asc либо dsc")
  private String direction = "dsc";
  @ApiParam(value = "Сортировка по названию столбца", example = "id, username, firstName")
  @Pattern(regexp = "[a-z][a-zA-Z0-9]{1,200}", message =
      "Неправильный формат ключа, либо такой ключ отсутствует у запрашиваемого списка")
  private String key = "id";
  
  /**
   * Builds a {@link Sort} instance based on the {@link #direction} and {@link #key} fields.
   * If the {@link #direction} is "dsc", the sort instance will be a descending sort.
   * If the {@link #direction}
   * is "asc", the sort instance will be an ascending sort.
   * If the {@link #direction} is neither of these values,
   * then the sort instance will be a descending sort.
   *
   * @return a {@link Sort} instance based on the {@link #direction} and {@link #key} fields
   */
  public Sort buildSort() {
    if (direction.equals("asc")) {
      return Sort.by(key).ascending();
    }
    return Sort.by(key).descending();
  }
  
  /**
   * This method builds a sort for a manager object.
   *
   * @return The sort for the manager object
   */
  public Sort buildManagerOrEmpSort() {
    Sort sort;
    key = "lastName";
    String nextKey = "firstName";
    if (direction.equals("asc")) {
      sort = Sort.by(key).ascending().and(Sort.by(nextKey).ascending());
    } else {
      sort = Sort.by(key).descending().and(Sort.by(nextKey).descending());
    }
    return sort;
  }
  
  /**
   * This method builds a sort for a type work object.
   *
   * @return The sort for the type work object
   */
  public Sort buildTypeWorkSort() {
    Sort sort;
    key = "name";
    String nextKey = "id";
    if (direction.equals("asc")) {
      sort = Sort.by(key).ascending().and(Sort.by(nextKey).ascending());
    } else {
      sort = Sort.by(key).descending().and(Sort.by(nextKey).descending());
    }
    return sort;
  }
  
  /**
   * This method builds a sort for a project object.
   *
   * @return The sort for the project object
   */
  public Sort buildProjectSort() {
    Sort sort;
    key = "endDateInContract";
    String nextKey = "id";
    if (direction.equals("asc")) {
      sort = Sort.by(key).ascending().and(Sort.by(nextKey).ascending());
    } else {
      sort = Sort.by(key).descending().and(Sort.by(nextKey).descending());
    }
    return sort;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PageSettings that = (PageSettings) o;
    return Objects.equals(direction, that.direction) && Objects.equals(key, that.key);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(direction, key);
  }
}