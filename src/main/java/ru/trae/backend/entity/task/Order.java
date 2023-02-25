/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.entity.task;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.trae.backend.entity.user.Customer;
import ru.trae.backend.entity.user.Manager;

/**
 * The Order class represents an order in a customer service system.
 * It contains the customer and manager associated with the order.
 *
 * @author Vladimir Olennikov
 */
@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order extends Task {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;
  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;
  @ToString.Exclude
  @ManyToOne
  @JoinColumn(name = "manager_id", nullable = false)
  private Manager manager;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Order order = (Order) o;
    return Objects.equals(id, order.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
