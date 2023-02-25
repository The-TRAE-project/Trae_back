/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.dto.mapper;

import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.order.OrderDto;
import ru.trae.backend.entity.task.Order;

/**
 * The OrderDtoMapper class is an implementation of the Function interface used to map an
 * Order object to a OrderDto object.
 * It contains a constructor that requires a CustomerDtoMapper and a ManagerDtoMapper which
 * are used to map respective customer and manager objects.
 * The apply() method takes an Order object and maps it to an OrderDto object.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class OrderDtoMapper implements Function<Order, OrderDto> {
  private final CustomerDtoMapper customerDtoMapper;
  private final ManagerDtoMapper managerDtoMapper;

  @Override
  public OrderDto apply(Order o) {
    return new OrderDto(
            o.getId(),
            o.getName(),
            o.getDescription(),
            o.getStartDate(),
            o.getPlannedEndDate(),
            o.getRealEndDate(),
            o.getPeriod(),
            o.isEnded(),
            customerDtoMapper.apply(o.getCustomer()),
            managerDtoMapper.apply(o.getManager())
    );
  }
}
