/*
 * Copyright (c) 2023. Vladimir Olennikov.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.trae.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.trae.backend.dto.CustomerDto;
import ru.trae.backend.dto.mapper.OrderDtoMapper;
import ru.trae.backend.dto.order.NewOrderDto;
import ru.trae.backend.dto.order.OrderDto;
import ru.trae.backend.entity.task.Order;
import ru.trae.backend.entity.user.Customer;
import ru.trae.backend.exceptionhandler.exception.OrderException;
import ru.trae.backend.repository.OrderRepository;

/**
 * The OrderService class provides methods for managing orders.
 * It provides methods for creating, retrieving and converting orders.
 *
 * @author Vladimir Olennikov
 */
@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final CustomerService customerService;
  private final ManagerService managerService;
  private final OrderDtoMapper orderDtoMapper;

  /**
   * Receive a new order from a customer.
   *
   * @param dto the NewOrderDto object containing all the necessary information
   * @return the newly created Order object
   */
  public Order receiveNewOrder(NewOrderDto dto) {
    CustomerDto customerDto = dto.customerDto();
    Customer c = customerService.getCustomer(customerDto.firstName(),
                    customerDto.middleName(), customerDto.lastName())
            .orElse(customerService.saveNewCustomer(dto.customerDto()));

    Order order = new Order();
    order.setName(dto.name());
    order.setDescription(dto.description());
    order.setEnded(false);
    order.setPeriod(dto.period());
    order.setStartDate(LocalDateTime.now());
    order.setPlannedEndDate(LocalDateTime.now().plusDays(order.getPeriod()));
    order.setRealEndDate(null);
    order.setManager(managerService.getManagerById(dto.managerId()));
    order.setCustomer(c);

    return orderRepository.save(order);
  }

  /**
   * Retrieve an order by its ID.
   *
   * @param id the ID of the order to retrieve
   * @return the Order object with the given ID
   */
  public Order getOrderById(long id) {
    return orderRepository.findById(id).orElseThrow(
            () -> new OrderException(HttpStatus.NOT_FOUND,
                    "The order with ID: " + id + " not found"));
  }

  /**
   * Retrieve all orders.
   *
   * @return a list of all Order objects
   */
  public List<OrderDto> getAllOrder() {
    return orderRepository.findAll()
            .stream()
            .map(orderDtoMapper)
            .toList();
  }

  /**
   * Convert an order to a dto object based on its ID.
   *
   * @param id the ID of the order to convert
   * @return the OrderDto object for the given order
   */
  public OrderDto convertFromOrderById(long id) {
    return orderDtoMapper.apply(getOrderById(id));
  }

  /**
   * Convert an order to a dto object.
   *
   * @param o the Order object to convert
   * @return the OrderDto object for the given order
   */
  public OrderDto convertFromOrder(Order o) {
    return orderDtoMapper.apply(o);
  }
}
